package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.inv.EditBookMessage;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public final class EditBookCodec implements Codec<EditBookMessage> {
    @Override
    public EditBookMessage decode(ByteBuf byteBuf) throws IOException {
        ItemStack slot = GlowBufUtils.readSlot(byteBuf);
        boolean isSigning = byteBuf.readBoolean();
        int hand = byteBuf.readInt();
        return new EditBookMessage(slot, isSigning, hand);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, EditBookMessage message) {
        GlowBufUtils.writeSlot(byteBuf, message.getNewBook());
        byteBuf.writeBoolean(message.isSigning());
        byteBuf.writeInt(message.getHand());
        return byteBuf;
    }
}
