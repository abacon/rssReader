import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * TitleAndDescObserver displays the title and description of new posts when they are detected.
 * Use it by adding it to a FeedWatcher instance.
 * 
 * @see FeedWatcher#addObserver(FeedObserver)
 */
public class TitleAndDescObserver extends FeedObserver {

	private static final String TERMINAL_BLACK = "\033[0m";
	private static final String TERMINAL_BOLD = "\033[1m";
	private Date lastRunDate;
	
	/**
	* Notify the observer that a feed has been updated
	*
	* @param changedFeeds   the list of feeds that have updated
	* @param lastRunDAte    when the FeedWatcher last checked for updates
	*/
	@Override
	public void notify(ArrayList<SyndFeedImpl> changedFeeds, Date lastRunDate) {
		this.lastRunDate = lastRunDate;
		displayChangedFeeds(changedFeeds);
	}

    /**
    * Display the feeds that have changed since the last time notify was called
    * 
    * @param changedFeeds   The list of foods to display
    */
	protected void displayChangedFeeds(ArrayList<SyndFeedImpl> changedFeeds) {
		
		for (SyndFeedImpl feed : changedFeeds) {
			for (SyndEntryImpl entry : (ArrayList<SyndEntryImpl>) feed.getEntries())
				if (entry.getPublishedDate() != null && entry.getPublishedDate().after(lastRunDate))
					printEntry(entry);
		}
	}
	
	/**
	* Display a single entry, properly formatted
	*/
	protected void printEntry(SyndEntryImpl entry) { 
		System.out.println(TERMINAL_BOLD + entry.getTitle());
		System.out.println(TERMINAL_BLACK + stripHTML(entry.getDescription().getValue()));
	}
	
	public String stripHTML(String html) {
	    return Jsoup.parse(html).text();
	}
	
}
