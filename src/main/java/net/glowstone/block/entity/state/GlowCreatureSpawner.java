package net.glowstone.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.MobSpawnerEntity;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class GlowCreatureSpawner extends GlowBlockState implements CreatureSpawner {

    @Getter
    @Setter
    private EntityType spawnedType;
    @Getter
    private int delay;

    /**
     * Creates a mob spawner.
     *
     * @param block the spawner block
     */
    public GlowCreatureSpawner(GlowBlock block) {
        super(block);

        MobSpawnerEntity spawner = getBlockEntity();
        spawnedType = spawner.getSpawning();
        delay = spawner.getDelay();
    }

    private MobSpawnerEntity getBlockEntity() {
        return (MobSpawnerEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            MobSpawnerEntity spawner = getBlockEntity();
            spawner.setSpawning(spawnedType);
            spawner.setDelay(delay);
            spawner.updateInRange();
        }
        return result;
    }

    @Override
    public void setDelay(int i) {
        if (i < 0) {
            i = 0;
        }
        delay = i;
    }

    // TODO: mob spawner API
    @Override
    public int getMinSpawnDelay() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setMinSpawnDelay(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getMaxSpawnDelay() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setMaxSpawnDelay(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getSpawnCount() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setSpawnCount(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getMaxNearbyEntities() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setMaxNearbyEntities(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getRequiredPlayerRange() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setRequiredPlayerRange(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int getSpawnRange() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setSpawnRange(int i) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean isActivated() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void resetTimer() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void setSpawnedItem(@NotNull ItemStack itemStack) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Spawned Type

    @Override
    public void setCreatureTypeByName(String creatureType) {
        EntityType type = EntityType.fromName(creatureType);
        if (type != null) {
            spawnedType = type;
        }
    }

    @Override
    public String getCreatureTypeName() {
        return spawnedType.getName();
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
