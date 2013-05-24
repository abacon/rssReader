import java.util.ArrayList;
import java.util.Date;

import com.sun.syndication.feed.synd.SyndFeedImpl;

public abstract class FeedObserver {

	public abstract void notify(ArrayList<SyndFeedImpl> changedFeeds, Date lastRunDate);

}
