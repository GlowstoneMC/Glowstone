package net.glowstone.constants.enchantments;

final class ProjectileProtection extends Protection {
    ProjectileProtection() {
        super(4, "PROTECTION_PROJECTILE", "Projectile Protection", 5);
    }

    @Override
    protected int getMinRange(int level) {
        return 3 + (level - 1) * 6;
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 15;
    }
}
