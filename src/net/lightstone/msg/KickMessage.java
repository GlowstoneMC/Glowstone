package net.lightstone.msg;

public final class KickMessage extends Message {

	private final String reason;

	public KickMessage(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

}
