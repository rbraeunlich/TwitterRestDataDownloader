package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriterFactory;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataWriter;

public class TwitterDataFileWriterFactory implements TwitterDataWriterFactory {

	private String cursorFilesDir;
	private String graphFilesDir;
	private String tweetsFilesDir;

	public TwitterDataFileWriterFactory(String cursorFilesDir,
			String graphFilesDir, String tweetsFilesDir) {
		this.cursorFilesDir = cursorFilesDir;
		this.graphFilesDir = graphFilesDir;
		this.tweetsFilesDir = tweetsFilesDir;
	}

	@Override
	public TwitterDataWriter createDataWriter(String user, int level) {
		return new TwitterDataFileWriter(user, cursorFilesDir, graphFilesDir,
				tweetsFilesDir, level);
	}

	@Override
	public Collection<String> listUsersForLevel(String prefixForCurrentLevel) {
		Collection<String> result = new ArrayList<String>();
		for (File f : FileUtils.listFiles(new File(graphFilesDir
				+ prefixForCurrentLevel), FileFilterUtils.makeCVSAware(null),
				null)) {
			String filename = f.getName();
			String name = filename.substring(0, filename.indexOf('.'));
			result.add(name);
		}
		return result;
	}
}
