package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.CraftRecipeRequestMessage;

public final class CraftRecipeRequestCodec implements Codec<CraftRecipeRequestMessage> {

    @Override
    public CraftRecipeRequestMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int windowId = buf.readByte();
        int recipeId = ByteBufUtils.readVarInt(buf);
        boolean makeAll = buf.readBoolean();
        return new CraftRecipeRequestMessage(windowId, recipeId, makeAll);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, CraftRecipeRequestMessage message) throws IOException {
        buf.writeByte(message.getWindowId());
        ByteBufUtils.writeVarInt(buf, message.getRecipeId());
        buf.writeBoolean(message.isMakeAll());
        return buf;
    }
}
