package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import twitter4j.TwitterObjectFactory;
import twitter4j.User;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReader;

public class TwitterDataFileReader implements TwitterDataReader {

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private String user;
	private String cursorFilesDir;
	private String graphFilesDir;
	private int level;

	public TwitterDataFileReader(String user, String cursorFilesDir,
			String graphFilesDir, int level) {
		this.user = maskUser(user);
		this.cursorFilesDir = cursorFilesDir;
		this.graphFilesDir = graphFilesDir;
		this.level = level;
	}

	/**
	 * Replaces slashes for unix systems in username with a colon and appends
	 * txt for the filename
	 * 
	 * @param user
	 * @return
	 */
	private String maskUser(String user) {
		return user.replace("/", ":") + ".json";
	}

	@Override
	public Collection<User> readFriendsFromGraphData() {
		File graphFile = new File(graphFilesDir + "/level" + level + "/" + user);
		List<User> result = new ArrayList<User>();
		try (Scanner s = new Scanner(graphFile)) {
			while (s.hasNextLine()) {
				String json = s.nextLine();
				User user = TwitterObjectFactory.createUser(json);
				result.add(user);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Long readCursor() {
		try {
			File cursorFile = new File(cursorFilesDir + user);
			if (cursorFile.exists()) {
				return Long.valueOf(FileUtils.readFileToString(cursorFile,
						CHARSET));
			}
			return -1L;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
