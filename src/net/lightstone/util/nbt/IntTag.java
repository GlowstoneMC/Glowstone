package net.lightstone.util.nbt;

/**
 * The {@code TAG_Int} tag.
 * @author Graham Edgecombe
 */
public final class IntTag extends Tag {

	/**
	 * The value.
	 */
	private final int value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public IntTag(String name, int value) {
		super(name);
		this.value = value;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Int" + append + ": " + value;
	}

}

