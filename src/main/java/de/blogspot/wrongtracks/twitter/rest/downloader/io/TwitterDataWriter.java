package de.blogspot.wrongtracks.twitter.rest.downloader.io;

import twitter4j.TwitterResponse;

public interface TwitterDataWriter {

	void writeToGraphFile(Iterable<? extends TwitterResponse> what);
	
	void writeToTweetsFile(Iterable<? extends TwitterResponse> what);
	
	boolean isTweetsFileExisting();

	boolean isGraphFileExisting();
	
	void writeToCursorFile(Long cursor);
}
