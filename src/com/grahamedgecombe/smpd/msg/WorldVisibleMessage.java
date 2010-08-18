package com.grahamedgecombe.smpd.msg;

public final class WorldVisibleMessage extends Message {

	private final boolean visible;

	public WorldVisibleMessage(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

}
