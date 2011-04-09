package net.lightstone.util.nbt;

/**
 * The {@code TAG_Double} tag.
 * @author Graham Edgecombe
 */
public final class DoubleTag extends Tag {

	/**
	 * The value.
	 */
	private final double value;

	/**
	 * Creates the tag.
	 * @param name The name.
	 * @param value The value.
	 */
	public DoubleTag(String name, double value) {
		super(name);
		this.value = value;
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";
		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}
		return "TAG_Double" + append + ": " + value;
	}

}

