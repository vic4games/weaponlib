package com.vicmatskiv.weaponlib;

import org.junit.Assert;
import org.junit.Test;

import com.vicmatskiv.weaponlib.StatusMessageCenter.Message;

public class MessageQueueTest {

	@Test
	public void testEmpty() {
		StatusMessageCenter queue = new StatusMessageCenter();
		Assert.assertNull(queue.nextMessage());
	}
	
	@Test
	public void testSingleExpiringMessage() throws InterruptedException {
		StatusMessageCenter queue = new StatusMessageCenter();
		String message = "m1";
		int duration = 10;
		queue.addMessage(message, duration);
		Message m = queue.nextMessage();
		Assert.assertEquals(message, m);
		
		m = queue.nextMessage(); // get the message second time, make sure it's still there
		Assert.assertEquals(message, m);
		Thread.sleep(duration + 1);
		m = queue.nextMessage();
		Assert.assertNull(m);
		Assert.assertEquals(0, queue.messageQueue.size());
	}
	
	@Test
	public void testSecondMessageExpiringBeforeFirst() throws InterruptedException {
		StatusMessageCenter queue = new StatusMessageCenter();
		String message1 = "m1";
		long duration1 = 1000;
		queue.addMessage(message1, duration1);
		
		String message2 = "m2";
		long duration2 = 10;
		queue.addMessage(message2, duration2);
		Message m = queue.nextMessage();
		Assert.assertEquals(message2, m);
		m = queue.nextMessage();
		Assert.assertEquals(message2, m);
		
		Thread.sleep(duration2 + 1);
		m = queue.nextMessage();
		Assert.assertEquals(message1, m);
		Assert.assertEquals(1, queue.messageQueue.size());
	}
	
	@Test
	public void testSecondMessageExpiringAfterFirst() throws InterruptedException {
		StatusMessageCenter queue = new StatusMessageCenter();
		String message1 = "m1";
		long duration1 = 10;
		queue.addMessage(message1, duration1);
		
		String message2 = "m2";
		long duration2 = 1000;
		queue.addMessage(message2, duration2);
		Message m = queue.nextMessage();
		Assert.assertEquals(message2, m);
		Assert.assertEquals(1, queue.messageQueue.size());
	}
	
	@Test
	public void testTwoMessagesWithTheSameExpirationTime() throws InterruptedException {
		StatusMessageCenter queue = new StatusMessageCenter();
		String message1 = "m1";
		queue.addMessage(message1);
		String message2 = "m2";
		queue.addMessage(message2);
		Message m = queue.nextMessage();
		Assert.assertEquals(message2, m);
		Assert.assertEquals(1, queue.messageQueue.size());
	}
}
