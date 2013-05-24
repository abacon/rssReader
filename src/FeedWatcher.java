import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;




/**
 * @author abacon
 *
 */
public class FeedWatcher extends Thread {
	
	protected ArrayList<FeedObserver> observers = new ArrayList<FeedObserver>();
	protected ArrayList<SyndFeedImpl> feedsToWatch;
	protected Date lastRunDate;
	
	public FeedWatcher(ArrayList<SyndFeedImpl> someFeeds) {
		this.feedsToWatch = someFeeds;
		this.lastRunDate = new Date(); 
	}
	
	public Date getLastRunDate() {
		return lastRunDate;
	}
	
	public void addObserver(FeedObserver observer) {
		observers.add(observer);
	}
	
	protected ArrayList<SyndFeedImpl> changedFeeds() {
		ArrayList<SyndFeedImpl> changedFeeds = new ArrayList<SyndFeedImpl>();
		
		for (SyndFeedImpl feed : feedsToWatch) {
			if (feedHasChanged(feed))
				changedFeeds.add(feed);
		}
		
		return changedFeeds;
	}
	
	
	// TODO: This method needs access to the original feeds in order to make a comparison.
	//       Alternatively, we pass in a current Date when the thread is created, which is then updated every run.
	protected boolean feedHasChanged(SyndFeedImpl feed) {
		
		List<SyndEntryImpl> entries = feed.getEntries();
		
		for (SyndEntryImpl entry : entries) {
			Date pubDate = entry.getPublishedDate();
			if (pubDate != null && pubDate.after(this.lastRunDate))
				return true;
		}
		
		return false;
	}
	
	public void run() {
		while(true) {
			try {
				//Thread.sleep(120000);
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("Something bad happened! Exiting. ");
				e.printStackTrace();
			}
			
			ArrayList<SyndFeedImpl> changedFeeds = changedFeeds();
	
			if (changedFeeds.size() != 0)
				notifyObservers(changedFeeds);

			lastRunDate = new Date();
		}
		
	}
	
	protected void notifyObservers(ArrayList<SyndFeedImpl> changedFeeds) {
		for (FeedObserver observer : observers) {
			observer.notify(changedFeeds, lastRunDate);
		}
	}
	
}
