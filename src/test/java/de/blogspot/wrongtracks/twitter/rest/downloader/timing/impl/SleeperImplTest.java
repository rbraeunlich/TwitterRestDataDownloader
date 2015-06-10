package de.blogspot.wrongtracks.twitter.rest.downloader.timing.impl;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.Test;

import de.blogspot.wrongtracks.twitter.rest.downloader.timing.Sleeper;

public class SleeperImplTest {

	@Test
	public void testNormalSleepInterval() {
		Date start = new Date();
		Sleeper sleeper = new SleeperImpl(100L);
		sleeper.start();
		sleeper.waitUntilEndOfInterval();
		assertThat(new Date().getTime() - start.getTime() < 150, is(true));
	}
	
	@Test
	public void testNegativeSleepInterval() throws InterruptedException {
		Sleeper sleeper = new SleeperImpl(10L);
		sleeper.start();
		Thread.sleep(100L);
		sleeper.waitUntilEndOfInterval();
		//just check that we do not crash
	}
	
	@Test
	public void testDoNotSleepIfNeverStarted(){
		Date start = new Date();
		Sleeper sleeper = new SleeperImpl(100L);
		sleeper.waitUntilEndOfInterval();
		assertThat(new Date().getTime() - start.getTime() < 20, is(true));
	}
}
