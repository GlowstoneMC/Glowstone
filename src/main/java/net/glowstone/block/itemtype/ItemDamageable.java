package net.glowstone.block.itemtype;

public class ItemDamageable extends ItemType {

    private final int maxUses;

    public ItemDamageable(int maxUses) {
        this.maxUses = maxUses;
    }

    /**
     * Get the number times this tool can be used without being destroyed.
     * @return Number of uses
     */
    public int getMaxUses() {
        return maxUses;
    }
}
