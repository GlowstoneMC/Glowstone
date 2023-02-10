package net.glowstone.net;

import com.destroystokyo.paper.ParticleBuilder;
import com.flowpowered.network.util.ByteBufUtils;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.SneakyThrows;
import net.glowstone.GlowServer;
import net.glowstone.constants.GlowParticle;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.entity.meta.MetadataType;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.util.*;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NbtInputStream;
import net.glowstone.util.nbt.NbtOutputStream;
import net.glowstone.util.nbt.NbtReadLimiter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Contains several utility methods for writing special data types to @{link ByteBuf}s.
 */
public final class GlowBufUtils {

    private GlowBufUtils() {
    }

    /**
     * Read a list of mob metadata entries from the buffer.
     *
     * @param buf The buffer.
     * @return The metadata.
     * @throws IOException if the buffer could not be read
     */
    public static List<Entry> readMetadata(ByteBuf buf) throws IOException {
        List<Entry> entries = new ArrayList<>();
        byte item;
        while ((item = buf.readByte()) != -1) {
            MetadataType type = MetadataType.byId(buf.readByte());
            MetadataIndex index = MetadataIndex.getIndex((int) item, type);

            switch (type) {
                case BYTE:
                    entries.add(new Entry(index, buf.readByte()));
                    break;
                case INT:
                    entries.add(new Entry(index, ByteBufUtils.readVarInt(buf)));
                    break;
                case FLOAT:
                    entries.add(new Entry(index, buf.readFloat()));
                    break;
                case STRING:
                    entries.add(new Entry(index, ByteBufUtils.readUTF8(buf)));
                    break;
                case ITEM:
                    entries.add(new Entry(index, readSlot(buf)));
                    break;
                case BOOLEAN:
                    entries.add(new Entry(index, buf.readBoolean()));
                    break;
                case VECTOR:
                    float x = buf.readFloat();
                    float y = buf.readFloat();
                    float z = buf.readFloat();
                    entries.add(new MetadataMap.Entry(index, new EulerAngle(x, y, z)));
                    break;
                case POSITION:
                case OPTPOSITION:
                    entries.add(new Entry(index, Position.getPosition(buf.readLong())));
                    break;
                case DIRECTION:
                    entries.add(new Entry(index, ByteBufUtils.readVarInt(buf)));
                    break;
                case OPTUUID:
                    if (buf.readBoolean()) {
                        entries.add(new Entry(index, readUuid(buf)));
                    }
                    break;
                case BLOCKID:
                    entries.add(new Entry(index, ByteBufUtils.readVarInt(buf)));
                    break;
                case NBTTAG:
                    entries.add(new Entry(index, readCompound(buf)));
                    break;
                default:
                    // do nothing
            }
        }
        return entries;
    }

    /**
     * Write a list of mob metadata entries to the buffer.
     *
     * @param buf     The buffer.
     * @param entries The metadata.
     * @throws IOException if the buffer could not be written to
     */
    public static void writeMetadata(ByteBuf buf, List<Entry> entries) throws IOException {
        for (Entry entry : entries) {
            MetadataIndex index = entry.index;
            Object value = entry.value;


            int type = index.getType().getId();
            int id = index.getIndex();

            System.out.println("Metadata: " + id + " " + type);

            buf.writeByte(id);
            ByteBufUtils.writeVarInt(buf, type);

            if (!index.getType().isOptional() && value == null) {
                continue;
            }
            if (index.getType().isOptional()) {
                buf.writeBoolean(value != null);
                if (value == null) {
                    continue;
                }
            }

            writeValue(buf, value, index.getType());
        }

        buf.writeByte(0xff);
    }

    public static void writeValue(ByteBuf buf, Object value, MetadataType type)
        throws IOException {
        switch (type) {
            case BYTE:
                buf.writeByte((Byte) value);
                break;
            case INT:
                ByteBufUtils.writeVarInt(buf, (Integer) value);
                break;
            case FLOAT:
                buf.writeFloat((Float) value);
                break;
            case STRING:
                ByteBufUtils.writeUTF8(buf, (String) value);
                break;
            case CHAT:
                writeChat(buf, (TextMessage) value);
                break;
            case OPTCHAT:
                writeChat(buf, (TextMessage) value);
                break;
            case ITEM:
                writeSlot(buf, (ItemStack) value);
                break;
            case BOOLEAN:
                buf.writeBoolean((Boolean) value);
                break;
            case VECTOR:
                EulerAngle angle = (EulerAngle) value;
                buf.writeFloat((float) Math.toDegrees(angle.getX()));
                buf.writeFloat((float) Math.toDegrees(angle.getY()));
                buf.writeFloat((float) Math.toDegrees(angle.getZ()));
                break;
            case POSITION:
            case OPTPOSITION:
                BlockVector vector = (BlockVector) value;
                buf.writeLong(Position.getPosition(vector));
                break;
            case DIRECTION:
                ByteBufUtils.writeVarInt(buf, (Integer) value);
                break;
            case OPTUUID:
                writeUuid(buf, (UUID) value);
                break;
            case BLOCKID:
                ByteBufUtils.writeVarInt(buf, (Integer) value);
                break;
            case NBTTAG:
                writeCompound(buf, (CompoundTag) value);
                break;
            case PARTICLE:
                writeParticle(buf, (ParticleBuilder) value);
                break;
            default:
                // do nothing
        }
    }

    /**
     * Read an uncompressed compound NBT tag from the buffer.
     *
     * @param buf The buffer.
     * @return The tag read, or null.
     */
    public static CompoundTag readCompound(ByteBuf buf) {
        return readCompound(buf, false);
    }

    private static CompoundTag readCompound(ByteBuf buf, boolean network) {
        int idx = buf.readerIndex();
        if (buf.readByte() == 0) {
            return null;
        }

        buf.readerIndex(idx);
        try (NbtInputStream str = new NbtInputStream(new ByteBufInputStream(buf), false)) {
            return str.readCompound(
                network ? new NbtReadLimiter(2097152L) : NbtReadLimiter.UNLIMITED);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Write an uncompressed compound NBT tag to the buffer.
     *
     * @param buf  The buffer.
     * @param data The tag to write, or null.
     */
    public static void writeCompound(ByteBuf buf, CompoundTag data) {
        if (data == null) {
            buf.writeByte(0);
            return;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (NbtOutputStream str = new NbtOutputStream(out, false)) {
            str.writeTag(data);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Error serializing NBT: " + data, e);
            return;
        }

        buf.writeBytes(out.toByteArray());
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf The buffer.
     * @return The stack read, or null.
     */
    public static ItemStack readSlot(ByteBuf buf) throws IOException {
        return readSlot(buf, false);
    }

    /**
     * Read an item stack from the buffer.
     *
     * @param buf     The buffer.
     * @param network Mark network source.
     * @return The stack read, or null.
     */
    public static ItemStack readSlot(ByteBuf buf, boolean network) throws IOException {
        boolean present = buf.readBoolean();
        if (!present) {
            return InventoryUtil.createEmptyStack();
        }

        int type = ByteBufUtils.readVarInt(buf);
        int amount = buf.readUnsignedByte();
        GlowUnsafeValues unsafeValues = (GlowUnsafeValues) Bukkit.getServer().getUnsafe();

        Material material = unsafeValues.fromId(type);
        if (material == null) {
            return InventoryUtil.createEmptyStack();
        }

        CompoundTag tag = readCompound(buf, network);
        ItemStack stack = new ItemStack(material, amount);
        stack.setItemMeta(GlowItemFactory.instance().readNbt(material, tag));
        return stack;
    }

    /**
     * Write an item stack to the buffer.
     *
     * @param buf   The buffer.
     * @param stack The stack to write, or null.
     */
    public static void writeSlot(ByteBuf buf, ItemStack stack) {
        if (InventoryUtil.isEmpty(stack)) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ByteBufUtils.writeVarInt(buf, MaterialUtil.getId(stack.getType()));
            buf.writeByte(stack.getAmount());
            if (stack.hasItemMeta()) {
                CompoundTag tag = GlowItemFactory.instance().writeNbt(stack.getItemMeta());
                writeCompound(buf, tag);
            } else {
                writeCompound(buf, null);
            }
        }
    }

    /**
     * Read an encoded block vector (position) from the buffer.
     *
     * @param buf The buffer.
     * @return The vector read.
     */
    public static BlockVector readBlockPosition(ByteBuf buf) {
        long val = buf.readLong();
        long x = val >> 38; // signed
        long y = val >> 26 & 0xfff; // unsigned
        // this shifting madness is used to preserve sign
        long z = val << 38 >> 38; // signed
        return new BlockVector((double) x, y, z);
    }

    /**
     * Write an encoded block vector (position) to the buffer.
     *
     * @param buf    The buffer.
     * @param vector The vector to write.
     */
    public static void writeBlockPosition(ByteBuf buf, Vector vector) {
        writeBlockPosition(buf, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * Write an encoded block vector (position) to the buffer.
     *
     * @param buf The buffer.
     * @param x   The x value.
     * @param y   The y value.
     * @param z   The z value.
     */
    public static void writeBlockPosition(ByteBuf buf, long x, long y, long z) {
        buf.writeLong((x & 0x3ffffff) << 38 | (y & 0xfff) << 26 | z & 0x3ffffff);
    }

    /**
     * Read a UUID encoded as two longs from the buffer.
     *
     * @param buf The buffer.
     * @return The UUID read.
     */
    public static UUID readUuid(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    /**
     * Write a UUID encoded as two longs to the buffer.
     *
     * @param buf  The buffer.
     * @param uuid The UUID to write.
     */
    public static void writeUuid(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Read an encoded chat message from the buffer.
     *
     * @param buf The buffer.
     * @return The chat message read.
     * @throws IOException on read failure.
     */
    public static TextMessage readChat(ByteBuf buf) throws IOException {
        return TextMessage.decode(ByteBufUtils.readUTF8(buf));
    }

    /**
     * Write an encoded chat message to the buffer.
     *
     * @param buf  The buffer.
     * @param text The chat message to write.
     * @throws IOException on write failure.
     */
    public static void writeChat(ByteBuf buf, TextMessage text) throws IOException {
        ByteBufUtils.writeUTF8(buf, text.encode());
    }

    public static EquipmentSlot readHand(ByteBuf buf) throws IOException {
        int ordinal = ByteBufUtils.readVarInt(buf);
        return ordinal == 1 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
    }

    public static void writeHand(ByteBuf buf, EquipmentSlot hand) {
        Preconditions.checkArgument(hand == EquipmentSlot.HAND || hand == EquipmentSlot.OFF_HAND,
            "hand can be only OFF_HAND or HAND");
        ByteBufUtils.writeVarInt(buf, hand == EquipmentSlot.OFF_HAND ? 1 : 0);
    }

    /**
     * Write a Particle to the buffer.
     *
     * @param buf      The buffer
     * @param particle The Particle to write.
     * @throws IOException on write failure.
     */
    public static void writeParticle(ByteBuf buf, ParticleBuilder particle) throws IOException {
        int particleId = GlowParticle.getId(particle.particle());
        Object data = particle.data();

        ByteBufUtils.writeVarInt(buf, particleId);
        Class<?> dataType = particle.particle().getDataType();
        if (data != null && !particle.particle().getDataType().equals(Void.class)
            && particle.particle().getDataType().isInstance(data)) {
            if (dataType.equals(Particle.DustOptions.class)) {
                Particle.DustOptions options = (Particle.DustOptions) data;
                buf.writeFloat(options.getColor().getRed() / 255.0F);
                buf.writeFloat(options.getColor().getGreen() / 255.0F);
                buf.writeFloat(options.getColor().getBlue() / 255.0F);
                buf.writeFloat(options.getSize());
            } else if (dataType.equals(ItemStack.class)) {
                ItemStack stack = (ItemStack) data;
                writeSlot(buf, stack);
            } else if (dataType.equals(BlockState.class)) {
                BlockState state = (BlockState) data;
                // todo: convert state to int
                ByteBufUtils.writeVarInt(buf, 0);
            }
        }
    }

    public static void writeBitSet(ByteBuf buf, BitSet bitSet) {
        long[] longs = bitSet.toLongArray();
        ByteBufUtils.writeVarInt(buf, longs.length);
        for (long aLong : longs) {
            buf.writeLong(aLong);
        }
    }

    @SneakyThrows
    public static NamespacedKey readNamespacedKey(ByteBuf buf) {
        String raw = ByteBufUtils.readUTF8(buf);
        return NamespacedKey.fromString(raw);
    }

    @SneakyThrows
    public static void writeNamespacedKey(ByteBuf buf, NamespacedKey key) {
        ByteBufUtils.writeUTF8(buf, key.toString());
    }

    public static GlobalPosition readGlobalPos(ByteBuf buf) {
        NamespacedKey key = readNamespacedKey(buf);
        BlockVector position = readBlockPosition(buf);
        return new GlobalPosition(key, position);
    }

    public static void writeGlobalPos(ByteBuf buf, GlobalPosition globalPosition) {
        writeNamespacedKey(buf, globalPosition.getWorld());
        writeBlockPosition(buf, globalPosition.getPosition());
    }
}
