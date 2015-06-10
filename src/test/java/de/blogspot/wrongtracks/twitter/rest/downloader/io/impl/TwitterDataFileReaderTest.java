package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import static org.hamcrest.CoreMatchers.either;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twitter4j.User;

public class TwitterDataFileReaderTest {

	private static final Charset ENCODING = Charset.forName("UTF-8");
	public static final String TEST_GRAPH_DIR = "/tmp/";
	public static final String TEST_CURSOR_FILE = "/tmp/cursor.txt";
	public static final String USER = "foo";
	private static final String LEVEL = "/level0/";
	private TwitterDataFileReader reader;
	private Collection<File> toDelete = new ArrayList<File>();

	@Before
	public void setUp() {
		reader = new TwitterDataFileReader(USER, TEST_CURSOR_FILE,
				TEST_GRAPH_DIR, 0);
	}

	@After
	public void tearDown() {
		for (File file : toDelete) {
			file.delete();
		}
		toDelete.clear();
	}

	@Test(expected = RuntimeException.class)
	public void readNonExistingFile() {
		reader.readFriendsFromGraphData();
	}

	@Test
	public void readEmptyGraphFile() throws IOException {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR + LEVEL + USER + ".json");
		graphFile.createNewFile();
		toDelete.add(graphFile);
		Collection<User> users = reader.readFriendsFromGraphData();
		assertThat(users.isEmpty(), is(true));
	}

	@Test
	public void readUserFromGraphFile() throws IOException {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR + LEVEL + USER + ".json");
		graphFile.createNewFile();
		toDelete.add(graphFile);
		String user = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8927,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Test User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		FileUtils.writeStringToFile(graphFile, user, ENCODING);
		Collection<User> users = reader.readFriendsFromGraphData();
		assertThat(users.size(), is(1));
		User readUser = users.iterator().next();
		assertThat(readUser.getId(), is(8927L));
		assertThat(readUser.getName(), is("Test User"));
	}

	@Test
	public void readSeveralUsersFromGraphFile() throws IOException {
		File levelDir = new File(TEST_GRAPH_DIR + LEVEL);
		levelDir.mkdir();
		toDelete.add(levelDir);
		File graphFile = new File(TEST_GRAPH_DIR + LEVEL + USER + ".json");
		graphFile.createNewFile();
		toDelete.add(graphFile);
		String user = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8927,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Test User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		String user2 = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8928,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Foo User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		String user3 = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8929,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Bar User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		FileUtils.writeStringToFile(graphFile, user, ENCODING);
		FileUtils.writeStringToFile(graphFile, user2, ENCODING);
		FileUtils.writeStringToFile(graphFile, user3, ENCODING);
		Collection<User> users = reader.readFriendsFromGraphData();
		for (User readUser : users) {
			assertThat(
					readUser.getId(),
					is(either(equalTo(8927L)).or(equalTo(8928L)).or(
							equalTo(8929L))));
			assertThat(
					readUser.getName(),
					is(either(equalTo("Test User")).or(equalTo("Foo User")).or(
							equalTo("Bar User"))));
		}
	}

	@Test
	public void readFromCursorFile() throws IOException {
		File cursorFile = new File(TEST_CURSOR_FILE + USER + ".json");
		toDelete.add(cursorFile);
		FileUtils.writeStringToFile(cursorFile, "123", ENCODING);
		Long cursor = reader.readCursor();
		assertThat(cursor, is(123L));
	}
	
	@Test
	public void readFromNonExistingCursorFile() throws IOException {
		Long cursor = reader.readCursor();
		assertThat(cursor, is(-1L));
	}
}
