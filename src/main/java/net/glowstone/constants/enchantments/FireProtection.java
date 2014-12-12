package net.glowstone.constants.enchantments;

final class FireProtection extends Protection {
    FireProtection() {
        super(1, "PROTECTION_FIRE", "Fire Protection", 5);
    }

    @Override
    protected int getMinRange(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 12;
    }
}
