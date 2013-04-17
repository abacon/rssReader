rssReader
=========

This RSS reader was created by Veronica Lynn, Holly French, and Andrew
Bacon for CS204 S'13, Carleton College.

__Usage:__  
./rss_reader.sh [options] urls.txt  

urls.txt must be a text file containing urls of rss feeds, one feed per
line.  

__Options:__  
  --by-date         Commingles all posts and sorts them according to date.  
  --by-alpha        Shows posts by feed, where feeds are in alphabetical order.  
  --description     Show descriptions of posts.  
  --number=<NUM>    Limits the number of posts shown from each feed  
  --title=REGEX     Limits posts to those matching the specified regex.  

__Design:__  
    The program is comprised of three classes:  
    RSSReader - The main class, which handles parsing the feeds, sorting the posts, and displaying the output  
    FileParser - Used by RSSReader to parse the input file containing urls of rss feeds  
    ArgParser - Used by RSSReader to parse the command line arguments  
    Refer to the documentation for more info.  

__Known Issues:__  
    - Crashes if a feed or post doesn't have a title  
    - Crashes if a non-integer is entered for the --number option  
