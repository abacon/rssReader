/**
* 
*/

public class RSSReader {

	private ArrayList<SyndFeedImpl> feeds;

	public RSSReader(ArrayList<SyndEntryImpl> allFeeds) {
		feeds = allFeeds;
	}

	/**
	* does formatting and output and should respond to the following two command line arguments:
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
	* sort date-ically
	*/
	public ArrayList<SyndEntryImpl> sortPostsByDate() {
		return posts;
	}

	public static void main(String[] args) {

	}

}