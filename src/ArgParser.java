import be.ugent.twijug.jclops.annotations.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * A class to parse the command line arguments for the RSSReader. 
 * 
 * Derived from code by
 * @author Amy Csizmar Dalal
 * CS 204, Spring 2013
 * date: 9 April 2013
 */

@AtMostOneOf({"by-date", "by-alpha"})
public class ArgParser {
	// These instance variables are derived from the command line arguments passed in.
	
	/** Flag to display story links in order of date posted */
	private boolean byDate;
	
	/** Flag to display story links with feeds ordered alphabetically */
	private boolean byAlpha;
	
	/** Flag to display only stories posted or updated since the last time the feed parser was run */
	private boolean newest;
	
	/** Flag to also display the description associated with a story */
	private boolean description;
	
	/** The number of stories per feed to list */
	private int number;

	/** Display only story links published on or after this date */
	private Date since;

	/** A regex to find matching titles */
	private Pattern title;

	/** The file containing the list of feeds */
	private String filename;

	/** Whether or not to display a usage message */
	private boolean showHelp;
	
	private boolean update;
	
	private boolean chime;

	/**
	 * The constructor sets default values for the instance variables, in case these are
	 * not set by command line options.
	 */
	public ArgParser() {
		byDate = false;
		byAlpha = false;
		newest = false;
		description = false;
		number = 2;
		since = new Date(0);
		title = null;
		filename = null;
		chime = false;
		update = false;
	}

	/**
	 * Retrieves the current value of the "sort by date" flag.
	 * @return true if we should sort the posts by date, false otherwise
	 */
	public boolean isByDate() {
		return byDate;
	}

	/**
	 * Turn on the "sort by date" flag.
	 * Automatically turns off "sort by alpha" flag.
	 */
	@Option(description="Sort posts by date.",shortName='d')
	public void setByDate() {
		this.byDate = true;
		this.byAlpha = false;
	}

	/**
	 * Retrieves the current value of the "sort by alpha" flag.
	 * @return true if we should sort the posts alphabetically, false otherwise
	 */
	public boolean isByAlpha() {
		return byAlpha;
	}

	/**
	 * Turn on the "sort by alpha" flag.
	 * Automatically turns off the "sort by date" flag.
	 */
	@Option(description="Show feeds in alphabetical order.")
	public void setByAlpha() {
		this.byAlpha = true;
		this.byDate = false;
	}

	/**
	 * Retrieves the current value of the "show newest" flag.
	 * @return true if we should only display the stories posted or updated since the last time the feed parser was run, false otherwise
	 */
	public boolean isNewest() {
		return newest;
	}

	/**
	 * Turn on the "show newest" flag.
	 */
	@Option(description="Show only those items which are new since your last use of RSSReader.",shortName='n')
	public void setNewest() {
		this.newest = true;
	}

	/**
	 * Retrieves the current value of the "show description" flag.
	 * @return true if we should display the post's description, false otherwise
	 */
	public boolean isDescription() {
		return description;
	}

	/**
	 * Turn on the "show description" flag.
	 */
	@Option(description="Show a description under each post.",shortName='D')
	public void setDescription() {
		this.description = true;
	}

	/**
	 * Returns the number of posts per feed to display.
	 * @return the number of posts to display
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number of posts per feed to display
	 * @param number the number of posts
	 */
	@Option(description="<N> the number of posts per feed to display.", shortName='N')
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the date for which to display posts published on or after.
	 * @return the date for which to display posts published on or after
	 */
	public Date getSince() {
		return since;
	}

	/**
	 * Sets the date for which to display posts published on or after
	 * @param since the date for which to display posts published on or after
	 */
	@Option(description="<S> the date for which to display posts published on or after.", shortName='S')
	public void setSince(String since) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.since = df.parse(since);
		} catch (ParseException e) {
			System.out.println("Error: Date for option --since not valid. Use format yyyy-mm-dd");
			this.since = new Date(0); 
		}
	}

		/**
	 * Returns the REGEX with which to match feed titles.
	 * @return the REGEX for title matching
	 */
	public Pattern getTitle() {
		return title;
	}

	/**
	 * Sets the REGEX with which to match feed titles.
	 * @param title the string to be parsed into a REGEX for title matching
	 */
	@Option(description="<S> the regex with which to match feed titles.")
	public void setTitle(String title) {
		this.title = Pattern.compile(title);
	}

	/**
	 * Sets the name of the input feed file
	 * @param filename the input file name containing the feeds
	 */
	@Option(description="The location of a file containing feed URLs, one per line.")
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Returns the filename containing the feeds
	 * @return the filename containing the feeds
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets whether or not to show help.
	 */
	@Option(shortName = 'h', 
		description = "Displays this message", 
		exclude = Exclusion.OTHER_OPTIONS_OR_ARGUMENTS)
	public void setHelp() {
		this.showHelp= true;
	}

	/**
	 * Gets whether or not to show the help.
	 */
	public boolean showHelp() {
		return this.showHelp;
	}

	public boolean getUpdate() {
		return this.update;
	}
	
	@Option(shortName = 'u', 
			description = "Periodically check for new posts and print them when they appear." 
	)
	public void setUpdate() {
		this.update = true;
	}
	
	public boolean getChime() {
		return this.chime;
	}
	
	@Option(shortName = 'c', 
			description = "Sound a chime when new posts are found." 
	)
	public void setChime() {
		this.chime = true;
	}
	

}
