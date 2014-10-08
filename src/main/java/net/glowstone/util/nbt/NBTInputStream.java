package net.glowstone.util.nbt;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object
 * graph of subclasses of the {@link Tag} object.
 * <p/>
 * The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NBTInputStream implements Closeable {

    /**
     * The data input stream.
     */
    private final DataInputStream is;

    /**
     * Creates a new NBTInputStream, which will source its data
     * from the specified input stream. This assumes the stream is compressed.
     * @param is The input stream.
     * @throws IOException if an I/O error occurs.
     */
    public NBTInputStream(InputStream is) throws IOException {
        this(is, true);
    }

    /**
     * Creates a new NBTInputStream, which sources its data from the
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
     * Reads the root NBT {@link CompoundTag} from the stream.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    public CompoundTag readCompound() throws IOException {
        // read type
        TagType type = TagType.byIdOrError(is.readUnsignedByte());
        if (type != TagType.COMPOUND) {
            throw new IOException("Root of NBTInputStream was " + type + ", not COMPOUND");
        }

        // for now, throw away name
        int nameLength = is.readUnsignedShort();
        is.skipBytes(nameLength);

        // read tag
        return (CompoundTag) readTagPayload(type, 0);
    }

    private CompoundTag readCompound(int depth) throws IOException {
        CompoundTag result = new CompoundTag();

        while (true) {
            // read type
            TagType type = TagType.byIdOrError(is.readUnsignedByte());
            if (type == TagType.END) {
                break;
            }

            // read name
            int nameLength = is.readUnsignedShort();
            byte[] nameBytes = new byte[nameLength];
            is.readFully(nameBytes);
            String name = new String(nameBytes, StandardCharsets.UTF_8);

            // read tag
            Tag tag = readTagPayload(type, depth + 1);

            result.put(name, tag);
        }

        return result;
    }

    /**
     * Reads the payload of a {@link Tag}, given the name and type.
     * @param type The type.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    private Tag readTagPayload(TagType type, int depth) throws IOException {
        switch (type) {
            case BYTE:
                return new ByteTag(is.readByte());

            case SHORT:
                return new ShortTag(is.readShort());

            case INT:
                return new IntTag(is.readInt());

            case LONG:
                return new LongTag(is.readLong());

            case FLOAT:
                return new FloatTag(is.readFloat());

            case DOUBLE:
                return new DoubleTag(is.readDouble());

            case BYTE_ARRAY:
                int length = is.readInt();
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return new ByteArrayTag(bytes);

            case STRING:
                length = is.readShort();
                bytes = new byte[length];
                is.readFully(bytes);
                return new StringTag(new String(bytes, StandardCharsets.UTF_8));

            case LIST:
                TagType childType = TagType.byIdOrError(is.readUnsignedByte());
                length = is.readInt();

                List<Tag> tagList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    tagList.add(readTagPayload(childType, depth + 1));
                }

                return new ListTag(childType, tagList);

            case COMPOUND:
                return readCompound(depth + 1);

            case INT_ARRAY:
                length = is.readInt();
                int[] ints = new int[length];
                for (int i = 0; i < length; ++i) {
                    ints[i] = is.readInt();
                }
                return new IntArrayTag(ints);

            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

}

