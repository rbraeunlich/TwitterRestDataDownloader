package de.blogspot.wrongtracks.twitter.rest.downloader.io;

import java.util.Collection;

public interface TwitterDataWriterFactory {

	TwitterDataWriter createDataWriter(String user, int level);

	Collection<String> listUsersForLevel(String prefixForCurrentLevel);
}
