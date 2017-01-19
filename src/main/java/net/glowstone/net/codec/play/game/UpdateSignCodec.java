package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class UpdateSignCodec implements Codec<UpdateSignMessage> {
    @Override
    public UpdateSignMessage decode(ByteBuf buf) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buf);
        BaseComponent[][] message = new BaseComponent[4][];
        for (int i = 0; i < 4; i++) {
            message[i] = GlowBufUtils.readChat(buf);
        }
        return new UpdateSignMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), message);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UpdateSignMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        for (BaseComponent[] line : message.getMessage()) {
            GlowBufUtils.writeChat(buf, line);
        }
        return buf;
    }
}
