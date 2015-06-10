package de.blogspot.wrongtracks.twitter.rest.downloader.timing.impl;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.blogspot.wrongtracks.twitter.rest.downloader.timing.Sleeper;

/**
 * A sleeper that waits by calling {@link Thread#sleep(long)}. Multiple calls to
 * start do not have any effect.
 *
 */
public class SingleStartThreadSleeper implements Sleeper {

	private Long intervalMs;

	private Date startingPoint;

	public SingleStartThreadSleeper(Long waitIntervalInMs) {
		this.intervalMs = waitIntervalInMs;
	}

	@Override
	public void start() {
		if(startingPoint == null){
			startingPoint = new Date();
		}
	}

	@Override
	public void waitUntilEndOfInterval() {
		if (startingPoint == null) {
			return;
		}
		Date endPoint = new Date();
		try {
			long diff = intervalMs
					- (endPoint.getTime() - startingPoint.getTime());
			if (diff < 0) {
				return;
			}
			Thread.sleep(diff);
		} catch (InterruptedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,
					"Exception while sleeping", e);
		} finally {
			startingPoint = null;
		}
	}
}
