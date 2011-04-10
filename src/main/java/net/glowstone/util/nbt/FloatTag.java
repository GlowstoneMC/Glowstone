package net.glowstone.util.nbt;

/**
 * The {@code TAG_Float} tag.
 * @author Graham Edgecombe
 */
public final class FloatTag extends Tag {

	/**
	 * The value.
	 */
	private final float value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public FloatTag(String name, float value) {
		super(name);
		this.value = value;
	}

	@Override
	public Float getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Float" + append + ": " + value;
	}

}

