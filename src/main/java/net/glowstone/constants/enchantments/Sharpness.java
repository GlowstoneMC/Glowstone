package net.glowstone.constants.enchantments;

final class Sharpness extends WeaponDamage {
    Sharpness() {
        super(16, "DAMAGE_ALL", "Sharpness", 10);
    }

    @Override
    protected int getMinRange(int level) {
        return 1 + (level - 1) * 11;
    }
}
