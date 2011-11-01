package net.glowstone.net.codec;

import java.io.IOException;
import java.util.Map;

import net.glowstone.block.ItemProperties;
import net.glowstone.inventory.GlowItemStack;
import net.glowstone.util.ChannelBufferUtils;
import net.glowstone.util.nbt.Tag;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.SetWindowSlotsMessage;

public final class SetWindowSlotsCodec extends MessageCodec<SetWindowSlotsMessage> {

    public SetWindowSlotsCodec() {
        super(SetWindowSlotsMessage.class, 0x68);
    }

    @Override
    public SetWindowSlotsMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readUnsignedByte();
        int count = buffer.readUnsignedShort();
        GlowItemStack[] items = new GlowItemStack[count];
        for (int slot = 0; slot < count; slot++) {
            int item = buffer.readUnsignedShort();
            if (item == 0xFFFF) {
                items[slot] = null;
            } else {
                int itemCount = buffer.readUnsignedByte();
                int damage = buffer.readUnsignedByte();
                Map<String, Tag> nbtData = null;
                if (item > 255) {
                    ItemProperties props = ItemProperties.get(item);
                    if (props != null && props.hasNbtData()) ChannelBufferUtils.readCompound(buffer);
                }
                items[slot] = new GlowItemStack(item, itemCount, (short) damage, nbtData);
            }
        }
        return new SetWindowSlotsMessage(id, items);
    }

    @Override
    public ChannelBuffer encode(SetWindowSlotsMessage message) throws IOException {
        GlowItemStack[] items = message.getItems();

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeByte(message.getId());
        buffer.writeShort(items.length);
        for (int slot = 0; slot < items.length; slot++) {
            GlowItemStack item = items[slot];
            if (item == null) {
                buffer.writeShort(-1);
            } else {
                buffer.writeShort(item.getTypeId());
                buffer.writeByte(item.getAmount());
                buffer.writeByte(item.getDurability());
                if (item.getTypeId() > 255) {
                    ItemProperties props = ItemProperties.get(item.getTypeId());
                    if (props != null && props.hasNbtData()) ChannelBufferUtils.writeCompound(buffer, item.getNbtData());
                }
            }
        }

        return buffer;
    }

}
