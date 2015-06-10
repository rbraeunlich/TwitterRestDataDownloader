package de.blogspot.wrongtracks.twitter.rest.downloader.io;

import java.util.Collection;

import twitter4j.User;

public interface TwitterDataReader {

	Collection<User> readFriendsFromGraphData();

	/**
	 * Returns the last cursor for the user this reader is associated to.
	 * <p>
	 * Returns -1 if no cursor file is present.
	 * @return
	 */
	Long readCursor();
	
}
