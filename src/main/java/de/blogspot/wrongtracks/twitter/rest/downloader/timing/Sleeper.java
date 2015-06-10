package de.blogspot.wrongtracks.twitter.rest.downloader.timing;

public interface Sleeper {
	
	void start();
	
	void waitUntilEndOfInterval();

}
