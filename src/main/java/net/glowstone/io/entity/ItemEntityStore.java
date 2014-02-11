package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.util.nbt.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemEntityStore extends EntityStore<GlowItem> {

    public ItemEntityStore() {
        super(GlowItem.class, "Item");
    }

    @Override
    public GlowItem load(GlowServer server, GlowWorld world, CompoundTag compound) {
        GlowItem item = new GlowItem(server, world, null);
        load(item, compound);
        return item;
    }

    @Override
    public void load(GlowItem entity, CompoundTag compound) {
        if (compound.is("Item", CompoundTag.class)) {
            entity.setItemStack(readItemStack(compound.getTag("Item", CompoundTag.class)));
        }
        if (compound.is("Health", IntTag.class)) {
            // entity.setHealth(((IntTag)compound.getValue().get("Health")).getValue());
        }
        if (compound.is("Age", IntTag.class)) {
            entity.setTicksLived(compound.get("Age", IntTag.class));
        }
    }

    @Override
    public List<Tag> save(GlowItem entity) {
        List<Tag> ret = super.save(entity);
        List<Tag> itemTag = new ArrayList<Tag>(3);
        itemTag.add(new ShortTag("id", (short) entity.getItemStack().getTypeId()));
        itemTag.add(new ShortTag("Damage", entity.getItemStack().getDurability()));
        itemTag.add(new ByteTag("Count", (byte) entity.getItemStack().getAmount()));
        ret.add(new CompoundTag("Item", itemTag));
        // ret.add(new IntTag("Health", entity.getHealth()));
        // ret.add(new IntTag("Age", entity.getAge()));
        return ret;
    }

    public ItemStack readItemStack(CompoundTag tag) {
        ItemStack stack = null;
        short id = tag.is("id", ShortTag.class) ? tag.get("id", ShortTag.class) : 0;
        short damage = tag.is("Damage", ShortTag.class) ? tag.get("Damage", ShortTag.class) : 0;
        byte count = tag.is("Count", ByteTag.class) ? tag.get("Count", ByteTag.class) : 0;
        if (id != 0 && count != 0) {
            stack = new ItemStack(id, count, damage);
        }
        return stack;
    }
}
