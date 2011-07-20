package net.glowstone.io.nbt;

import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.DoubleTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.ListTag;
import net.glowstone.util.nbt.ShortTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbtSerialization {

    public static ItemStack[] tagToInventory(ListTag<CompoundTag> tagList, int size) {
       ItemStack[] items = new ItemStack[size];
        for (CompoundTag tag: tagList.getValue()) {
            Map<String, Tag> tagItems = tag.getValue();
            Tag idTag = tagItems.get("id");
            Tag damageTag = tagItems.get("Damage");
            Tag countTag = tagItems.get("Count");
            Tag slotTag = tagItems.get("Slot");
            short id = (idTag == null) ? 0 : ((ShortTag)idTag).getValue();
            short damage = (damageTag == null) ? 0 : ((ShortTag)damageTag).getValue();
            byte count = (countTag == null) ? 0 : ((ByteTag)countTag).getValue();
            byte slot = (slotTag == null) ? -1 : ((ByteTag)slotTag).getValue();
            if (id != 0 && slot >= 0 && count != 0) {
                if (items.length > slot) {
                    items[slot] = new ItemStack(id, count, damage);
                }
            }
        }
        return items;
    }

    public static ListTag<CompoundTag> inventoryToTag(ItemStack[] items) {
        List<CompoundTag> out = new ArrayList<CompoundTag>();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                Map<String, Tag> nbtItem = new HashMap<String, Tag>();
                nbtItem.put("id", new ShortTag("id", (short)stack.getTypeId()));
                nbtItem.put("Damage", new ShortTag("Damage", stack.getDurability()));
                nbtItem.put("Count", new ByteTag("Count", (byte)stack.getAmount()));
                nbtItem.put("Slot", new ByteTag("Slot", (byte)i));
                out.add(new CompoundTag("", nbtItem));
            }
        }
        return new ListTag<CompoundTag>("Inventory", CompoundTag.class, out);
    }

    public static Location listTagsToLocation(World world, ListTag<DoubleTag> pos, ListTag<FloatTag> rot) {
        List<DoubleTag> posList = pos.getValue();
        List<FloatTag> rotList = rot.getValue();
        if (posList.size() == 3 && rotList.size() == 2) {
            return new Location(world, posList.get(0).getValue(), posList.get(1).getValue(), posList.get(2).getValue(), rotList.get(0).getValue(), rotList.get(1).getValue());
        }
        return world.getSpawnLocation();
    }

    public static Map<String, Tag> locationToListTags(Location loc) {
        List<DoubleTag> posList = new ArrayList<DoubleTag>();
        List<FloatTag> rotList = new ArrayList<FloatTag>();
        Map<String, Tag> ret = new HashMap<String, Tag>();
        posList.add(new DoubleTag("", loc.getX()));
        posList.add(new DoubleTag("", loc.getY()));
        posList.add(new DoubleTag("", loc.getZ()));
        ret.put("Pos", new ListTag<DoubleTag>("Pos", DoubleTag.class, posList));
        rotList.add(new FloatTag("", loc.getYaw()));
        rotList.add(new FloatTag("", loc.getPitch()));
        ret.put("Rotation", new ListTag<FloatTag>("Rotation", FloatTag.class, rotList));
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
        return new ListTag<DoubleTag>("Motion", DoubleTag.class, ret);
    }

    public static Map<String, Tag> expandListForCompoundTag(List<Tag> list) {
        Map<String, Tag> ret = new HashMap<String, Tag>();
        for (Tag tag : list) {
            ret.put(tag.getName(), tag);
        }
        return ret;
    }
}
