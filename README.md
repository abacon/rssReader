rssReader
=========

Some RSS reader for class

What does it know?
*what syndfeed objects it has
*when it was last run
*all of the command line flags (and which are active)
*all posts (depending on how sort is implemented) (might make most sense for it to just be local, though)

What does it do?
*display output
*getPostFromFeed
*getAllPosts (calls above)
*sortPosts(using flags)
*matchByRegex
*listAllFeeds
*getFeedInfo

How is it constructed?
*command line arguments (bydate, byalpha, since, number of stories per feed, title (regex), description on/off, newest)
*feeds (file containing list of feed URLs). These will become an array of syndfeed objects.
