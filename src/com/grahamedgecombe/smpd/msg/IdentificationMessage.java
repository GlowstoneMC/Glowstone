package com.grahamedgecombe.smpd.msg;

public final class IdentificationMessage extends Message {

	private final int id;
	private final String name, message;

	public IdentificationMessage(int id, String name, String message) {
		this.id = id;
		this.name = name;
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

}
