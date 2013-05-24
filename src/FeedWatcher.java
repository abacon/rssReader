import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;




/**
 * <code>FeedWatcher</code> takes an <code>ArrayList&lt;SyndFeedImpl&gt;</code> and periodically checks for 
 * posts which have a publish date since the last time it checked. It then
 * notifies all observers.
 * 
 * Simple usage: 
 * <pre>
 * FeedWatcher feedWatcher = new FeedWatcher(feeds);
 * feedWatcher.start();
 * FeedObserver alertNoiseObserver = new AlertNoiseObserver();
 * feedWatcher.addObserver(alertNoiseObserver);
 * try {
 *	feedWatcher.join();
 * } catch (InterruptedException e) {
 *	e.printStackTrace();
 * }
 * </pre>
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
				Thread.sleep(15000);
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
