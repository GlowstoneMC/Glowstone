package net.glowstone.constants.enchantments;

final class BlastProtection extends Protection {
    BlastProtection() {
        super(3, "PROTECTION_EXPLOSIONS", "Blast Protection", 2);
    }

    @Override
    protected int getMinRange(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 12;
    }
}
