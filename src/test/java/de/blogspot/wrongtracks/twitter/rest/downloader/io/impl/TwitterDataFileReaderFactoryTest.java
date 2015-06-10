package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

public class TwitterDataFileReaderFactoryTest {

	public static final String TEST_GRAPH_DIR = "/tmp/";

	private Collection<File> toDelete = new ArrayList<File>();

	@After
	public void tearDown() {
		for (File file : toDelete) {
			file.delete();
		}
		toDelete.clear();
	}

	/**
	 * For every level we have to take a look into the previous level and read the names from the files there
	 * @throws IOException
	 */
	@Test
	public void getUsernamesForLevel() throws IOException {
		File levelDir = new File(TEST_GRAPH_DIR + "level0");
		levelDir.mkdir();
		toDelete.add(levelDir);
		File userFile = new File(levelDir, "user.json");
		userFile.createNewFile();
		toDelete.add(userFile);
		String user = "{\"utc_offset\":7200,\"friends_count\":145,\"profile_image_url_https\":\"https://none.com/normal.jpeg\",\"listed_count\":3,\"profile_background_image_url\":\"http://none.com/bg.png\",\"default_profile_image\":false,\"favourites_count\":3,\"description\":\"test user\",\"created_at\":\"Wed Nov 18 20:14:08 +0000 2009\",\"is_translator\":false,\"profile_background_image_url_https\":\"https://none.com/images/bg.png\",\"protected\":true,\"screen_name\":\"test\",\"blocking\":null,\"id_str\":\"89893\",\"profile_link_color\":\"0084B4\",\"is_translation_enabled\":false,\"id\":8927,\"geo_enabled\":false,\"profile_background_color\":\"C0DEED\",\"lang\":\"en\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_text_color\":\"333333\",\"verified\":false,\"profile_image_url\":\"http://none.com/normal.jpeg\",\"time_zone\":\"Berlin\",\"url\":null,\"contributors_enabled\":false,\"profile_background_tile\":false,\"muting\":null,\"entities\":{\"description\":{\"urls\":[]}},\"statuses_count\":35,\"follow_request_sent\":null,\"blocked_by\":null,\"followers_count\":2,\"profile_use_background_image\":true,\"default_profile\":true,\"following\":null,\"name\":\"Test User\",\"location\":\"Germany\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"notifications\":null}";
		FileUtils.writeStringToFile(userFile, user);

		TwitterDataFileReaderFactory readerFactory = new TwitterDataFileReaderFactory(
				"", TEST_GRAPH_DIR);
		Collection<String> usernames = readerFactory.getUsernamesForLevel(1);

		assertThat(usernames, hasItem("test"));
	}
}
