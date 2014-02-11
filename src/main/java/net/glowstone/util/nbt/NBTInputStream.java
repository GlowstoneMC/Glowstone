package net.glowstone.util.nbt;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object
 * graph of subclasses of the {@link Tag} object.
 * <p />
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 * @author Graham Edgecombe
 */
public final class NBTInputStream implements Closeable {

    /**
     * The data input stream.
     */
    private final DataInputStream is;

    /**
     * Creates a new {@link NBTInputStream}, which will source its data
     * from the specified input stream. This assumes the stream is compressed.
     * @param is The input stream.
     * @throws IOException if an I/O error occurs.
     */
    public NBTInputStream(InputStream is) throws IOException {
        this(is, true);
    }

    /**
     * Creates a new {@link NBTInputStream}, which sources its data from the
     * specified input stream. A flag must be passed which indicates if the
     * stream is compressed with GZIP or not.
     * @param is The input stream.
     * @param compressed A flag indicating if the stream is compressed.
     * @throws IOException if an I/O error occurs.
     */
    public NBTInputStream(InputStream is, boolean compressed) throws IOException {
        this.is = new DataInputStream(compressed ? new GZIPInputStream(is) : is);
    }

    /**
     * Reads an NBT {@link Tag} from the stream.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    public Tag readTag() throws IOException {
        return readTag(0);
    }

    /**
     * Reads an NBT {@link Tag} from the stream.
     * @param depth The depth of this tag.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    private Tag readTag(int depth) throws IOException {
        TagType type = TagType.byIdOrError(is.readUnsignedByte());

        String name;
        if (type != TagType.END) {
            int nameLength = is.readUnsignedShort();
            byte[] nameBytes = new byte[nameLength];
            is.readFully(nameBytes);
            name = new String(nameBytes, StandardCharsets.UTF_8);
        } else {
            name = "";
        }

        return readTagPayload(type, name, depth);
    }

    /**
     * Reads the payload of a {@link Tag}, given the name and type.
     * @param type The type.
     * @param name The name.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    private Tag readTagPayload(TagType type, String name, int depth) throws IOException {
        switch (type) {
        case END:
            if (depth == 0) {
                throw new IOException("TAG_End found without a TAG_Compound/TAG_List tag preceding it.");
            } else {
                return new EndTag();
            }

        case BYTE:
            return new ByteTag(name, is.readByte());

        case SHORT:
            return new ShortTag(name, is.readShort());

        case INT:
            return new IntTag(name, is.readInt());

        case LONG:
            return new LongTag(name, is.readLong());

        case FLOAT:
            return new FloatTag(name, is.readFloat());

        case DOUBLE:
            return new DoubleTag(name, is.readDouble());

        case BYTE_ARRAY:
            int length = is.readInt();
            byte[] bytes = new byte[length];
            is.readFully(bytes);
            return new ByteArrayTag(name, bytes);

        case STRING:
            length = is.readShort();
            bytes = new byte[length];
            is.readFully(bytes);
            return new StringTag(name, new String(bytes, StandardCharsets.UTF_8));

        case LIST:
            TagType childType = TagType.byIdOrError(is.readUnsignedByte());
            length = is.readInt();

            List<Tag> tagList = new ArrayList<Tag>();
            for (int i = 0; i < length; i++) {
                tagList.add(readTagPayload(childType, "", depth + 1));
            }

            return new ListTag(name, childType, tagList);

        case COMPOUND:
            Map<String, Tag> tagMap = new LinkedHashMap<String, Tag>();
            while (true) {
                Tag tag = readTag(depth + 1);
                if (tag instanceof EndTag) {
                    break;
                } else {
                    tagMap.put(tag.getName(), tag);
                }
            }

            return new CompoundTag(name, tagMap);

        case INT_ARRAY:
            length = is.readInt();
            int[] ints = new int[length];
            for (int i = 0; i < length; ++i) {
                ints[i] = is.readInt();
            }
            return new IntArrayTag(name, ints);

        default:
            throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

}

