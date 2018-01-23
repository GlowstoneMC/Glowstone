package net.glowstone.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.MobSpawnerEntity;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

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

}
