import java.util.ArrayList;
import java.util.Date;
import com.sun.syndication.feed.SyndFeedImp;


/**
* 
*/

public class RSSReader {

	private ArrayList<SyndEntryImpl> feeds;

	public RSSReader(ArrayList<SyndEntryImpl> allFeeds) {
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
	* does formatting and output and should respond to the following config options:
	* --number (number of posts)
	* --since (since a date in format yyyy-mm-dd, which is a date object)
	* --title
	* --description
	* --newest (optional)
	*/
	public void display() {
		System.out.println();
	}

	/**
	* TODO: double check the object return type
	* gets all posts from a particular feed and will accept a synd feed impl as a parameter
	*/
	public ArrayList<SyndEntryImpl> getPostsFromFeed(SyndFeedImpl curFeed) {
		allPosts = null;
		return allPosts;

	}

	/**
	* gets all posts from a particular feed and will accept a synd feed impl as a parameter
	*/
	public ArrayList<SyndEntryImpl> getAllPosts() {
		// worst case we will call this in the constructor to populate in inst var if needed.

	}

	/**
	* sort posts
	*/
	public ArrayList<SyndEntryImpl> sortPosts(String sortMode) {
		// TODO: do we want the functionality of sorting a subset of posts?
		return posts;
	}

	/**
	* don't need a param because it's sorting alphabetically
	*/
	public ArrayList<SyndEntryImpl> sortPostsByAlpha() {
		return posts;
	}

	/**
	* sort chronologically
	*/
	public ArrayList<SyndEntryImpl> sortPostsByDate() {
		return posts;
	}

  /**
   * Instantiates a new RSSReader, calls it with the arguments from
   * the command line.
   *
   *
   * See: http://grepcode.com/file/repo1.maven.org/maven2/org.rometools/rome-fetcher/1.2/org/rometools/fetcher/samples/FeedReader.java
   */
	public static void main(String[] args) {
    // Parse the file. Return some array, FeedUrls.
    ArrayList<String> FeedUrls = null;
    RSSReader reader = RSSReader(
	}

}
