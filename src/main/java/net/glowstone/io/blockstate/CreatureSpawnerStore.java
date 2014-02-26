package net.glowstone.io.blockstate;

import net.glowstone.block.entity.GlowCreatureSpawner;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;

import java.util.List;

public class CreatureSpawnerStore extends BlockStateStore<GlowCreatureSpawner> {
    public CreatureSpawnerStore() {
        super(GlowCreatureSpawner.class, "MobSpawner");
    }


    @Override
    public void load(GlowCreatureSpawner spawner, CompoundTag compound) {
        super.load(spawner, compound);
        spawner.setCreatureTypeByName(compound.get("EntityId", StringTag.class));
        spawner.setDelay(compound.get("Delay", IntTag.class));
    }

    @Override
    public List<Tag> save(GlowCreatureSpawner spawner) {
        List<Tag> ret = super.save(spawner);
        ret.add(new StringTag("EntityId", spawner.getCreatureTypeName()));
        ret.add(new IntTag("Delay", spawner.getDelay()));
        return ret;
    }
}
