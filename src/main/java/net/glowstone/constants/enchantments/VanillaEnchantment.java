package net.glowstone.constants.enchantments;

public enum VanillaEnchantment {
    PROTECTION_ENVIRONMENTAL(new EnvironmentalProtection()),
    PROTECTION_FIRE(new FireProtection()),
    PROTECTION_FALL(new FallProtection()),
    PROTECTION_EXPLOSIONS(new BlastProtection()),
    PROTECTION_PROJECTILE(new ProjectileProtection()),
    OXYGEN(new Oxygen()),
    WATER_WORKER(new WaterWorker()),
    THORNS(new Thorns()),
    DEPTH_STRIDER(new DepthStrider()),
    DAMAGE_ALL(new Sharpness()),
    DAMAGE_UNDEAD(new Smite()),
    DAMAGE_ARTHROPODS(new Arthropods()),
    KNOCKBACK(new Knockback()),
    FIRE_ASPECT(new FireAspect()),
    LOOT_BONUS_MOBS(new Looting()),
    DIG_SPEED(new DigSpeed()),
    SILK_TOUCH(new SilkTouch()),
    DURABILITY(new Durability()),
    LOOT_BONUS_BLOCKS(new Fortune()),
    ARROW_DAMAGE(new ArrowDamage()),
    ARROW_KNOCKBACK(new ArrowKnockback()),
    ARROW_FIRE(new ArrowFire()),
    ARROW_INFINITE(new ArrowInfinity()),
    LUCK(new Luck()),
    LURE(new Lure());

    /////////////////////////////////////////////////

    private final GlowEnchantment impl;

    VanillaEnchantment(GlowEnchantment impl) {
        this.impl = impl;
    }

    public GlowEnchantment getGlowEnchantment() {
        return impl;
    }
}
