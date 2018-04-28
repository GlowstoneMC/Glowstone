package net.glowstone.io.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.glowstone.GlowServer;
import net.glowstone.constants.ItemIds;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Utility methods for transforming various objects to and from NBT.
 */
public final class NbtSerialization {

    private NbtSerialization() {
    }

    /**
     * Read an item stack in from an NBT tag.
     *
     * <p>Returns null if no item exists.
     *
     * @param tag The tag to read from.
     * @return The resulting ItemStack, or null.
     */
    public static ItemStack readItem(CompoundTag tag) {
        final Material[] material = new Material[1];
        if (!tag.readString(id -> material[0] = ItemIds.getItem(id), "id")
                && !tag.readShort(id -> material[0] = Material.getMaterial(id), "id")) {
            return null;
        }
        final short[] damage = {0};
        tag.readShort(x -> damage[0] = x, "Damage");
        final byte[] count = {0};
        tag.readByte(x -> count[0] = x, "Count");

        if (material[0] == null || material[0] == Material.AIR || count[0] == 0) {
            return null;
        }
        ItemStack stack = new ItemStack(material[0], count[0], damage[0]);
        // This is slightly different than what tag.readItem would do, since we specify the
        // material separately.
        tag.readCompound(
            subtag -> stack.setItemMeta(GlowItemFactory.instance().readNbt(material[0], subtag)),
                "tag");
        return stack;
    }

    /**
     * Write an item stack to an NBT tag.
     *
     * <p>Null stacks produce an empty tag, and if slot is negative it is omitted from the result.
     *
     * @param stack The stack to write, or null.
     * @param slot The slot, or negative to omit.
     * @return The resulting tag.
     */
    public static CompoundTag writeItem(ItemStack stack, int slot) {
        CompoundTag tag = new CompoundTag();
        if (stack == null || stack.getType() == Material.AIR) {
            return tag;
        }
        tag.putString("id", ItemIds.getName(stack.getType()));
        tag.putShort("Damage", stack.getDurability());
        tag.putByte("Count", stack.getAmount());
        tag.putByte("Slot", slot);
        CompoundTag meta = GlowItemFactory.instance().writeNbt(stack.getItemMeta());
        if (meta != null) {
            tag.putCompound("tag", meta);
        }
        return tag;
    }

    /**
     * Read a full inventory (players, chests, etc.) from a compound list.
     *
     * @param tagList The list of CompoundTags to read from.
     * @param start The slot number to consider the inventory's start.
     * @param size The desired size of the inventory.
     * @return An array with the contents of the inventory.
     */
    public static ItemStack[] readInventory(List<CompoundTag> tagList, int start, int size) {
        ItemStack[] items = new ItemStack[size];
        for (CompoundTag tag : tagList) {
            tag.readByte(slot -> {
                if (slot >= start && slot < start + size) {
                    items[slot - start] = readItem(tag);
                }
            }, "Slot");
        }
        return items;
    }

    /**
     * Write a full inventory (players, chests, etc.) to a compound list.
     *
     * @param items An array with the contents of the inventory.
     * @param start The slot number to consider the inventory's start.
     * @return The list of CompoundTags.
     */
    public static List<CompoundTag> writeInventory(ItemStack[] items, int start) {
        List<CompoundTag> out = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (!InventoryUtil.isEmpty(stack)) {
                out.add(writeItem(stack, start + i));
            }
        }
        return out;
    }

    /**
     * Attempt to resolve a world based on the contents of a compound tag.
     *
     * @param server The server to look up worlds in.
     * @param compound The tag to read the world from.
     * @return The world, or null if none could be found.
     */
    public static World readWorld(GlowServer server, CompoundTag compound) {
        final World[] world = {null};
        compound.readUuid(uuid -> world[0] = server.getWorld(uuid),
                "WorldUUIDMost", "WorldUUIDLeast");
        if (world[0] == null) {
            compound.readString(name -> world[0] = server.getWorld(name), "World");
        }
        if (world[0] == null) {
            compound.readInt(dim -> {
                for (World serverWorld : server.getWorlds()) {
                    if (serverWorld.getEnvironment().getId() == dim) {
                        world[0] = serverWorld;
                        break;
                    }
                }
            }, "Dimension");
        }
        return world[0];
    }

    /**
     * Save world identifiers (UUID and dimension) to a compound tag for later lookup.
     *
     * @param world The world to identify.
     * @param compound The tag to write to.
     */
    public static void writeWorld(World world, CompoundTag compound) {
        UUID worldUuid = world.getUID();
        // world UUID used by Bukkit and code above
        compound.putLong("WorldUUIDMost", worldUuid.getMostSignificantBits());
        compound.putLong("WorldUUIDLeast", worldUuid.getLeastSignificantBits());
        // leave a Dimension value for possible Vanilla use
        compound.putInt("Dimension", world.getEnvironment().getId());
    }

    /**
     * Read a Location from the "Pos" and "Rotation" children of a tag.
     *
     * <p>If "Pos" is absent or invalid, null is returned.
     *
     * <p>If "Rotation" is absent or invalid, it is skipped and a location without rotation is
     * returned.
     *
     * @param world The world of the location (see readWorld).
     * @param tag The tag to read from.
     * @return The location, or null.
     */
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

    /**
     * Write a Location to the "Pos" and "Rotation" children of a tag.
     *
     * <p>Does not save world information, use writeWorld instead.
     *
     * @param loc The location to write.
     * @param tag The tag to write to.
     */
    public static void locationToListTags(Location loc, CompoundTag tag) {
        tag.putDoubleList("Pos", Arrays.asList(loc.getX(), loc.getY(), loc.getZ()));
        tag.putFloatList("Rotation", Arrays.asList(loc.getYaw(), loc.getPitch()));
    }

    /**
     * Create a Vector from a list of doubles.
     *
     * <p>If the list is invalid, a zero vector is returned.
     *
     * @param list The list to read from.
     * @return The Vector.
     */
    public static Vector listToVector(List<Double> list) {
        if (list.size() == 3) {
            return new Vector(list.get(0), list.get(1), list.get(2));
        }
        return new Vector(0, 0, 0);
    }

    /**
     * Create a list of doubles from a Vector.
     *
     * @param vec The vector to write.
     * @return The list.
     */
    public static List<Double> vectorToList(Vector vec) {
        return Arrays.asList(vec.getX(), vec.getY(), vec.getZ());
    }

}
