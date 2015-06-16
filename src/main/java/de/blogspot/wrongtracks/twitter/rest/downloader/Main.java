package de.blogspot.wrongtracks.twitter.rest.downloader;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReaderFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriterFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.impl.TwitterDataFileReaderFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.impl.TwitterDataFileWriterFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.timing.impl.SingleStartThreadSleeper;

public class Main {

	private static final String DOWNLOADER_PROPERTIES = "downloader.properties";
	private static final String CURSOR_FILE_KEY = "cursorFilesDir";
	private static final String GRAPH_FILES_DIR_KEY = "graphFilesDir";
	private static final String TWEETS_FILES_DIR_KEY = "tweetsFilesDir";
	private static final String CONSUMER_KEY_KEY = "consumerKey";
	private static final String CONSUMER_SECRET_KEY = "consumerSecret";
	private static final long ONE_MIN_IN_MS = TimeUnit.MINUTES.toMillis(1L);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(Main.class.getClassLoader().getResourceAsStream(
				DOWNLOADER_PROPERTIES));
		String cursorFile = props.getProperty(CURSOR_FILE_KEY);
		String graphFilesDir = props.getProperty(GRAPH_FILES_DIR_KEY);
		String tweetsFilesDir = props.getProperty(TWEETS_FILES_DIR_KEY);
		if (args.length < 2) {
			System.out
					.println("Wrong number of arguments. Start user and depth must be given");
			return;
		}
		if (Integer.parseInt(args[1]) < 1) {
			System.err.println("Invalid depth!");
		}
		TwitterDataWriterFactory writerFactory = new TwitterDataFileWriterFactory(
				cursorFile, graphFilesDir, tweetsFilesDir);
		TwitterDataReaderFactory readerFactory = new TwitterDataFileReaderFactory(
				cursorFile, graphFilesDir);
		int retries = 5;
		do {
			Twitter twitter = initTwitter(props.getProperty(CONSUMER_KEY_KEY),
					props.getProperty(CONSUMER_SECRET_KEY));
			TwitterDataDownloader downloader = new TwitterDataDownloader(
					args[0], Integer.valueOf(args[1]), writerFactory,
					readerFactory, new SingleStartThreadSleeper(ONE_MIN_IN_MS));
			try {
				downloader.start(twitter);
				//normal finish
				retries = 0;
			} catch (Exception e) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "", e);
				retries--;
				try {
					Thread.sleep(TimeUnit.SECONDS.toMillis(5L));
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
			}
		} while (retries > 0);
	}

	private static Twitter initTwitter(String consumerKey, String consumerSecret) {
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setApplicationOnlyAuthEnabled(true);
		builder.setIncludeEntitiesEnabled(true);
		builder.setJSONStoreEnabled(true);
		Twitter twitter = new TwitterFactory(builder.build()).getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		try {
			// necessary to force authorization
			twitter.getOAuth2Token();
		} catch (TwitterException e) {
			throw new RuntimeException(e);
		}
		return twitter;
	}
}
