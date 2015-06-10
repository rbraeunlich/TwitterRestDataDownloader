package de.blogspot.wrongtracks.twitter.rest.downloader.io;

import java.util.Collection;

public interface TwitterDataReaderFactory {

	TwitterDataReader createDataReader(String user, int level);

	Collection<String> getUsernamesForLevel(int i);
	
}
