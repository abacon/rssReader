import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		this.lastRun = getLastRun();
	}

	public void setArgParser(ArgParser argParser) {
		this.argParser = argParser;
	}

	public ArgParser getArgParser() {
		return this.argParser;
	}

	public void setFeeds(ArrayList<SyndFeedImpl> feeds) {
		this.feeds = feeds;
	}

	public ArrayList<SyndFeedImpl> getFeeds() {
		return this.feeds;
	}

	/**
	 * This parses a date file into a date object so we know when we last ran
	 * the parser.
	 * 
	 * @return date at which the feed reader was last run
	 */
	public Date getLastRun() {
		BufferedReader br = null;
		try {
			String sCurrentLine;

			br = new BufferedReader(new FileReader(LAST_RUN_FILE));

			while ((sCurrentLine = br.readLine()) != null) {
				String date = sCurrentLine;
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					return df.parse(date);
				} catch (ParseException e) {
					return new Date(0);
				}
			}

		} catch (IOException e) {
			return new Date(0);
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				return new Date(0);
			}
		}
		return new Date(0);
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	/**
	 * Does formatting and output and should respond to the following config
	 * options: --number (number of posts) --since (since a date in format
	 * yyyy-mm-dd, which is a date object) --title --description --newest
	 * (optional)
	 */
	public void display() {
		argParser = this.getArgParser();
		int number = argParser.getNumber();
		Date since = argParser.getSince();
		Pattern title = argParser.getTitle();
		boolean isByDate = argParser.isByDate();
		boolean isByAlpha = argParser.isByAlpha();
		boolean isNewest = argParser.isNewest();
		boolean isDescription = argParser.isDescription();

		if (title != null) {
			displayByTitle(number, since, isByAlpha, isByDate, isDescription,
					title, isNewest);
		} else if (isByDate) {
			displayByDate(number, since, isDescription, isNewest);
		} else {
			displayByFeeds(number, since, isByAlpha, isDescription, isNewest);
		}

		writeTimeToFile();

	}

	/**
	 * displayByFeeds is called by display, which is the default display setting
	 * if no date or title arguments are provided.
	 * 
	 * @param number
	 *            Number of articles to be displayed
	 * @param since
	 *            Earliest date from which an article can be displayed
	 * @param isByAlpha
	 *            Determines whether the display should be alphabetically by
	 *            title
	 * @param isDescription
	 *            Determines whether to print the articles description too
	 */
	public void displayByFeeds(int number, Date since, boolean isByAlpha,
			boolean isDescription, boolean isNewest) {
		ArrayList<SyndFeedImpl> curFeeds;
		if (isByAlpha)
			curFeeds = sortPostsByAlpha();
		else
			curFeeds = this.getFeeds();

		for (SyndFeedImpl feed : curFeeds) {
			System.out.println(feed.getTitle().toUpperCase());
			int articleNum = 1;
			for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
				SyndEntryImpl entry = (SyndEntryImpl) i.next();
				String entrydate;
				if (entry.getPublishedDate() != null)
					entrydate = entry.getPublishedDate().toString();
				else
					entrydate = "";

				/*
				 * we only print if the date is correct: if we want the newest,
				 * we only print the articles that happened after it was last
				 * run if we are imposing the since condition, we only print
				 * articles after the specified date and time. we also include
				 * error handling since apparently not all articles have a date.
				 */
				if (isPrintable(isNewest, entry, since)) {
					System.out.println("(" + articleNum + ")"
							+ entry.getTitle() + "\t" + entrydate + "\t"
							+ entry.getLink());
					if (isDescription && entry.getDescription() != null) {
						System.out.println(entry.getDescription().getValue());
					}
					articleNum++;
					if (articleNum > number)
						break;
				}

			}
			System.out.println();
		}
	}

	/**
	 * displayByFeeds is called by display, which displays articles by date
	 * rather than by news source.
	 * 
	 * @param number
	 *            Number of articles to be displayed
	 * @param since
	 *            Earliest date from which an article can be displayed
	 * @param isDescription
	 *            Determines whether a description is included with the article
	 */
	public void displayByDate(int number, Date since, boolean isDescription,
			boolean isNewest) {
		ArrayList<SyndEntryImpl> posts;
		posts = sortPostsByDate();

		int articleNum = 1;
		for (int i = 0; i < number; i++) {
			SyndEntryImpl post = posts.get(i);
			/*
			 * we only print if the date is correct: if we want the newest, we
			 * only print the articles that happened after it was last run if we
			 * are imposing the since condition, we only print articles after
			 * the specified date and time. we also include error handling since
			 * apparently not all articles have a date.
			 */
			if (isPrintable(isNewest, post, since)) {
				String date = post.getPublishedDate() != null ? post
						.getPublishedDate().toString() : "";
						String feedOutput = "(" + articleNum + ")" + post.getTitle()
								+ "\t" + date + "\t" + post.getLink();
						System.out.println(feedOutput);
						if (isDescription && post.getDescription() != null) {
							System.out.println(post.getDescription().getValue());
						}
						articleNum++;
			}
		}
	}

	/**
	 * displayByTitle displays articles that match a given regular expression
	 * for a title
	 * 
	 * @param number
	 *            Number of posts
	 * @param since
	 *            Earliest date from which an article can be displayed
	 * @param isByAlpha
	 *            Determines whether we sort the titles alphabetically
	 * @param isDescription
	 *            Determines whether we show the article description
	 * @param title
	 *            The pattern we are using to match article titles
	 */
	public void displayByTitle(int number, Date since, boolean isByAlpha,
			boolean isByDate, boolean isDescription, Pattern title,
			boolean isNewest) {

		// if we want to display by title and by date
		if (isByDate) {
			ArrayList<SyndEntryImpl> posts;
			posts = sortPostsByDate();

			int articleNum = 1;
			for (int i = 0; i < number; i++) {
				SyndEntryImpl post = posts.get(i);
				/*
				 * we only print if the date is correct: if we want the newest,
				 * we only print the articles that happened after it was last
				 * run if we are imposing the since condition, we only print
				 * articles after the specified date and time. we also include
				 * error handling since apparently not all articles have a date.
				 */
				if (isPrintable(isNewest, post, since)) {
					Matcher matcher = title.matcher(post.getTitle());
					if (matcher.find()) {
						String date = post.getPublishedDate() != null ? post
								.getPublishedDate().toString() : "";
								String feedOutput = "(" + articleNum + ")"
										+ post.getTitle() + "\t" + date + "\t"
										+ post.getLink();
								System.out.println(feedOutput);
								if (isDescription && post.getDescription() != null) {
									System.out
									.println(post.getDescription().getValue());
								}
								articleNum++;
					}
				}
			}
		}

		// if we do not care about date
		else {
			ArrayList<SyndFeedImpl> curFeeds;
			if (isByAlpha)
				curFeeds = sortPostsByAlpha();
			else
				curFeeds = feeds;

			int articleNum = 1;
			for (SyndFeedImpl feed : curFeeds) {
				for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
					SyndEntryImpl entry = (SyndEntryImpl) i.next();
					/*
					 * we only print if the date is correct: if we want the
					 * newest, we only print the articles that happened after it
					 * was last run if we are imposing the since condition, we
					 * only print articles after the specified date and time. we
					 * also include error handling since apparently not all
					 * articles have a date.
					 */
					if (isPrintable(isNewest, entry, since)) {
						Matcher matcher = title.matcher(entry.getTitle());
						if (matcher.find()) {
							String date = entry.getPublishedDate() != null ? entry
									.getPublishedDate().toString() : "";
									System.out.println("(" + articleNum + ")"
											+ entry.getTitle() + "\t" + date + "\t"
											+ entry.getLink());
									if (isDescription && entry.getDescription() != null) {
										System.out.println(entry.getDescription()
												.getValue());
									}
									articleNum++;
									if (articleNum > number)
										break;
						}
					}
				}
			}
		}

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
	 * This is called by the sortPosts method. It sorts posts when the mode is
	 * alpha; that is, the user wants feeds sorted alphabetically
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
				return a.compareTo(b);
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
		// This is the object we'll be using
		argParser = new ArgParser();

		// Retrieve and process the command line arguments, setting the
		// appropriate instance variables in test
		CLManager options = new CLManager(argParser);
		options.parse(args);

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
		if (remaining.length > 0) {
			// Note: we make an assumption here that the first "extraneous"
			// argument is the feed file.
			argParser.setFilename(remaining[0]);
		} else {
			// the program should exit if the feed file is not specified on the
			// command line.
			System.err.println("Error: no input filename specified.");
			System.exit(-1);
		}
		this.setArgParser(argParser);
	}

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
			fop = new FileOutputStream(file);

			if (!file.exists()) {
				file.createNewFile();
			}

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

	public boolean isPrintable(boolean isNewest, SyndEntryImpl entry, Date since) {
		return !isNewest
				&& ((entry.getPublishedDate() != null ? entry
						.getPublishedDate().after(since) : true) || (isNewest && entry
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
		String urlFile = null;

		reader.parseArguments(args);
		urlFile = reader.getArgParser().getFilename();
		ArrayList<SyndFeedImpl> feeds = reader.getSyndFeedsFromFile(urlFile);
		reader.setFeeds(feeds);

		reader.display();
	}

}
