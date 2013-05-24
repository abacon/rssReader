import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;


public class TitleAndDescObserver extends FeedObserver {

	private static final String TERMINAL_BLACK = "\033[0m";
	private static final String TERMINAL_BOLD = "\033[1m";
	private Date lastRunDate;
	
	@Override
	public void notify(ArrayList<SyndFeedImpl> changedFeeds, Date lastRunDate) {
		this.lastRunDate = lastRunDate;
		displayChangedFeeds(changedFeeds);
	}

	protected void displayChangedFeeds(ArrayList<SyndFeedImpl> changedFeeds) {
		
		for (SyndFeedImpl feed : changedFeeds) {
			for (SyndEntryImpl entry : (ArrayList<SyndEntryImpl>) feed.getEntries())
				if (entry.getPublishedDate() != null && entry.getPublishedDate().after(lastRunDate))
					printEntry(entry);
		}
	}
	
	protected void printEntry(SyndEntryImpl entry) { 
		System.out.println(TERMINAL_BOLD + entry.getTitle());
		System.out.println(TERMINAL_BLACK + stripHTML(entry.getDescription().getValue()));
	}
	
	public String stripHTML(String html) {
	    return Jsoup.parse(html).text();
	}
	
}
