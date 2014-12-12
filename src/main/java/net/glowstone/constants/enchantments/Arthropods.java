package net.glowstone.constants.enchantments;

final class Arthropods extends WeaponDamage {
    Arthropods() {
        super(18, "DAMAGE_ARTHROPODS", "Bane of Arthropods", 5);
    }

    @Override
    protected int getMinRange(int level) {
        return 5 + (level - 1) * 8;
    }
}
