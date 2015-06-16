package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.blogspot.wrongtracks.twitter.rest.downloader.io.impl.TwitterDataFileWriter;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

public class TwitterDataFileWriterTest {

	private static final Charset ENCODING = Charset.forName("UTF-8");
	private static final String TEST_GRAPH_DIR = "/tmp/";
	private static final String TEST_TWEETS_DIR = "/tmp/";
	private static final String TEST_CURSOR_DIR = "/tmp/";
	private static final String USER = "foo";
	private static final String LEVEL = "level1";

	private TwitterDataFileWriter writer;
	private Collection<File> toDelete = new ArrayList<File>();

	@Before
	public void setUp() {
		writer = new TwitterDataFileWriter(USER, TEST_CURSOR_DIR,
				TEST_GRAPH_DIR, TEST_TWEETS_DIR, 1);
	}
	
	@After
	public void tearDown(){
		for (File file : toDelete) {
			file.delete();
		}
		toDelete.clear();
	}

	@Test
	public void nonExistingTweetsFile() {
		boolean existing = writer.isTweetsFileExisting();
		assertThat(existing, is(false));
	}

	@Test
	public void existingTweetsFile() throws IOException {
		File file = new File(TEST_TWEETS_DIR + USER + ".json");
		file.createNewFile();
		toDelete.add(file);
		boolean existing = writer.isTweetsFileExisting();
		assertThat(existing, is(true));
	}
	
	@Test
	public void existingZippedTweetsFile() throws IOException {
		File file = new File(TEST_TWEETS_DIR + USER + ".json.gz");
		file.createNewFile();
		toDelete.add(file);
		boolean existing = writer.isTweetsFileExisting();
		assertThat(existing, is(true));
	}
	
	@Test
	public void nonExistingGraphFile() {
		boolean existing = writer.isGraphFileExisting();
		assertThat(existing, is(false));
	}

	@Test
	public void existingGraphFile() throws IOException {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File file = new File(TEST_GRAPH_DIR + LEVEL + "/" + USER + ".json");
		file.createNewFile();
		toDelete.add(file);
		boolean existing = writer.isGraphFileExisting();
		assertThat(existing, is(true));
	}

	@Test
	public void writeToCursorFile() throws IOException {
		writer.writeToCursorFile(1L);
		File cursorFile = new File(TEST_CURSOR_DIR + USER + ".json");
		toDelete.add(cursorFile);
		String string = FileUtils.readFileToString(cursorFile);
		assertThat(string, is("1"));
	}

	@Test
	public void writesToCursorFileDeleteOldEntries() throws IOException {
		writer.writeToCursorFile(1L);
		writer.writeToCursorFile(123456L);
		writer.writeToCursorFile(2L);
		File cursorFile = new File(TEST_CURSOR_DIR + USER + ".json");
		toDelete.add(cursorFile);
		String string = FileUtils.readFileToString(cursorFile, ENCODING);
		assertThat(string, is("2"));
	}

	@Test
	public void writeToGraphFile() throws Exception {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR  + LEVEL + "/" + USER + ".json");
		toDelete.add(graphFile);
		String user = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8927,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Test User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		User twitterUser = TwitterObjectFactory.createUser(user);
		JSONObject jsonObject = new JSONObject(user);
		setObjectIntoFactory(twitterUser, jsonObject);
		writer.writeToGraphFile(Collections.singleton(twitterUser));
		String string = FileUtils.readFileToString(graphFile, ENCODING);
		assertThat(string, is(user + "\n"));
	}
	
	@Test
	public void writeToGraphWithoutFriendsCreatesEmptryFile() throws Exception {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR  + LEVEL + "/" + USER + ".json");
		toDelete.add(graphFile);
		writer.writeToGraphFile(Collections.emptyList());
		String string = FileUtils.readFileToString(graphFile, ENCODING);
		assertThat(string, is(""));
	}

	@Test
	public void severalWritesToGraphFileAppend() throws Exception {
		File levelDir = new File("/tmp/" + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR  + LEVEL + "/"+ USER + ".json");
		toDelete.add(graphFile);
		String user = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8927,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Test User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		User twitterUser = TwitterObjectFactory.createUser(user);
		JSONObject jsonObject = new JSONObject(user);
		setObjectIntoFactory(twitterUser, jsonObject);
		writer.writeToGraphFile(Arrays.asList(twitterUser, twitterUser, twitterUser));
		String string = FileUtils.readFileToString(graphFile, ENCODING);
		assertThat(string, is(user + "\n" + user + "\n" + user + "\n"));
	}
	
	@Test
	public void writeToTweetFile() throws JSONException, TwitterException, IOException{
		File tweetFile = new File(TEST_TWEETS_DIR + USER + ".json");
		toDelete.add(tweetFile);
		String tweet = "{\"in_reply_to_status_id_str\":null,\"in_reply_to_status_id\":null,\"possibly_sensitive\":false,\"coordinates\":null,\"created_at\":\"Wed May 31 16:08:34 +0000 2015\",\"truncated\":false,\"in_reply_to_user_id_str\":\"117603\",\"source\":\"<a href=\\\"http://none.com\\\" rel=\\\"nofollow\\\">Client<\\/a>\",\"retweet_count\":0,\"retweeted\":false,\"geo\":null,\"in_reply_to_screen_name\":\"Foo Bar\",\"entities\":{\"urls\":[{\"display_url\":\"none.com\",\"indices\":[84,106],\"expanded_url\":\"http://www.none.com\",\"url\":\"http://t.co/\"}],\"hashtags\":[],\"user_mentions\":[{\"indices\":[0,12],\"screen_name\":\"Foo Bar\",\"id_str\":\"103501\",\"name\":\"Foo Bar\",\"id\":117603}],\"symbols\":[]},\"id_str\":\"6035936481920\",\"in_reply_to_user_id\":113501,\"favorite_count\":0,\"id\":60359364861920,\"text\":\"text\",\"place\":null,\"contributors\":null,\"lang\":\"en\",\"user\":{\"utc_offset\":-18000,\"friends_count\":121,\"profile_image_url_https\":\"https://none.com/normal.jpg\",\"listed_count\":273,\"profile_background_image_url\":\"http://none.com/back.jpg\",\"default_profile_image\":false,\"favourites_count\":9,\"description\":\"\",\"created_at\":\"Wed Dec 28 03:27:37 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/bg.jpg\",\"protected\":false,\"screen_name\":\"foo\",\"id_str\":\"1001517\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":100391517,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpg\",\"time_zone\":\"Central Time (US & Canada)\",\"url\":\"http://no.ne/rg\",\"contributors_enabled\":false,\"profile_background_tile\":false,\"profile_banner_url\":\"https://none.com/1357745455\",\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"display_url\":\"foo.bar\",\"indices\":[0,22],\"expanded_url\":\"http://www.foo.bar\",\"url\":\"http://no.ne/9n5\"}]}},\"statuses_count\":67,\"follow_request_sent\":null,\"followers_count\":558,\"profile_use_background_image\":true,\"default_profile\":false,\"following\":null,\"name\":\"Foo Bar\",\"location\":\"San Francisco\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null},\"favorited\":false}";
		JSONObject jsonObject = new JSONObject(tweet);
		Status status = TwitterObjectFactory.createStatus(tweet);
		setObjectIntoFactory(status, jsonObject);
		writer.writeToTweetsFile(Collections.singleton(status));
		String string = FileUtils.readFileToString(tweetFile, ENCODING);
		assertThat(string, is(tweet + "\n"));
	}
	
	@Test
	public void multipleWritesToTweetFileAppend() throws JSONException, TwitterException, IOException{
		File tweetFile = new File(TEST_TWEETS_DIR + USER + ".json");
		toDelete.add(tweetFile);
		String tweet = "{\"in_reply_to_status_id_str\":null,\"in_reply_to_status_id\":null,\"possibly_sensitive\":false,\"coordinates\":null,\"created_at\":\"Wed May 31 16:08:34 +0000 2015\",\"truncated\":false,\"in_reply_to_user_id_str\":\"117603\",\"source\":\"<a href=\\\"http://none.com\\\" rel=\\\"nofollow\\\">Client<\\/a>\",\"retweet_count\":0,\"retweeted\":false,\"geo\":null,\"in_reply_to_screen_name\":\"Foo Bar\",\"entities\":{\"urls\":[{\"display_url\":\"none.com\",\"indices\":[84,106],\"expanded_url\":\"http://www.none.com\",\"url\":\"http://t.co/\"}],\"hashtags\":[],\"user_mentions\":[{\"indices\":[0,12],\"screen_name\":\"Foo Bar\",\"id_str\":\"103501\",\"name\":\"Foo Bar\",\"id\":117603}],\"symbols\":[]},\"id_str\":\"6035936481920\",\"in_reply_to_user_id\":113501,\"favorite_count\":0,\"id\":60359364861920,\"text\":\"text\",\"place\":null,\"contributors\":null,\"lang\":\"en\",\"user\":{\"utc_offset\":-18000,\"friends_count\":121,\"profile_image_url_https\":\"https://none.com/normal.jpg\",\"listed_count\":273,\"profile_background_image_url\":\"http://none.com/back.jpg\",\"default_profile_image\":false,\"favourites_count\":9,\"description\":\"\",\"created_at\":\"Wed Dec 28 03:27:37 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/bg.jpg\",\"protected\":false,\"screen_name\":\"foo\",\"id_str\":\"1001517\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":100391517,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpg\",\"time_zone\":\"Central Time (US & Canada)\",\"url\":\"http://no.ne/rg\",\"contributors_enabled\":false,\"profile_background_tile\":false,\"profile_banner_url\":\"https://none.com/1357745455\",\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"display_url\":\"foo.bar\",\"indices\":[0,22],\"expanded_url\":\"http://www.foo.bar\",\"url\":\"http://no.ne/9n5\"}]}},\"statuses_count\":67,\"follow_request_sent\":null,\"followers_count\":558,\"profile_use_background_image\":true,\"default_profile\":false,\"following\":null,\"name\":\"Foo Bar\",\"location\":\"San Francisco\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null},\"favorited\":false}";
		JSONObject jsonObject = new JSONObject(tweet);
		Status status = TwitterObjectFactory.createStatus(tweet);
		setObjectIntoFactory(status, jsonObject);
		writer.writeToTweetsFile(Collections.singleton(status));
		writer.writeToTweetsFile(Collections.singleton(status));
		String string = FileUtils.readFileToString(tweetFile, ENCODING);
		assertThat(string, is(tweet + "\n" + tweet + "\n"));
	}
	
	/**
	 * Twitter4J is not very helpful for testing, that's why we gotta hack it a little bit.
	 * @param twitterUser
	 * @param jsonObject
	 */
	private void setObjectIntoFactory(Object o, JSONObject jsonObject){
		Method method;
		try {
			method = TwitterObjectFactory.class.getDeclaredMethod("registerJSONObject", new Class[]{Object.class, Object.class});
			method.setAccessible(true);
			method.invoke(null, o, jsonObject);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
