package net.glowstone.util.nbt;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * This class reads NBT, or Named Binary Tag streams, and produces an object graph of subclasses of
 * the {@link Tag} object.
 *
 * <p>The NBT format was created by Markus Persson, and the specification may be found at <a href="http://www.minecraft.net/docs/NBT.txt"> http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NbtInputStream implements Closeable {

    /**
     * The data input stream.
     */
    private final DataInputStream is;

    /**
     * Creates a new NBTInputStream, which will source its data from the specified input stream.
     *
     * <p>This assumes the stream is compressed.
     *
     * @param is The input stream.
     * @throws IOException if an I/O error occurs.
     */
    public NbtInputStream(InputStream is) throws IOException {
        this(is, true);
    }

    /**
     * Creates a new NBTInputStream, which sources its data from the specified input stream.
     *
     * <p>A flag must be passed which indicates if the stream is compressed with GZIP or not.
     *
     * @param is         The input stream.
     * @param compressed A flag indicating if the stream is compressed.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("resource")
    public NbtInputStream(InputStream is, boolean compressed) throws IOException {
        this.is = new DataInputStream(compressed ? new GZIPInputStream(is) : is);
    }

    /**
     * Reads the root NBT {@link CompoundTag} from the stream.
     *
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    public CompoundTag readCompound() throws IOException {
        return readCompound(NbtReadLimiter.UNLIMITED);
    }

    /**
     * Reads the root NBT {@link CompoundTag} from the stream.
     *
     * @param readLimiter The read limiter to prevent overflow when reading the NBT data.
     * @return The tag that was read.
     * @throws IOException if an I/O error occurs.
     */
    public CompoundTag readCompound(NbtReadLimiter readLimiter) throws IOException {
        // read type
        TagType type = TagType.byIdOrError(is.readUnsignedByte());
        if (type != TagType.COMPOUND) {
            throw new IOException("Root of NBTInputStream was " + type + ", not COMPOUND");
        }

        // for now, throw away name
        int nameLength = is.readUnsignedShort();
        is.skipBytes(nameLength);

        // read tag
        return (CompoundTag) readTagPayload(type, 0, readLimiter);
    }

    private CompoundTag readCompound(int depth, NbtReadLimiter readLimiter) throws IOException {
        CompoundTag result = new CompoundTag();

        while (true) {
            // read type
            TagType type = TagType.byIdOrError(is.readUnsignedByte());
            if (type == TagType.END) {
                break;
            }

            // read name
            String name = is.readUTF();
            readLimiter.read(28 + 2 * name.length());

            // read tag
            Tag tag = readTagPayload(type, depth + 1, readLimiter);
            readLimiter.read(36);
            result.put(name, tag);
        }

        return result;
    }

    /**
     * Reads the payload of a {@link Tag}, given the name and type.
     *
     * @param type  The type.
     * @param depth The depth.
     * @return The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    private Tag readTagPayload(TagType type, int depth, NbtReadLimiter readLimiter)
        throws IOException {

        if (depth > 512) {
            throw new IllegalStateException(
                "Tried to read NBT tag with too high complexity, depth > 512");
        }

        switch (type) {
            case BYTE:
                readLimiter.read(1);
                return new ByteTag(is.readByte());

            case SHORT:
                readLimiter.read(2);
                return new ShortTag(is.readShort());

            case INT:
                readLimiter.read(4);
                return new IntTag(is.readInt());

            case LONG:
                readLimiter.read(8);
                return new LongTag(is.readLong());

            case FLOAT:
                readLimiter.read(4);
                return new FloatTag(is.readFloat());

            case DOUBLE:
                readLimiter.read(8);
                return new DoubleTag(is.readDouble());

            case BYTE_ARRAY:
                readLimiter.read(24);
                int length = is.readInt();
                readLimiter.read(length);
                byte[] bytes = new byte[length];
                is.readFully(bytes);
                return new ByteArrayTag(bytes);

            case STRING:
                readLimiter.read(36);
                String s = is.readUTF();
                readLimiter.read(2 * s.length());
                return new StringTag(s);

            case LIST:
                readLimiter.read(37);
                TagType childType = TagType.byIdOrError(is.readUnsignedByte());
                length = is.readInt();
                readLimiter.read(4 * length);

                List<Tag> tagList = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    tagList.add(readTagPayload(childType, depth + 1, readLimiter));
                }

                return new ListTag(childType, tagList);

            case COMPOUND:
                readLimiter.read(48);
                return readCompound(depth + 1, readLimiter);

            case INT_ARRAY:
                readLimiter.read(37);
                length = is.readInt();
                readLimiter.read(4 * length);
                int[] ints = new int[length];
                for (int i = 0; i < length; ++i) {
                    ints[i] = is.readInt();
                }
                return new IntArrayTag(ints);

            case LONG_ARRAY:
                readLimiter.read(37);
                length = is.readInt();
                readLimiter.read(8 * length);
                long[] longs = new long[length];
                for (int i = 0; i < length; i++) {
                    longs[i] = is.readLong();
                }
                return new LongArrayTag(longs);

            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

}

