package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.MobSpawnerEntity;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

public class GlowCreatureSpawner extends GlowBlockState implements CreatureSpawner {

    private EntityType spawned;
    private int delay;

    public GlowCreatureSpawner(GlowBlock block) {
        super(block);

        MobSpawnerEntity spawner = getBlockEntity();
        spawned = spawner.getSpawning();
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
            spawner.setSpawning(spawned);
            spawner.setDelay(delay);
            spawner.updateInRange();
        }
        return result;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int i) {
        if (i < 0) i = 0;
        delay = i;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Spawned Type

    @Override
    public EntityType getSpawnedType() {
        return spawned;
    }

    @Override
    public void setSpawnedType(EntityType creatureType) {
        spawned = creatureType;
    }

    @Override
    public void setCreatureTypeByName(String creatureType) {
        EntityType type = EntityType.fromName(creatureType);
        if (type != null) {
            spawned = type;
        }
    }

    @Override
    public String getCreatureTypeName() {
        return spawned.getName();
    }

}
