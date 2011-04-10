package net.glowstone.msg;

public final class IdentificationMessage extends Message {

	private final int id, dimension;
	private final String name, message;
	private final long seed;

	public IdentificationMessage(int id, String name, String message, long seed, int dimension) {
		this.id = id;
		this.name = name;
		this.message = message;
		this.seed = seed;
		this.dimension = dimension;
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

	public long getSeed() {
		return seed;
	}

	public int getDimension() {
		return dimension;
	}

}
