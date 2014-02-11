package net.glowstone.io.entity;

import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.ListTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Location;

import java.util.List;


public abstract class HumanEntityStore<T extends GlowHumanEntity> extends LivingEntityStore<T> {

    public HumanEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        /*
        this.sleeping = nbttagcompound.m("Sleeping");
        this.sleepTicks = nbttagcompound.d("SleepTimer");
        */
        if (compound.is("Inventory", ListTag.class)) {
            List<CompoundTag> items = compound.getList("Inventory", CompoundTag.class);
            entity.getInventory().setContents(NbtSerialization.tagToInventory(items, entity.getInventory().getSize()));
        }
        if (compound.is("SpawnX", IntTag.class) && compound.is("SpawnY", IntTag.class) && compound.is("SpawnZ", IntTag.class)) {
            int x = compound.get("SpawnX", IntTag.class);
            int y = compound.get("SpawnY", IntTag.class);
            int z = compound.get("SpawnZ", IntTag.class);
            entity.setBedSpawnLocation(new Location(entity.getWorld(), x, y, z));
        }
    }

    @Override
    public List<Tag> save(T entity) {
        /*
        this.sleeping = nbttagcompound.m("Sleeping");
        this.sleepTicks = nbttagcompound.d("SleepTimer");
        */
        List<Tag> ret = super.save(entity);
        ret.add(NbtSerialization.inventoryToTag(entity.getInventory().getContents()));
        Location bed = entity.getBedSpawnLocation();
        if (bed != null) {
            ret.add(new IntTag("SpawnX", bed.getBlockX()));
            ret.add(new IntTag("SpawnY", bed.getBlockY()));
            ret.add(new IntTag("SpawnZ", bed.getBlockZ()));
        }
        return ret;
    }
}
