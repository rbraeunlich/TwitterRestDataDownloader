package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriter;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterResponse;

public class TwitterDataFileWriter implements TwitterDataWriter {

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private static final String LINE_SEPARATOR = "\n";

	private final String cursorFilesDir;
	private final String graphFilesDir;
	private final String tweetsFilesDir;
	private final String user;
	private int level;

	public TwitterDataFileWriter(String user, String cursorFilesDir,
			String graphFilesDir, String tweetsFilesDir, int level) {
		this.cursorFilesDir = cursorFilesDir;
		this.graphFilesDir = graphFilesDir;
		this.tweetsFilesDir = tweetsFilesDir;
		this.user = maskUser(user);
		this.level = level;
	}

	public void writeToCursorFile(Long cursor) {
		try {
			FileUtils.writeStringToFile(new File(cursorFilesDir + "/" + user),
					cursor.toString(), CHARSET, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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

	private void writeToUserFile(String what, String dir, String user) {
		try {
			FileUtils.writeStringToFile(new File(dir + user), what, CHARSET,
					true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeToGraphFile(Iterable<? extends TwitterResponse> what) {
		what.forEach((x) -> writeToUserFile(TwitterObjectFactory.getRawJSON(x)
				+ LINE_SEPARATOR, graphFilesDir + "/level" + level + "/", user));
	}

	public void writeToTweetsFile(Iterable<? extends TwitterResponse> what) {
		what.forEach((x) -> writeToUserFile(TwitterObjectFactory.getRawJSON(x)
				+ LINE_SEPARATOR, tweetsFilesDir, user));
	}

	public boolean isTweetsFileExisting() {
		return new File(tweetsFilesDir + user).exists();
	}

	public boolean isGraphFileExisting() {
		return new File(graphFilesDir + "/level" + level + "/" + user).exists();
	}
}
