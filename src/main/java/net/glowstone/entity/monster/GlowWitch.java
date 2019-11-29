package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Witch;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class GlowWitch extends GlowMonster implements Witch {

    public GlowWitch(Location loc) {
        super(loc, EntityType.WITCH, 26);
        setBoundingBox(0.6, 1.8);
    }

    public boolean isAggressive() {
        return metadata.getBoolean(MetadataIndex.WITCH_AGGRESSIVE);
    }

    public void setAggressive(boolean aggressive) {
        metadata.set(MetadataIndex.WITCH_AGGRESSIVE, aggressive);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_WITCH_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_WITCH_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_WITCH_AMBIENT;
    }

    @Override
    public void rangedAttack(LivingEntity target, float charge) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setChargingAttack(boolean raiseHands) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    // TODO: 1.13
    @Override
    public boolean isDrinkingPotion() {
        return false;
    }

    @Override
    public int getPotionUseTimeLeft() {
        return 0;
    }

    @Override
    public @Nullable ItemStack getDrinkingPotion() {
        return null;
    }

    @Override
    public void setDrinkingPotion(@Nullable ItemStack potion) {

    }
}
