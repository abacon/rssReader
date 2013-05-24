rssReader
=========

This RSS reader was originally created by Veronica Lynn, Holly French, and Andrew
Bacon for CS204 S'13, Carleton College.

This version has been updated by Andrew Bacon & Veronica Lynn to use the observer pattern.
It contains two observers: One which posts the title and description of any new posts, and one which plays an alert when new posts are found. 

PLEASE TURN YOUR SOUND ON!

__Usage:__  
./rss_reader.sh [options] <urls.txt>  

urls.txt must be a text file containing urls of rss feeds, one feed per
line.  

__Options:__  
Valid options are:
      --by-alpha     Show feeds in alphabetical order.  
  -d, --by-date      Sort posts by date.  
  -c, --chime        Sound a chime when new posts are found. NEW OBSERVER  
  -D, --description  Show a description under each post.  
      --filename     The location of a file containing feed URLs, one per line.  
  -h, --help         Displays this message  
  -n, --newest       Show only those items which are new since your last use of RSSReader.  
  -N, --number       <N> the number of posts per feed to display.  
  -S, --since        <S> the date for which to display posts published on or after.  
      --title        <S> the regex with which to match feed titles.  
  -u, --update       Periodically check for new posts and print them when they appear. NEW OBSERVER  
