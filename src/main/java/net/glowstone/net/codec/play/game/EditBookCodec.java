package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.EditBookMessage;
import org.bukkit.inventory.ItemStack;

public final class EditBookCodec implements Codec<EditBookMessage> {

    @Override
    public EditBookMessage decode(ByteBuf buf) throws IOException {
        ItemStack item = GlowBufUtils.readSlot(buf);
        boolean signing = buf.readBoolean();
        int hand = ByteBufUtils.readVarInt(buf);
        return new EditBookMessage(item, signing, hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EditBookMessage message) throws IOException {
        GlowBufUtils.writeSlot(buf, message.getBook());
        buf.writeBoolean(message.isSigning());
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
