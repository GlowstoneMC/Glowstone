package net.glowstone.constants.enchantments;

final class EnvironmentalProtection extends Protection {
    EnvironmentalProtection() {
        super(0, "PROTECTION_ENVIRONMENTAL", "Protection", 10);
    }

    @Override
    protected int getMinRange(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 20;
    }
}
