package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.CraftRecipeResponseMessage;

public final class CraftRecipeResponseCodec implements Codec<CraftRecipeResponseMessage> {

    @Override
    public CraftRecipeResponseMessage decode(ByteBuf buf) throws IOException {
        int windowId = buf.readByte();
        int recipeId = ByteBufUtils.readVarInt(buf);
        return new CraftRecipeResponseMessage(windowId, recipeId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CraftRecipeResponseMessage message) throws IOException {
        buf.writeByte(message.getWindowId());
        ByteBufUtils.writeVarInt(buf, message.getRecipeId());
        return buf;
    }
}
