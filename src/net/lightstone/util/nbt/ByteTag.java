package net.lightstone.util.nbt;

/**
 * The {@code TAG_Byte} tag.
 * @author Graham Edgecombe
 */
public final class ByteTag extends Tag {

	/**
	 * The value.
	 */
	private final byte value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public ByteTag(String name, byte value) {
		super(name);
		this.value = value;
	}

	@Override
	public Byte getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Byte" + append + ": " + value;
	}

}

