import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import be.ugent.twijug.jclops.CLManager;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;


/**
 * A class for parsing and outputting RSS subscriptions
 * @author Andrew Bacon, Holly French, Veronica Lynn
 * CS 204, Spring 2013
 * date: 16 April 2013
 */

public class RSSReader {

    private ArrayList<SyndFeedImpl> feeds;
    private static ArgParser argParser;
    private static final String ALPHA = "alpha";
    private static final String DATE = "date";

    public RSSReader(ArrayList<SyndFeedImpl> allFeeds) {
        feeds = allFeeds;
    }


  /**
   * Gets all the posts, sorts them if necessary, and displays the result.
   */
  public void run() {
    // For every feed, fetch posts into some array (local or instance var?)
    // Then sort these posts
    // Then display them according to the flags we've parsed.
  }

    /**
    * Does formatting and output and should respond to the following config options:
    * --number (number of posts)
    * --since (since a date in format yyyy-mm-dd, which is a date object)
    * --title
    * --description
    * --newest (optional)
    * 
    * FEED NAME
(1) story title [tab] publication date [tab] link
[description]
(2) title [tab] publication date [tab] link [tab]
[description]

ANOTHER FEED NAME
(1) story title [tab] publication date [tab] link
[description]
(2) title [tab] publication date [tab] link [tab]
[description]
    */
    public void display() {
        int number = argParser.getNumber();
        Date since = argParser.getSince();
        Pattern title = argParser.getTitle();
        boolean isByDate = argParser.isByDate();
        boolean isByAlpha = argParser.isByAlpha();
        boolean isNewest = argParser.isNewest();
        boolean isDescription = argParser.isDescription();
        
        if (title != null) {
        	// match the regexp and figure out what we want to do here.  do we take into account the number variable here?
        }
        else if (isByDate) {
	        ArrayList<SyndEntryImpl> posts;
	        posts = sortPostsByDate();
		}
		else {
			displayByFeeds(number, since, isByAlpha, isDescription);
		}
        
    }
    
    public void displayByFeeds(int number, Date since, boolean isByAlpha, boolean isDescription) {
    	ArrayList<SyndFeedImpl> curFeeds;
    	if (isByAlpha)
    		curFeeds = sortPostsByAlpha();
    	else
    		curFeeds = feeds;
    	
		for (SyndFeedImpl feed : curFeeds) {
			for (int i = 0; i < number; i++){
				if (feed.getPublishedDate().compareTo(since) >= 0) {
					String feedOutput = feed.getTitle() + "\t" + feed.getPublishedDate() + "\t" + feed.getLink();
					System.out.println(feedOutput);
					if (isDescription) {
						String description = feed.getDescription();
						System.out.println(description);
					}
				}
			}
		}
    }

    /**
    * TODO: double check the object return type
    * Gets all posts from a particular feed and will accept a synd feed impl as a parameter
    * @param SyndFeedImpl curFeed the current feed from which we want to get posts
    * @return an array list of SyndEntry objects, which are the posts
    */
    public ArrayList<SyndEntryImpl> getPostsFromFeed(SyndFeedImpl curFeed) {
        //allPosts = null;
        //return allPosts;
    	return (ArrayList<SyndEntryImpl>) curFeed.getEntries();

    }

    /**
    * Gets all posts from all subscribed feeds.
    * @return ArrayList<SyndEntryImpl> allPosts an array list of all posts
    */
    public ArrayList<SyndEntryImpl> getAllPosts() {
        // worst case we will call this in the constructor to populate in inst var if needed.
    	ArrayList<SyndEntryImpl> allPosts = new ArrayList<SyndEntryImpl>();
    	for (SyndFeedImpl feed : feeds) {
    		ArrayList<SyndEntryImpl> curPosts = getPostsFromFeed(feed);
    		for (SyndEntryImpl post : curPosts) 
    			allPosts.add(post);  		
    	}
    	return allPosts;
    }

    /**
    * Sort posts by a given sort mode (the current options are alphabetically and by date
    * @param String sortMode the mode we are using to sort, either alphabetically or by date (can be expanded later)
    * @return ArrayList<SyndEntryImpl> posts An array of posts sorted according to the mode parameter
    */
    public ArrayList<SyndEntryImpl> sortPosts(String sortMode) {
        // TODO: do we want the functionality of sorting a subset of posts?
        if (sortMode.equals("alpha"))
            sortPostsByAlpha();
        else if (sortMode.equals("date"))
            return sortPostsByDate();
        return null;
    }

    /**
    * This is called by the sortPosts method.  It sorts posts when the mode is alpha; that is, the user wants feeds sorted alphabetically
    * @return ArrayList<SyndEntryImpl> posts The posts sorted alphabetically
    */
    public ArrayList<SyndFeedImpl> sortPostsByAlpha() {
    	ArrayList<SyndFeedImpl> sortedFeeds = new ArrayList<SyndFeedImpl>(feeds);
        Collections.sort(sortedFeeds, new Comparator<SyndFeedImpl>() {
            public int compare(SyndFeedImpl o1, SyndFeedImpl o2) {
                String a = o1.getTitle();
                String b = o2.getTitle();
                return a.compareTo(b);
            }
        });
        return sortedFeeds;
    }

    /**
    * This is called by the sortPosts method.  It sorts posts when the mode is date; that is, the user wants posts sorted chronologically
    * @return ArrayList<SyndEntryImpl> posts The posts sorted by date
    */
    public ArrayList<SyndEntryImpl> sortPostsByDate() {
        ArrayList<SyndEntryImpl> posts = getAllPosts();
        Collections.sort(posts, new Comparator<SyndEntryImpl>() {
            public int compare(SyndEntryImpl o1, SyndEntryImpl o2) {
                Date a = o1.getPublishedDate();
                Date b = o2.getPublishedDate();
                return a.compareTo(b);
            }
        });
        return posts;
    }

    /**
    * Parses the command line arguments provided by the user.  
    * @param String[] args The arguments provided by the suer
    * @author Amy Csizmar Dalal
    */
    public static void parseArguments(String[] args) {
        // This is the object we'll be using
        argParser = new ArgParser();

        // Retrieve and process the command line arguments, setting the appropriate instance variables in test
        CLManager options = new CLManager(argParser);
        options.parse(args);

        // Collect any remaining command line arguments that were not parsed.
        String[] remaining = options.getRemainingArguments();

        // Get the filename out of the remaining options
        if (remaining.length > 0) {
            // Note: we make an assumption here that the first "extraneous" argument is the feed file.
            argParser.setFilename(remaining[0]);
        } else {
            // the program should exit if the feed file is not specified on the command line.
            System.err.println("Error: no input filename specified.");
            System.exit(-1);
        }

        // DEBUG: were the instance variables set correctly?
        System.out.println("Instance variables:");
        System.out.println("Sort by alpha: " + argParser.isByAlpha());
        System.out.println("Date: " + argParser.getSince());
        System.out.println("Number of feeds to list: " + argParser.getNumber());
    }

    public static ArrayList<SyndFeedImpl> getSyndFeedsFromFile(String filename) {
    	FileParser fp = new FileParser();
    	ArrayList<String> urls = fp.getLines(argParser.getFilename());
    	ArrayList<SyndFeedImpl> feeds = null;
    	for (String url : urls) {
    		feeds.add(makeSyndFeedImplFromUrl(url));
    	}
    }
    
    public static SyndFeedImpl makeSyndFeedImplFromUrl(String) {
    	
    }

   /** Instantiates a new RSSReader, calls it with the arguments from
   * the command line.
   *
   *
   * See: http://grepcode.com/file/repo1.maven.org/maven2/org.rometools/rome-fetcher/1.2/org/rometools/fetcher/samples/FeedReader.java
   */
    public static void main(String[] args) {
        parseArguments(args);
        // Parse the file. Return some array, FeedUrls.
        ArrayList<SyndFeedImpl> feeds = getSyndFeedsFromFile(argParser.getFilename());
        RSSReader reader = new RSSReader(feeds);
        // or like:
        // reader.set_sort('name');
        // reader.run('args');
    }

}
