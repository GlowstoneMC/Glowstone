package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

public class GlowCreatureSpawner extends GlowBlockState implements CreatureSpawner {

    private MobSpawnerWrapper wrapper = new MobSpawnerWrapper();

    public GlowCreatureSpawner(GlowBlock block) {
        super(block);
    }

    public int getDelay() {
        return wrapper.delay;
    }

    public void setDelay(int i) {
        if (i < 0) i = 0;
        wrapper.delay = i;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Spawned Type

    public EntityType getSpawnedType() {
        return wrapper.type;
    }

    public void setSpawnedType(EntityType creatureType) {
        wrapper.type = creatureType;
    }

    public void setCreatureTypeByName(String creatureType) {
        EntityType type = EntityType.fromName(creatureType);
        if (type != null) {
            wrapper.type = type;
        }
    }

    public String getCreatureTypeName() {
        return wrapper.type.getName();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Deprecated CreatureType

    @Deprecated
    public CreatureType getCreatureType() {
        return CreatureType.fromEntityType(wrapper.type);
    }

    @Deprecated
    public void setCreatureType(CreatureType creatureType) {
        wrapper.type = creatureType.toEntityType();
    }

    @Deprecated
    public String getCreatureTypeId() {
        return wrapper.type.getName();
    }

    @Deprecated
    public void setCreatureTypeId(String s) {
        CreatureType type = CreatureType.fromName(s);
        if (type != null) {
            wrapper.type = type.toEntityType();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private static class MobSpawnerWrapper {
        public EntityType type = EntityType.PIG;
        public int delay = 15000;
    }

}
