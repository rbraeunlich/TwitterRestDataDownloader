package de.blogspot.wrongtracks.twitter.rest.downloader;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import twitter4j.PagableResponseList;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReader;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReaderFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriter;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriterFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.timing.Sleeper;

public class TwitterDataDownloaderTest {

	private static final Sleeper EMPTY_SLEEPER = new Sleeper() {

		@Override
		public void waitUntilEndOfInterval() {
		}

		@Override
		public void start() {
		}
	};

	@Test
	public void getDataForInitialUserWithOneLonelyFriend()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		User onlyFriend = createMockUser(friendName, friendId, false);
		createFriendsAnswer(twitter, userId, onlyFriend);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);

		new TwitterDataDownloader(username, 0, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(twitter).showUser(eq(username));
		verify(twitter).getFriendsList(eq(userId), eq(-1L), eq(20));
		verify(twitter).getUserTimeline(eq(friendId), anyObject());
		verify(userWriter).writeToGraphFile(anyObject());
		verify(userWriter).writeToCursorFile(eq(0L));
		verify(friendWriter).writeToTweetsFile(anyObject());
	}

	@Test
	public void getDataForInitialUserWithOneLonelyProtectedFriend()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		User onlyFriend = createMockUser(friendName, friendId, true);
		createFriendsAnswer(twitter, userId, onlyFriend);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);

		new TwitterDataDownloader(username, 0, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(twitter).showUser(eq(username));
		verify(twitter).getFriendsList(eq(userId), eq(-1L), eq(20));
		verify(twitter, never()).getUserTimeline(eq(friendId), anyObject());
		verify(userWriter).writeToGraphFile(anyObject());
		verify(userWriter).writeToCursorFile(eq(0L));
		verify(friendWriter, never()).writeToTweetsFile(anyObject());
	}

	@Test
	public void getDataForInitialUserWithTwoLonelyFriends()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		String friendName2 = "Friend2";
		Long friendId2 = 12345L;
		User friend1 = createMockUser(friendName, friendId, false);
		User friend2 = createMockUser(friendName2, friendId2, false);
		createFriendsAnswer(twitter, userId, friend1, friend2);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);
		// reuse the writer
		when(writerFactory.createDataWriter(eq(friendName2), eq(1)))
				.thenReturn(friendWriter);

		new TwitterDataDownloader(username, 0, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(twitter).getFriendsList(eq(userId), eq(-1L), eq(20));
		verify(twitter).getUserTimeline(eq(friendId), anyObject());
		verify(userWriter).writeToGraphFile(anyObject());
		verify(userWriter).writeToCursorFile(eq(0L));
		verify(friendWriter, times(2)).writeToTweetsFile(anyObject());
	}

	@Test
	public void getDataForInitialUsersFriendWithOneFriend()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		String friendName2 = "Friend2";
		Long friendId2 = 12345L;
		User friend1 = createMockUser(friendName, friendId, false);
		User friend2 = createMockUser(friendName2, friendId2, false);
		createFriendsAnswer(twitter, userId, friend1);
		createFriendsAnswer(twitter, friendId, friend2);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		when(twitter.showUser(eq(friendName))).thenReturn(friend1);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		when(readerFactory.getUsernamesForLevel(eq(1))).thenReturn(
				Collections.singleton(friendName));
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		when(friendWriter.isGraphFileExisting()).thenReturn(false);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(userWriter.isGraphFileExisting()).thenReturn(false);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);
		// reuse the writer
		when(writerFactory.createDataWriter(eq(friendName2), eq(2)))
				.thenReturn(friendWriter);

		new TwitterDataDownloader(username, 1, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(twitter, times(1)).showUser(eq(username));
		verify(twitter, times(1)).showUser(eq(friendName));
		verify(twitter).getFriendsList(eq(userId), eq(-1L), eq(20));
		verify(twitter).getFriendsList(eq(friendId), eq(-1L), eq(20));
		verify(twitter).getUserTimeline(eq(friendId), anyObject());
		verify(userWriter).writeToGraphFile(anyObject());
		verify(userWriter, times(1)).writeToCursorFile(eq(0L));
		verify(friendWriter, times(1)).writeToCursorFile(eq(0L));
		verify(friendWriter, times(2)).writeToTweetsFile(anyObject());
	}

	private User createMockUser(String screenName, Long friendId,
			boolean isProtected) {
		User friend = mock(User.class);
		when(friend.getScreenName()).thenReturn(screenName);
		when(friend.getId()).thenReturn(friendId);
		when(friend.isProtected()).thenReturn(isProtected);
		return friend;
	}

	@Test
	public void getShouldNotWriteTweetsAgainIfAlreadyExisting()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		User onlyFriend = createMockUser(friendName, friendId, false);
		createFriendsAnswer(twitter, userId, onlyFriend);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		when(friendWriter.isTweetsFileExisting()).thenReturn(true);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);

		new TwitterDataDownloader(username, 0, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(friendWriter).isTweetsFileExisting();
		verify(friendWriter, never()).writeToTweetsFile(anyObject());
	}

	@Test
	public void shouldNotWriteGraphFileAgainAtInitialCursor()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		User onlyFriend = createMockUser(friendName, friendId, false);
		createFriendsAnswer(twitter, userId, onlyFriend);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		TwitterDataReaderFactory readerFactory = createMockReaderFactoryWithSimpleReader();
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		when(friendWriter.isGraphFileExisting()).thenReturn(true);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(userWriter.isGraphFileExisting()).thenReturn(true);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);

		new TwitterDataDownloader(username, 0, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(userWriter).isGraphFileExisting();
		verify(userWriter, never()).writeToGraphFile(anyObject());
	}

	@Test
	public void resumeAtCursorIfInterruptedButNotFinished()
			throws TwitterException {
		Twitter twitter = mock(Twitter.class);
		String username = "user";
		Long userId = 12L;
		User user = createMockUser(username, userId, false);
		String friendName = "Friend";
		Long friendId = 1234L;
		String friendName2 = "Friend2";
		Long friendId2 = 12345L;
		Long cursor = 34573452L;
		User friend1 = createMockUser(friendName, friendId, false);
		User friend2 = createMockUser(friendName2, friendId2, false);
		createFriendsAnswer(twitter, userId, friend1);
		createFriendsAnswer(twitter, friendId, cursor, friend2);
		Status tweet = mock(Status.class);
		createTweetAnswerForUser(twitter, friendId, tweet);
		when(twitter.showUser(eq(username))).thenReturn(user);
		when(twitter.showUser(eq(friendName))).thenReturn(friend1);
		TwitterDataReaderFactory readerFactory = mock(TwitterDataReaderFactory.class);
		TwitterDataReader friendDataReader = mock(TwitterDataReader.class);
		when(friendDataReader.readCursor()).thenReturn(cursor);
		when(friendDataReader.readCursor()).thenReturn(cursor);
		when(readerFactory.createDataReader(eq(friendName), eq(1))).thenReturn(
				friendDataReader);
		TwitterDataReader userReader = mock(TwitterDataReader.class);
		when(userReader.readCursor()).thenReturn(-1L);
		when(readerFactory.createDataReader(eq(username), eq(0))).thenReturn(
				userReader);
		when(readerFactory.getUsernamesForLevel(eq(1))).thenReturn(
				Collections.singleton(friendName));
		TwitterDataWriterFactory writerFactory = mock(TwitterDataWriterFactory.class);
		TwitterDataWriter friendWriter = mock(TwitterDataWriter.class);
		TwitterDataWriter userWriter = mock(TwitterDataWriter.class);
		when(userWriter.isGraphFileExisting()).thenReturn(true);
		when(writerFactory.createDataWriter(eq(username), eq(0))).thenReturn(
				userWriter);
		when(writerFactory.createDataWriter(eq(friendName), eq(1))).thenReturn(
				friendWriter);
		// reuse the writer
		when(writerFactory.createDataWriter(eq(friendName2), eq(2)))
		.thenReturn(friendWriter);

		new TwitterDataDownloader(username, 1, writerFactory, readerFactory,
				EMPTY_SLEEPER).start(twitter);

		verify(twitter).getFriendsList(eq(friendId), eq(cursor), eq(20));
		verify(userWriter, never()).writeToGraphFile(anyObject());
	}

	private void createFriendsAnswer(Twitter twitterMock, Long userId,
			User... friends) throws TwitterException {
		createFriendsAnswer(twitterMock, userId, -1L, friends);
	}

	@SuppressWarnings("unchecked")
	private void createFriendsAnswer(Twitter twitterMock, Long userId,
			Long atCursor, User... friends) throws TwitterException {
		PagableResponseList<User> usersFriends = mock(PagableResponseList.class);
		when(usersFriends.iterator()).thenReturn(
				Arrays.asList(friends).iterator());
		when(usersFriends.getNextCursor()).thenReturn(0L);
		when(twitterMock.getFriendsList(eq(userId), eq(atCursor), eq(20)))
				.thenReturn(usersFriends);
	}

	@SuppressWarnings("unchecked")
	private void createTweetAnswerForUser(Twitter twitterMock, Long friendId,
			Status... status) throws TwitterException {
		ResponseList<Status> friendsTweets = mock(ResponseList.class);
		when(friendsTweets.iterator()).thenReturn(
				Arrays.asList(status).iterator());
		when(twitterMock.getUserTimeline(eq(friendId), anyObject()))
				.thenReturn(friendsTweets);
	}

	/**
	 * Mocks a {@link TwitterDataReaderFactory} that returns for every user a
	 * {@link TwitterDataReader}, which returns -1 as cursor.
	 * 
	 * @return
	 */
	private TwitterDataReaderFactory createMockReaderFactoryWithSimpleReader() {
		TwitterDataReaderFactory readerFactory = mock(TwitterDataReaderFactory.class);
		TwitterDataReader dataReader = mock(TwitterDataReader.class);
		when(dataReader.readCursor()).thenReturn(-1L);
		when(readerFactory.createDataReader(anyObject(), anyInt())).thenReturn(
				dataReader);
		return readerFactory;
	}
}
