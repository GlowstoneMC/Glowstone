package net.glowstone.block;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.EntityType;

public class GlowCreatureSpawner extends GlowBlockState implements CreatureSpawner {
    private MobSpawnerWrapper wrapper = new MobSpawnerWrapper(CreatureType.PIG, 15000);

    public GlowCreatureSpawner(GlowBlock block) {
        super(block);
    }

    public CreatureType getCreatureType() {
        return wrapper.type;
    }

    public void setCreatureType(CreatureType creatureType) {
        wrapper.type = creatureType;
    }

    public String getCreatureTypeId() {
        return wrapper.type.getName();
    }

    public void setCreatureTypeId(String s) {
        CreatureType type = CreatureType.fromName(s);
        if (type != null) {
            wrapper.type = type;
        }
    }

    public int getDelay() {
        return wrapper.delay;
    }

    public void setDelay(int i) {
        if (i < 0) i = 0;
        wrapper.delay = i;
    }

    @Override
    public GlowCreatureSpawner shallowClone() {
        GlowCreatureSpawner spawner = new GlowCreatureSpawner(getBlock());
        spawner.wrapper = wrapper;
        return spawner;
    }

    @Override
    public void destroy() {
        setDelay(-1);
        setCreatureType(CreatureType.PIG);
    }

    private static class MobSpawnerWrapper {
        public int delay;
        public CreatureType type;
        public MobSpawnerWrapper(CreatureType type, int delay) {
            this.type = type;
            this.delay = delay;
        }
    }

    // NEW STUFF

    @Override
    public EntityType getSpawnedType() {
        return null;
    }

    @Override
    public void setSpawnedType(EntityType creatureType) {

    }

    @Override
    public void setCreatureTypeByName(String creatureType) {

    }

    @Override
    public String getCreatureTypeName() {
        return null;
    }
}
