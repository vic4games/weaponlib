package com.vicmatskiv.weaponlib;

import java.util.Deque;
import java.util.LinkedList;

final class StatusMessageCenter {
	
	static class Message {
		long expiresAt;
		String message;
		public Message(String message, long expiresAt) {
			this.message = message;
			this.expiresAt = expiresAt;
		}
	}
	
	protected Deque<Message> messageQueue = new LinkedList<>();

	public void addMessage(String message) {
		addMessage(message, -1);
	}
	/*
	 * Rules:
	 * 	Latest added message has highest display priority
	 * 
	 * To add message:
	 * 	check existing messages in the stack and remove all of them with the same or smaller expiration time
	 */
	public void addMessage(String message, long duration) {
		long expiresAt = duration < 0 ? Long.MAX_VALUE : System.currentTimeMillis() + duration;
		while(!messageQueue.isEmpty()) {
			Message m = messageQueue.removeFirst();
			if(m.expiresAt > expiresAt) {
				// if found a message expiring after the one to add, put it back to the queue and stop the lookup
				messageQueue.addFirst(m);
				break;
			}
		}
		messageQueue.addFirst(new Message(message, expiresAt));
	}
	
	public String nextMessage() {
		String message = null;
		while(!messageQueue.isEmpty()) {
			Message m = messageQueue.removeFirst();
			if(m.expiresAt > System.currentTimeMillis()) {
				message = m.message;
				messageQueue.addFirst(m);
				break;
			}
		}
		return message;
	}
	
}
