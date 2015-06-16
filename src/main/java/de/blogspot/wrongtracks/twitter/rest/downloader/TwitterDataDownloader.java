package de.blogspot.wrongtracks.twitter.rest.downloader;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReaderFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriter;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriterFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.timing.Sleeper;

public class TwitterDataDownloader {

	private static final int TIME_WINDOW_MINUTES = 15;
	private static final int FRIENDS_PER_FRIEND_REQUEST = 20;
	private static final int TWEET_REQUESTS_PER_MINUTE = 300
			/ TIME_WINDOW_MINUTES / FRIENDS_PER_FRIEND_REQUEST;

	private static final Logger LOGGER = Logger
			.getLogger(TwitterDataDownloader.class.getName());
	private Sleeper sleeper;
	private String startUser;
	private Integer depth;
	private TwitterDataWriterFactory writerFactory;
	private TwitterDataReaderFactory readerFactory;

	public TwitterDataDownloader(String startUser, Integer depth,
			TwitterDataWriterFactory writerFactory,
			TwitterDataReaderFactory readerFactory, Sleeper sleeper) {
		this.startUser = startUser;
		this.depth = depth;
		this.writerFactory = writerFactory;
		this.readerFactory = readerFactory;
		this.sleeper = sleeper;
	}

	public Sleeper getSleeper() {
		return sleeper;
	}

	public String getStartUser() {
		return startUser;
	}

	public Integer getDepth() {
		return depth;
	}

	public TwitterDataWriterFactory getWriterFactory() {
		return writerFactory;
	}

	public TwitterDataReaderFactory getReaderFactory() {
		return readerFactory;
	}

	/**
	 * limitations: 300 tweet requests per 15min window 15 friends list requests
	 * per 15min window; both in chunks of twenty
	 *
	 *
	 * Therefore, we can request every minute 20 friends and for each of those
	 * friends we can make one tweet requests
	 */
	public void start(Twitter twitter) throws TwitterException {
		Integer level = Integer.valueOf(depth);
		Collection<String> usersForCurrentLevel = Collections
				.singleton(getStartUser());
		for (int i = 0; i <= level; i++) {
			if (i > 0) {
				usersForCurrentLevel = getReaderFactory().getUsernamesForLevel(
						i);
			}
			for (String currUser : usersForCurrentLevel) {
				LOGGER.info("Reading data for user " + currUser);
				Long cursor = getReaderFactory().createDataReader(currUser, i)
						.readCursor();
				do {
					TwitterDataWriter dataWriter = getWriterFactory()
							.createDataWriter(currUser, i);
					if (hasGraphDataAlreadyBeenRead(cursor, dataWriter)) {
						cursor = 0L;
						LOGGER.info("Skipping " + currUser);
						continue;
					}
					User user = twitter.showUser(currUser);
					if (!user.isProtected()) {
						PagableResponseList<User> friendsList = twitter
								.getFriendsList(user.getId(), cursor,
										FRIENDS_PER_FRIEND_REQUEST);
						cursor = friendsList.getNextCursor();
						LOGGER.info("Cursor " + cursor);
						cursor = readAndWriteFriends(user.getScreenName(),
								dataWriter, cursor, i, friendsList);
						for (User friend : friendsList) {
							if (!friend.isProtected()) {
								readAndWriteTweets(twitter, i, friend);
							}
							sleeper.start();
						}
						LOGGER.info("sleeping");
						sleeper.waitUntilEndOfInterval();
					} else {
						cursor = 0L;
					}
				} while (cursor != 0);
			}
		}
		LOGGER.info("Done for level " + level + " and user " + startUser);
	}

	private boolean hasGraphDataAlreadyBeenRead(Long cursor,
			TwitterDataWriter dataWriter) {
		return (cursor == -1L || cursor == 0L)
				&& dataWriter.isGraphFileExisting();
	}

	private void readAndWriteTweets(Twitter twitter, Integer level, User user)
			throws TwitterException {
		TwitterDataWriter friendWriter = getWriterFactory().createDataWriter(
				user.getScreenName(), level + 1);
		if (!friendWriter.isTweetsFileExisting()) {
			LOGGER.info("Reading tweets for friend " + user.getScreenName());
			ResponseList<Status> timeline = twitter.getUserTimeline(
					user.getId(), new Paging(1, 200));
			friendWriter.writeToTweetsFile(timeline);
			// if I ever want to make more than one request per
			// friend;
			for (int i = 0; i < TWEET_REQUESTS_PER_MINUTE - 1; i++) {
				long minId = getMinId(timeline);
				Paging paging = new Paging(minId);
				timeline = twitter.getUserTimeline(user.getId(), paging);
				friendWriter.writeToTweetsFile(timeline);
				minId = getMinId(timeline);
			}
		} else {
			LOGGER.info("Tweets already present for " + user.getScreenName());
		}
	}

	private Long readAndWriteFriends(String user, TwitterDataWriter dataWriter,
			Long cursor, Integer level, PagableResponseList<User> friendsList) {
		dataWriter.writeToGraphFile(friendsList);
		dataWriter.writeToCursorFile(cursor);
		return cursor;
	}

	private static long getMinId(ResponseList<Status> timeline) {
		long minId = Long.MAX_VALUE;
		for (Status status : timeline) {
			if (status.getId() < minId) {
				minId = status.getId();
			}
		}
		return minId;
	}
}
