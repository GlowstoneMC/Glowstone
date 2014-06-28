package net.glowstone.io.nbt;

import net.glowstone.GlowServer;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class NbtSerialization {

    private NbtSerialization() {
    }

    public static ItemStack readItem(CompoundTag tag) {
        short id = tag.isShort("id") ? tag.getShort("id") : 0;
        short damage = tag.isShort("Damage") ? tag.getShort("Damage") : 0;
        byte count = tag.isByte("Count") ? tag.getByte("Count") : 0;

        Material material = Material.getMaterial(id);
        if (material == null || id == 0 || count == 0) {
            return null;
        }
        ItemStack stack = new ItemStack(material, count, damage);
        if (tag.isCompound("tag")) {
            stack.setItemMeta(GlowItemFactory.instance().readNbt(material, tag.getCompound("tag")));
        }
        return stack;
    }

    public static CompoundTag writeItem(ItemStack stack, int slot) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("id", stack.getTypeId());
        tag.putShort("Damage", stack.getDurability());
        tag.putByte("Count", stack.getAmount());
        tag.putByte("Slot", slot);
        CompoundTag meta = GlowItemFactory.instance().writeNbt(stack.getItemMeta());
        if (meta != null) {
            tag.putCompound("tag", meta);
        }
        return tag;
    }

    public static ItemStack[] readInventory(List<CompoundTag> tagList, int start, int size) {
        ItemStack[] items = new ItemStack[size];
        for (CompoundTag tag : tagList) {
            byte slot = tag.isByte("Slot") ? tag.getByte("Slot") : 0;
            if (slot >= start && slot < start + size) {
                items[slot - start] = readItem(tag);
            }
        }
        return items;
    }

    public static List<CompoundTag> writeInventory(ItemStack[] items, int start) {
        List<CompoundTag> out = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                out.add(writeItem(stack, start + i));
            }
        }
        return out;
    }

    public static World findWorld(GlowServer server, CompoundTag compound) {
        World world = null;
        if (compound.isLong("WorldUUIDLeast") && compound.isLong("WorldUUIDMost")) {
            long uuidLeast = compound.getLong("WorldUUIDLeast");
            long uuidMost = compound.getLong("WorldUUIDMost");
            world = server.getWorld(new UUID(uuidMost, uuidLeast));
        }
        if (world == null && compound.isString("World")) {
            world = server.getWorld(compound.getString("World"));
        }
        if (world == null && compound.isInt("Dimension")) {
            int dim = compound.getInt("Dimension");
            for (World sWorld : server.getWorlds()) {
                if (sWorld.getEnvironment().getId() == dim) {
                    world = sWorld;
                    break;
                }
            }
        }
        return world;
    }

    public static Location listTagsToLocation(World world, CompoundTag tag) {
        // check for position list
        if (tag.isList("Pos", TagType.DOUBLE)) {
            List<Double> pos = tag.getList("Pos", TagType.DOUBLE);
            if (pos.size() == 3) {
                Location location = new Location(world, pos.get(0), pos.get(1), pos.get(2));

                // check for rotation
                if (tag.isList("Rotation", TagType.FLOAT)) {
                    List<Float> rot = tag.getList("Rotation", TagType.FLOAT);
                    if (rot.size() == 2) {
                        location.setYaw(rot.get(0));
                        location.setPitch(rot.get(1));
                    }
                }

                return location;
            }
        }

        return null;
    }

    public static void locationToListTags(Location loc, CompoundTag tag) {
        List<Double> posList = new ArrayList<>();
        posList.add(loc.getX());
        posList.add(loc.getY());
        posList.add(loc.getZ());

        List<Float> rotList = new ArrayList<>();
        rotList.add(loc.getYaw());
        rotList.add(loc.getPitch());

        tag.putList("Pos", TagType.DOUBLE, posList);
        tag.putList("Rotation", TagType.FLOAT, rotList);
    }

    public static Vector listTagToVector(List<Double> list) {
        if (list.size() == 3) {
            return new Vector(list.get(0), list.get(1), list.get(2));
        }
        return new Vector(0, 0, 0);
    }

    public static List<Double> vectorToList(Vector vec) {
        List<Double> ret = new ArrayList<>(3);
        ret.add(vec.getX());
        ret.add(vec.getY());
        ret.add(vec.getZ());
        return ret;
    }

}
