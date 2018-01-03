package net.glowstone.util.nbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

/**
 * This class writes NBT, or Named Binary Tag, {@link Tag} objects to an underlying
 * {@link OutputStream}.
 *
 * <p>The NBT format was created by Markus Persson, and the specification may be found at <a href="http://www.minecraft.net/docs/NBT.txt"> http://www.minecraft.net/docs/NBT.txt</a>.
 */
public final class NbtOutputStream implements Closeable {

    /**
     * The output stream.
     */
    private final DataOutputStream os;

    /**
     * Creates a new NBTOutputStream, which will write data to the specified underlying output
     * stream. This assumes the output stream should be compressed with GZIP.
     *
     * @param os The output stream.
     * @throws IOException if an I/O error occurs.
     */
    public NbtOutputStream(OutputStream os) throws IOException {
        this(os, true);
    }

    /**
     * Creates a new NBTOutputStream, which will write data to the specified underlying output
     * stream. A flag indicates if the output should be compressed with GZIP or not.
     *
     * @param os The output stream.
     * @param compressed A flag that indicates if the output should be compressed.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("resource")
    public NbtOutputStream(OutputStream os, boolean compressed) throws IOException {
        this.os = new DataOutputStream(compressed ? new GZIPOutputStream(os) : os);
    }

    /**
     * Write a tag with a blank name (the root tag) to the stream.
     *
     * @param tag The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    public void writeTag(Tag tag) throws IOException {
        writeTag("", tag);
    }

    /**
     * Write a tag with a name.
     *
     * @param name The name to give the written tag.
     * @param tag The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTag(String name, Tag tag) throws IOException {
        TagType type = tag.getType();
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

        if (type == TagType.END) {
            throw new IOException("Named TAG_End not permitted.");
        }

        os.writeByte(type.getId());
        os.writeShort(nameBytes.length);
        os.write(nameBytes);

        writeTagPayload(tag);
    }

    /**
     * Writes tag payload.
     *
     * @param tag The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    private void writeTagPayload(Tag tag) throws IOException {
        TagType type = tag.getType();
        byte[] bytes;

        switch (type) {
            case BYTE:
                os.writeByte((byte) tag.getValue());
                break;

            case SHORT:
                os.writeShort((short) tag.getValue());
                break;

            case INT:
                os.writeInt((int) tag.getValue());
                break;

            case LONG:
                os.writeLong((long) tag.getValue());
                break;

            case FLOAT:
                os.writeFloat((float) tag.getValue());
                break;

            case DOUBLE:
                os.writeDouble((double) tag.getValue());
                break;

            case BYTE_ARRAY:
                bytes = (byte[]) tag.getValue();
                os.writeInt(bytes.length);
                os.write(bytes);
                break;

            case STRING:
                bytes = ((StringTag) tag).getValue().getBytes(StandardCharsets.UTF_8);
                os.writeShort(bytes.length);
                os.write(bytes);
                break;

            case LIST:
                ListTag<Tag> listTag = (ListTag<Tag>) tag;
                List<Tag> tags = listTag.getValue();

                os.writeByte(listTag.getChildType().getId());
                os.writeInt(tags.size());
                for (Tag child : tags) {
                    writeTagPayload(child);
                }
                break;

            case COMPOUND:
                Map<String, Tag> map = ((CompoundTag) tag).getValue();
                for (Entry<String, Tag> entry : map.entrySet()) {
                    writeTag(entry.getKey(), entry.getValue());
                }
                os.writeByte((byte) 0); // end tag
                break;

            case INT_ARRAY:
                int[] ints = (int[]) tag.getValue();
                os.writeInt(ints.length);
                for (int value : ints) {
                    os.writeInt(value);
                }
                break;

            default:
                throw new IOException("Invalid tag type: " + type + ".");
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

}

