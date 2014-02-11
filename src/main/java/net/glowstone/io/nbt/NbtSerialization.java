package net.glowstone.io.nbt;

import net.glowstone.util.nbt.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public final class NbtSerialization {

    private NbtSerialization() {
    }

    public static ItemStack[] tagToInventory(List<CompoundTag> tagList, int size) {
        ItemStack[] items = new ItemStack[size];
        for (CompoundTag tag : tagList) {
            short id = tag.is("id", ShortTag.class) ? tag.get("id", ShortTag.class) : 0;
            short damage = tag.is("Damage", ShortTag.class) ? tag.get("Damage", ShortTag.class) : 0;
            byte count = tag.is("Count", ByteTag.class) ? tag.get("Count", ByteTag.class) : 0;
            byte slot = tag.is("Slot", ByteTag.class) ? tag.get("Slot", ByteTag.class) : 0;
            if (id != 0 && count != 0 && slot > 0 && slot < items.length) {
                items[slot] = new ItemStack(id, count, damage);
            }
        }
        return items;
    }

    public static ListTag<CompoundTag> inventoryToTag(ItemStack[] items) {
        List<CompoundTag> out = new ArrayList<CompoundTag>();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                List<Tag> nbtItem = new ArrayList<Tag>(4);
                nbtItem.add(new ShortTag("id", (short) stack.getTypeId()));
                nbtItem.add(new ShortTag("Damage", stack.getDurability()));
                nbtItem.add(new ByteTag("Count", (byte) stack.getAmount()));
                nbtItem.add(new ByteTag("Slot", (byte) i));
                out.add(new CompoundTag("", nbtItem));
            }
        }
        return new ListTag<CompoundTag>("Inventory", TagType.COMPOUND, out);
    }

    public static Location listTagsToLocation(World world, List<DoubleTag> pos, List<FloatTag> rot) {
        if (pos.size() == 3 && rot.size() == 2) {
            return new Location(world, pos.get(0).getValue(), pos.get(1).getValue(), pos.get(2).getValue(), rot.get(0).getValue(), rot.get(1).getValue());
        }
        return world.getSpawnLocation();
    }

    public static List<Tag> locationToListTags(Location loc) {
        List<DoubleTag> posList = new ArrayList<DoubleTag>();
        List<FloatTag> rotList = new ArrayList<FloatTag>();
        List<Tag> ret = new LinkedList<Tag>();
        posList.add(new DoubleTag("", loc.getX()));
        posList.add(new DoubleTag("", loc.getY()));
        posList.add(new DoubleTag("", loc.getZ()));
        ret.add(new ListTag<DoubleTag>("Pos", TagType.DOUBLE, posList));
        rotList.add(new FloatTag("", loc.getYaw()));
        rotList.add(new FloatTag("", loc.getPitch()));
        ret.add(new ListTag<FloatTag>("Rotation", TagType.FLOAT, rotList));
        return ret;
    }

    public static Vector listTagToVector(ListTag<DoubleTag> tag) {
        List<DoubleTag> vecList = tag.getValue();
        if (vecList.size() == 3) {
            return new Vector(vecList.get(0).getValue(), vecList.get(1).getValue(), vecList.get(2).getValue());
        }
        return new Vector(0, 0, 0);
    }

    public static ListTag<DoubleTag> vectorToListTag(Vector vec) {
        List<DoubleTag> ret = new ArrayList<DoubleTag>();
        ret.add(new DoubleTag("", vec.getX()));
        ret.add(new DoubleTag("", vec.getY()));
        ret.add(new DoubleTag("", vec.getZ()));
        return new ListTag<DoubleTag>("Motion", TagType.DOUBLE, ret);
    }

}
