package net.lightstone.msg;

public final class ChatMessage extends Message {

	private final String message;

	public ChatMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
