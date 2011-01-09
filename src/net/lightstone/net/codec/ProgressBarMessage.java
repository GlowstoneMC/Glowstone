package net.lightstone.net.codec;

import net.lightstone.msg.Message;

public final class ProgressBarMessage extends Message {

	private final int id, progressBar, value;

	public ProgressBarMessage(int id, int progressBar, int value) {
		this.id = id;
		this.progressBar = progressBar;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getProgressBar() {
		return progressBar;
	}

	public int getValue() {
		return value;
	}

}
