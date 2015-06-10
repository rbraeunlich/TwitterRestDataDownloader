package de.blogspot.wrongtracks.twitter.rest.downloader.io.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReader;
import de.blogspot.wrongtracks.twitter.rest.downloader.io.TwitterDataReaderFactory;

public class TwitterDataFileReaderFactory implements TwitterDataReaderFactory {

	private String cursorFileDir;
	private String graphFilesDir;

	public TwitterDataFileReaderFactory(String cursorFileDir, String graphFilesDir) {
		this.cursorFileDir = cursorFileDir;
		this.graphFilesDir = graphFilesDir;
	}

	@Override
	public TwitterDataReader createDataReader(String user, int level) {
		return new TwitterDataFileReader(user, cursorFileDir, graphFilesDir, level);
	}

	@Override
	public Collection<String> getUsernamesForLevel(int i) {
		Collection<String> result = new ArrayList<String>();
		File file = new File(graphFilesDir + "level" + (i-1) + "/");
		for (File graphFile : FileUtils.listFiles(file,
				FileFilterUtils.makeCVSAware(null), null)) {
			String fileName = graphFile.getName();
			TwitterDataReader dataReader = this.createDataReader(fileName.substring(0, fileName.indexOf('.')), (i-1));
			List<String> screenNames = dataReader.readFriendsFromGraphData().stream().map(f -> f.getScreenName()).collect(Collectors.toList());
			result.addAll(screenNames);
		}
		return result;
	}

}
