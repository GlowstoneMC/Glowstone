package net.glowstone.util;

import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import net.glowstone.util.nbt.Tag;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Contains several {@link ByteBuf}-related utility methods.
* @author Graham Edgecombe
*/
public final class TagCompoundUtils {

    /**
     * The UTF-8 character set.
     */
    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    /**
     * Writes a list of parameters (e.g. mob metadata) to the buffer.
     * @param buf The buffer.
     * @param parameters The parameters.
     */
    @SuppressWarnings("unchecked")
    public static void writeParameters(ByteBuf buf, List<Parameter<?>> parameters) throws IOException{
        for (Parameter<?> parameter : parameters) {
            int type  = parameter.getType();
            int index = parameter.getIndex();

            buf.writeByte((type << 5) | index);

            switch (type) {
            case Parameter.TYPE_BYTE:
                buf.writeByte(((Parameter<Byte>) parameter).getValue());
                break;
            case Parameter.TYPE_SHORT:
                buf.writeShort(((Parameter<Short>) parameter).getValue());
                break;
            case Parameter.TYPE_INT:
                buf.writeInt(((Parameter<Integer>) parameter).getValue());
                break;
            case Parameter.TYPE_FLOAT:
                buf.writeFloat(((Parameter<Float>) parameter).getValue());
                break;
            case Parameter.TYPE_STRING:
                ByteBufUtils.writeUTF8(buf, ((Parameter<String>) parameter).getValue());
                break;
            case Parameter.TYPE_ITEM:
                ItemStack item = ((Parameter<ItemStack>) parameter).getValue();
                buf.writeShort(item.getTypeId());
                buf.writeByte(item.getAmount());
                buf.writeShort(item.getDurability());
                break;
            }
        }

        buf.writeByte(127);
    }

    /**
     * Reads a list of parameters from the buffer.
     * @param buf The buffer.
     * @return The parameters.
     */
    public static List<Parameter<?>> readParameters(ByteBuf buf) throws IOException{
        List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

        for (int b = buf.readUnsignedByte(); b != 127; ) {
            int type  = (b & 0x0E) >> 5;
            int index = b & 0x1F;

            switch (type) {
            case Parameter.TYPE_BYTE:
                parameters.add(new Parameter<Byte>(type, index, buf.readByte()));
                break;
            case Parameter.TYPE_SHORT:
                parameters.add(new Parameter<Short>(type, index, buf.readShort()));
                break;
            case Parameter.TYPE_INT:
                parameters.add(new Parameter<Integer>(type, index, buf.readInt()));
                break;
            case Parameter.TYPE_FLOAT:
                parameters.add(new Parameter<Float>(type, index, buf.readFloat()));
                break;
            case Parameter.TYPE_STRING:
                parameters.add(new Parameter<String>(type, index, ByteBufUtils.readUTF8(buf)));
                break;
            case Parameter.TYPE_ITEM:
                int id = buf.readShort();
                int count = buf.readByte();
                short damage = buf.readShort();
                ItemStack item = new ItemStack(id, count, damage);
                parameters.add(new Parameter<ItemStack>(type, index, item));
                break;
            }
        }

        return parameters;
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

    /**
     * Default private constructor to prevent instantiation.
     */
    private TagCompoundUtils() {

    }
}
