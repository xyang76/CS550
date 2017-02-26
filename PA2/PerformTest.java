package CS550.iit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import CS550.iit.Query.QueryHitEvent;

/**
 * 
 * @author Xincheng Yang
 * 
 * Use a fake event listener to listen query hit event and count average response data.
 *
 */
public class PerformTest {

	public static void main(String[] args) {
		try {
			//Input essential information
			System.out.println("Please input a file name:");
			Scanner sc = new Scanner(System.in);
			String file = sc.nextLine();
			System.out.println("Please input a local port number:");
			int port = Integer.parseInt(sc.nextLine());
			
			//Start a peer without command line
			Peer p = new Peer();
			p.initSetting();
			Config.load(p, null);
			
			//a simple server thread to accept query hit information
			new Thread(){
				public void run(){
					p.setLocalPort(port);
					try {
						p.startFileShare();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}.start();
			
			//Do query
			for(int i=0; i<200; i++){
				final Date start = new Date();
				final ArrayList<Long> average = new ArrayList<Long>();
				Query q = new Query(p);
				
				/*
				 * Register a fake event listener, since we may have more than 200 data(replicated files and multiple hits)
				 * , we only count the average response time for 200 data.
				 */
				q.regQueryHitListener(new QueryHitEvent() {
					public void onQueryHit() {
						long elapse = (new Date()).getTime() - start.getTime();
						System.out.println("Time elapse: " + elapse);
						average.add(elapse);
						
						if(average.size() == 200){
							long total = 0;
							for(long e : average){
								total += e;
							}
							System.out.println("Average elapse: " + total/200);
						}
					}
				});
				
				q.startQuery(file);
				
				// Wait 10000ms then do a next query.
				Thread.currentThread().sleep(5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
