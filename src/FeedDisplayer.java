import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;




/**
 * A class for handling the RSSReader's output to terminal
 * 
 * @author Andrew Bacon, Holly French, Veronica Lynn CS 204, Spring 2013 date:
 *         16 April 2013
 */
public class FeedDisplayer {

	private ArgParser argParser;
	private Date lastRun;
	private static final String TERMINAL_BLACK = "\033[0m";
	private static final String TERMINAL_BLUE = "\033[34m";
	private static final String TERMINAL_BOLD = "\033[1m";
	private static final String TERMINAL_GRAY = "\033[0;37m";
	
	public FeedDisplayer(ArgParser argParser, Date lastRun) {
		this.argParser = argParser;
		this.lastRun = lastRun;
	}

	/**
	 * displayByDate is called by RSSReader.display()
	 * Displays articles by date rather than by news source.
	 * 
	 * @param posts sorted list of posts to display
	 */
	public void displayByDate(ArrayList<SyndEntryImpl> posts) {		
		int number = argParser.getNumber();
		Date since = argParser.getSince();
		int articleNum = 1;
		for (int i = 0; i < number; i++) {
			SyndEntryImpl entry = posts.get(i);
			
			String entrydate;
			if (entry.getPublishedDate() != null)
				entrydate = entry.getPublishedDate().toString();
			else
				entrydate = "";
			
			if (isPrintable(entry, since)) {
				printEntry(entry, articleNum, entrydate);
				articleNum++;
			}
		}
	}
	
	
	/**
	 * displayByFeeds is called by RSSReader.display()
	 * Given a list of feeds, displays the posts from each feed
	 * in the order provided
	 * 
	 * @param curFeeds the list of feeds to display
	 */
	public void displayByFeeds(ArrayList<SyndFeedImpl> curFeeds) {
		int number = argParser.getNumber();
		Date since = argParser.getSince();
		for (SyndFeedImpl feed : curFeeds) {
			System.out.println(TERMINAL_BLUE + feed.getTitle().toUpperCase() + TERMINAL_BLACK);
			int articleNum = 1;
			for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
				SyndEntryImpl entry = (SyndEntryImpl) i.next();
				
				String entrydate;
				if (entry.getPublishedDate() != null)
					entrydate = entry.getPublishedDate().toString();
				else
					entrydate = "";

				if (isPrintable(entry, since)) {
					printEntry(entry, articleNum, entrydate);
					articleNum++;
					if (articleNum > number)
						break;
				}

			}
			System.out.println();
		}
	}
	
	/**
	 * Prints an entry to the terminal
	 * 
	 * @param entry The entry to display
	 * @param articleNum The article number (number per feed)
	 * @param entrydate The date of the entry (empty string if date doesn't exist)
	 */
	public void printEntry(SyndEntryImpl entry, int articleNum, String entrydate) {
		System.out.println("(" + articleNum + ") "
				+ TERMINAL_BOLD + entry.getTitle() + TERMINAL_BLACK + "\t" + entrydate + "\t" + 
				TERMINAL_GRAY + entry.getLink() + TERMINAL_BLACK
				);
		if (argParser.isDescription() && entry.getDescription() != null) {
			System.out.println(this.stripHTML(entry.getDescription().getValue()));
		}
	}
	
	/**
	 * Determines whether a given entry should be printed or not
	 * Based on REGEX title matching and the --since and --newest flags
	 * 
	 * @param entry the entry to check
	 * @param since only display posts after this date
	 * @return true if entry should be printed, else false
	 */
	public boolean isPrintable(SyndEntryImpl entry, Date since) {
		boolean isNewest = argParser.isNewest();
		boolean matchesTitle = matchesTitleRegex(entry);

		return matchesTitle && ((!isNewest
				&& ((entry.getPublishedDate() != null ? entry
						.getPublishedDate().after(since) : true)) || (isNewest && entry
								.getPublishedDate().after(lastRun))));
	}
	
	/**
	 * Checks whether an entry title matches the title regex
	 * 
	 * @param entry The entry to check
	 * @return True if the regex matches or if regex is unspecified, else false
	 */
	public boolean matchesTitleRegex(SyndEntryImpl entry) {
		Pattern title = argParser.getTitle();
		if (title != null) {
			Matcher matcher = title.matcher(entry.getTitle());
			return matcher.find();
		}
		return true;
	}
	
	public String stripHTML(String html) {
	    return Jsoup.parse(html).text();
	}
	
}
