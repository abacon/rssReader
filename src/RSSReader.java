import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import be.ugent.twijug.jclops.CLManager;
import be.ugent.twijug.jclops.CLParseException;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * A class for parsing and outputting RSS subscriptions
 * 
 * @author Andrew Bacon, Holly French, Veronica Lynn CS 204, Spring 2013 date:
 *         16 April 2013
 */

public class RSSReader {

	private ArrayList<SyndFeedImpl> feeds;
	private ArgParser argParser;
	private Date lastRun;
	private static final String LAST_RUN_FILE = "data/lastRun.txt";

	public RSSReader() {
		lastRun = getLastRun();
	}

	public void setArgParser(ArgParser argParser) {
		this.argParser = argParser;
	}

	public ArgParser getArgParser() {
		return argParser;
	}

	public void setFeeds(ArrayList<SyndFeedImpl> feeds) {
		this.feeds = feeds;
	}

	public ArrayList<SyndFeedImpl> getFeeds() {
		return feeds;
	}

	/**
	 * This parses a date file into a date object so we know when we last ran
	 * the parser.
	 * 
	 * @return date at which the feed reader was last run
	 */
	public Date getLastRun() {

			Scanner file;
			try {
				file = new Scanner(new File(LAST_RUN_FILE));
			} catch (FileNotFoundException e1) {
				System.out.println("Warning: Last run file does not exist.");
				return new Date(0);
			}
			if (file.hasNextLine()) {
				String date = file.nextLine();
				file.close();
				
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				try {
					return df.parse(date);
				} catch (ParseException e) {
					return new Date(0);
				}
			} else {
				file.close();
				return new Date(0);
			}
			
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	/**
	 * Displays the feeds/posts depending on user specified input:
	 * --by-date displays the posts from newest to oldest
	 * --by-alpha displays feeds in alphabetical order
	 * If neither of those flags are active, defaults to displaying
	 * feeds in order given in URL file
	 */
	public void display() {
		argParser = getArgParser();
		boolean isByDate = argParser.isByDate();
		boolean isByAlpha = argParser.isByAlpha();
		
		FeedDisplayer displayer = new FeedDisplayer(argParser, lastRun);

		if (isByAlpha) {
			ArrayList<SyndFeedImpl> feeds = sortPostsByAlpha();
			displayer.displayByFeeds(feeds);
		} else if (isByDate) {
			ArrayList<SyndEntryImpl> posts = sortPostsByDate();
			displayer.displayByDate(posts);
		} else {
			displayer.displayByFeeds(getFeeds());
		}

		writeTimeToFile();
	}

	


	/**
	 * Gets the posts from the feed.
	 * 
	 * @param curFeed
	 *            the current feed from which we want to get posts
	 * @return an array list of SyndEntry objects, which are the posts
	 */
	public List<SyndEntryImpl> getPostsFromFeed(SyndFeedImpl curFeed) {
		return curFeed.getEntries();

	}

	/**
	 * Gets all posts from all subscribed feeds.
	 * 
	 * @return allPosts an array list of all posts
	 */
	public ArrayList<SyndEntryImpl> getAllPosts() {
		ArrayList<SyndEntryImpl> allPosts = new ArrayList<SyndEntryImpl>();
		for (SyndFeedImpl feed : feeds) {
			List<SyndEntryImpl> curPosts = getPostsFromFeed(feed);
			for (SyndEntryImpl post : curPosts)
				allPosts.add(post);
		}
		return allPosts;
	}

	/**
	 * This sorts posts when the mode is alpha; that is, when
	 *  the user wants feeds sorted alphabetically
	 * 
	 * @return posts The posts sorted alphabetically
	 */
	public ArrayList<SyndFeedImpl> sortPostsByAlpha() {
		ArrayList<SyndFeedImpl> sortedFeeds = new ArrayList<SyndFeedImpl>(feeds);
		Collections.sort(sortedFeeds, new Comparator<SyndFeedImpl>() {
			@Override
			public int compare(SyndFeedImpl o1, SyndFeedImpl o2) {
				String a = o1.getTitle();
				String b = o2.getTitle();
				return a.compareToIgnoreCase(b);
			}
		});
		return sortedFeeds;
	}

	/**
	 * This is called by the sortPosts method. It sorts posts when the mode is
	 * date; that is, the user wants posts sorted chronologically
	 * 
	 * @return posts The posts sorted by date
	 */
	public ArrayList<SyndEntryImpl> sortPostsByDate() {
		ArrayList<SyndEntryImpl> posts = getAllPosts();
		Collections.sort(posts, new Comparator<SyndEntryImpl>() {
			@Override
			public int compare(SyndEntryImpl o1, SyndEntryImpl o2) {
				Date a;
				Date b;
				// error handling: there are cases where posts do not have dates
				try {
					a = o1.getPublishedDate();
				} catch (Exception e) {
					a = new Date(Long.MIN_VALUE);
				}
				try {
					b = o2.getPublishedDate();
				} catch (Exception e) {
					b = new Date(Long.MIN_VALUE);
				}
				if (a == null)
					a = new Date(Long.MIN_VALUE);
				if (b == null)
					b = new Date(Long.MIN_VALUE);
				return a.compareTo(b);
			}
		});
		Collections.reverse(posts);
		return posts;
	}

	/**
	 * Parses the command line arguments provided by the user.
	 * 
	 * @param args
	 *            The arguments provided by the suer
	 */
	public void parseArguments(String[] args) {
		argParser = new ArgParser();

		// Retrieve and process the command line arguments, setting the
		// appropriate instance variables in ArgParser
		CLManager options = new CLManager(argParser);

		try {
			options.parse(args);
		} catch (CLParseException ex) {
			System.out.println(ex);
			System.out.println(options.getUsageMessage());
		}

		if (this.getArgParser().showHelp()) {
			System.err.println(options.getUsageMessage());
			System.exit(1);
		}
		// Collect any remaining command line arguments that were not parsed.
		String[] remaining = options.getRemainingArguments();

		// Get the filename out of the remaining options
		// Note: we make an assumption here that the first "extraneous"
		// argument is the feed file.
		if (remaining.length > 0) {
			argParser.setFilename(remaining[0]);
		} else {
			System.err.println("Error: no input filename specified.");
			System.exit(-1);
		}
		this.setArgParser(argParser);
	}

	/**
	 * Given the path to the URL file, parse URLS into feeds
	 * @param filename URL file path
	 * @return list of feeds
	 */
	public ArrayList<SyndFeedImpl> getSyndFeedsFromFile(String filename) {
		FileParser fp = new FileParser();
		ArrayList<String> urls = fp.getLines(filename);
		ArrayList<SyndFeedImpl> feeds = new ArrayList<SyndFeedImpl>();
		for (String url : urls) {
			try {
				feeds.add(makeSyndFeedImplFromUrl(url));
			} catch (MalformedURLException e) {
				System.out
				.println("Warning: " + url + " is not a valid URL.\n");
			} catch (IOException e) {
				System.out.println("Warning: URL " + url
						+ " could not be read.\n");
			} catch (FeedException e) {
				System.out.println("Warning: Feed at URL " + url
						+ " could not be parsed.\n");
			}
		}
		return feeds;
	}

	/**
	 * Given a URL, parse the associated feed
	 * 
	 * @param url The URL to parse
	 * @return the parsed feed
	 * @throws IllegalArgumentException
	 * @throws FeedException
	 * @throws IOException
	 */
	public SyndFeedImpl makeSyndFeedImplFromUrl(String url)
			throws IllegalArgumentException, FeedException, IOException {
		URL feedSource = new URL(url);
		SyndFeedInput input = new SyndFeedInput();
		SyndFeedImpl feed = (SyndFeedImpl) input
				.build(new XmlReader(feedSource));
		return feed;
	}

	/**
	 * This writes the current time out to the date file so we can keep track of
	 * when this was last run.
	 */
	public void writeTimeToFile() {
		FileOutputStream fop = null;
		File file;
		Date date = new Date();
		String content = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		.format(date);
		try {

			file = new File(LAST_RUN_FILE);

			if (!file.exists()) {
				file.createNewFile();
			}
			
			fop = new FileOutputStream(file);


			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Determines whether a given entry should be printed or not
	 * @param isNewest 
	 * @param entry
	 * @param since
	 * @return true if entry should be printed, else false
	 */
	public boolean isPrintable(boolean isNewest, SyndEntryImpl entry, Date since) {
		return (!isNewest
				&& ((entry.getPublishedDate() != null ? entry
						.getPublishedDate().after(since) : true)) || (isNewest && entry
								.getPublishedDate().after(lastRun)));
	}

	/**
	 * Instantiates a new RSSReader, calls it with the arguments from the
	 * command line.
	 * 
	 * 
	 * See: http://grepcode.com/file/repo1.maven.org/maven2/org.rometools/rome-
	 * fetcher/1.2/org/rometools/fetcher/samples/FeedReader.java
	 */
	public static void main(String[] args) {
		RSSReader reader = new RSSReader();

		reader.parseArguments(args);
		String urlFile = reader.getArgParser().getFilename();
		ArrayList<SyndFeedImpl> feeds = reader.getSyndFeedsFromFile(urlFile);
		reader.setFeeds(feeds);

		reader.display();
		FeedWatcher feedWatcher = new FeedWatcher(feeds);
		feedWatcher.start();
		if (reader.getArgParser().getChime()) {
			FeedObserver alertNoiseObserver = new AlertNoiseObserver();
			feedWatcher.addObserver(alertNoiseObserver);
		}
		if (reader.getArgParser().getUpdate()) {
			FeedObserver titleAndDescObserver = new TitleAndDescObserver();
			feedWatcher.addObserver(titleAndDescObserver);
		}
		try {
			feedWatcher.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
