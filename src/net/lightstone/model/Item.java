package net.lightstone.model;

public class Item {

	private final int id, count, damage;

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int count) {
		this(id, count, 0);
	}

	public Item(int id, int count, int damage) {
		this.id = id;
		this.count = count;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}

	public int getDamage() {
		return damage;
	}

}
