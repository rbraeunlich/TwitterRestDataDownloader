# TwitterDataDownloader

This is a side project that has been developed while attending a lecture about Big Data analysis at TU Berlin.
The program takes a user to start with (his screenname, so the thing you refer to with a @) and a level (parameters 0 and 1 for the main method) and starts downloading the friends and tweets for every friend.
The level indicates if you only want your friends tweets (level0) or your friends' friends and friendsfriends tweets (level1) and so on...
You have to provide a file named downloader.properties somewhere on your classpath containing the following information:
```
cursorFilesDir=
graphFilesDir=
tweetsFilesDir=
consumerKey=
consumerSecret=
```
The first three indicate where you want to save your data and the last two are needed for authentication.
Please note that the IO-tests currently assume that you have a UNIX-style file format and that they can access ``/tmp/``.

Many thank to the guys from [Twitter4J](http://twitter4j.org/en/index.html) for providing such a nice library.