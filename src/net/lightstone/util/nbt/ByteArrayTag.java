package net.lightstone.util.nbt;

/**
 * The {@code TAG_Byte_Array} tag.
 * @author Graham Edgecombe
 */
public final class ByteArrayTag extends Tag {

	/**
	 * The value.
	 */
	private final byte[] value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public ByteArrayTag(String name, byte[] value) {
		super(name);
		this.value = value;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder hex = new StringBuilder();
		for (byte b : value) {
			String hexDigits = Integer.toHexString(b).toUpperCase();
			if (hexDigits.length() == 1) {
				hex.append("0");
			}
			hex.append(hexDigits).append(" ");
		}

		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Byte_Array" + append + ": " + hex.toString();
	}

}

