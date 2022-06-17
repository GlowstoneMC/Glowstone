package net.glowstone.entity;

import com.destroystokyo.paper.entity.Pathfinder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GlowMob extends GlowLivingEntity implements Mob {

    @Getter
    @Setter
    private boolean aware;

    public GlowMob(Location location, double maxHealth) {
        super(location, maxHealth);
    }

    // TODO: 1.13
    @Override
    public @NotNull Pathfinder getPathfinder() {
        return null;
    }

    @Override
    public boolean isInDaylight() {
        return false;
    }

    @Override
    public void lookAt(@NotNull Location location) {

    }

    @Override
    public void lookAt(@NotNull Location location, float headRotationSpeed, float maxHeadPitch) {

    }

    @Override
    public void lookAt(@NotNull Entity entity) {

    }

    @Override
    public void lookAt(@NotNull Entity entity, float headRotationSpeed, float maxHeadPitch) {

    }

    @Override
    public void lookAt(double x, double y, double z) {

    }

    @Override
    public void lookAt(double x, double y, double z, float headRotationSpeed, float maxHeadPitch) {

    }

    @Override
    public int getHeadRotationSpeed() {
        return 0;
    }

    @Override
    public int getMaxHeadPitch() {
        return 0;
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        return null;
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    public void setLeftHanded(boolean leftHanded) {

    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {

    }

    @Override
    public @Nullable LootTable getLootTable() {
        return null;
    }

    @Override
    public void setLootTable(@Nullable LootTable table) {

    }

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public void setSeed(long seed) {

    }
}
