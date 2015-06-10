package de.blogspot.wrongtracks.twitter.rest.downloader.timing.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.blogspot.wrongtracks.twitter.rest.downloader.timing.Sleeper;

public class SleeperImpl implements Sleeper {

	private Long intervalMs;

	private Date startingPoint;

	public SleeperImpl(Long waitIntervalInMs) {
		this.intervalMs = waitIntervalInMs;
	}

	@Override
	public void start() {
		startingPoint = new Date();
	}

	@Override
	public void waitUntilEndOfInterval() {
		if(startingPoint == null){
			return;
		}
		Date endPoint = new Date();
		try {
			long diff = intervalMs
					- (endPoint.getTime() - startingPoint.getTime());
			if(diff < 0){
				return;
			}
			Thread.sleep(diff);
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Exception while sleeping", e);
		}
	}
}
