package net.glowstone.constants.enchantments;

final class Smite extends WeaponDamage {
    Smite() {
        super(17, "DAMAGE_UNDEAD", "Smite", 5);
    }

    @Override
    protected int getMinRange(int level) {
        return 5 + (level - 1) * 8;
    }
}
