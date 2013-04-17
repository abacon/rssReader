rssReader
=========

This RSS reader was created by Veronica Lynn, Holly French, and Andrew
Bacon for CS204 S'13, Carleton College.

Usage:  
./rss_reader.sh [options] urls.txt  

urls.txt must be a text file containing urls of rss feeds, one feed per
line.  

Options:  
  --by-date        Commingles all posts and sorts them according to date.  
  --by-alpha       Shows posts by feed, where feeds are in alphabetical order.  
  --description    Show descriptions of posts.  
  --number=<NUM>   Limits the number of posts shown from each feed  
  --title=REGEX    Limits posts to those matching the specified regex.  

Design:  
    

Known Issues:  
    -Crashes if a feed or post doesn't have a title  
    -Crashes if a non-integer is entered for the --number option  
