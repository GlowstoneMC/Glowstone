package net.glowstone.net;

import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import net.glowstone.util.nbt.Tag;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Contains several utility methods for writing special data types to @{link ByteBuf}s.
 */
public final class GlowBufUtils {

    /**
     * Writes a list of mob metadata entries to the buffer.
     * @param buf The buffer.
     * @param entries The metadata.
     */
    public static void writeMetadata(ByteBuf buf, List<MetadataMap.Entry> entries) throws IOException {
        for (MetadataMap.Entry entry : entries) {
            MetadataIndex index = entry.index;
            Object value = entry.value;

            if (value == null) continue;

            int type = index.getType().getId();
            int id = index.getIndex();
            buf.writeByte((type << 5) | id);

            switch (index.getType()) {
                case BYTE:
                    buf.writeByte((Byte) value);
                    break;
                case SHORT:
                    buf.writeShort((Short) value);
                    break;
                case INT:
                    buf.writeInt((Integer) value);
                    break;
                case FLOAT:
                    buf.writeFloat((Float) value);
                    break;
                case STRING:
                    ByteBufUtils.writeUTF8(buf, (String) value);
                    break;
                case ITEM:
                    writeSlot(buf, (ItemStack) value);
                    break;
            }
        }

        buf.writeByte(127);
    }

    public static Map<String, Tag> readCompound(ByteBuf buf) {
        int len = buf.readShort();
        if (len >= 0) {
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            NBTInputStream str = null;
            try {
                str = new NBTInputStream(new ByteArrayInputStream(bytes));
                Tag tag = str.readTag();
                if (tag instanceof CompoundTag) {
                    return ((CompoundTag) tag).getValue();
                }
            } catch (IOException e) {
            } finally {
                if (str != null) {
                    try {
                        str.close();
                    } catch (IOException e) {}
                }
            }
        }
        return null;
    }

    public static void writeCompound(ByteBuf buf, Map<String, Tag> data) {
        if (data == null) {
            buf.writeShort(-1);
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NBTOutputStream str = null;
        try {
            str = new NBTOutputStream(out);
            str.writeTag(new CompoundTag("", data));
            str.close();
            str = null;
            buf.writeShort(out.size());
            buf.writeBytes(out.toByteArray());
        } catch (IOException e) {
        } finally {
            if (str != null) {
                try {
                    str.close();
                } catch (IOException e) {}
            }
        }

    }

    public static void writeSlot(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.getTypeId() == 0) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(stack.getTypeId());
            buf.writeByte(stack.getAmount());
            buf.writeShort(stack.getDurability());
            writeCompound(buf, null); // todo - build data from stack
        }
    }

    public static ItemStack readSlot(ByteBuf buf) {
        short type = buf.readShort();
        if (type == -1) {
            return null;
        }

        int amount = buf.readUnsignedByte();
        short durability = buf.readShort();

        Map<String, Tag> tags = readCompound(buf); // todo - use this

        return new ItemStack(type, amount, durability);
    }

    private GlowBufUtils() {}
}
