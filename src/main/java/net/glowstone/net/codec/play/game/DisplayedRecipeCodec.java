package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.DisplayedRecipeMessage;

public final class DisplayedRecipeCodec implements Codec<DisplayedRecipeMessage> {

    @Override
    public DisplayedRecipeMessage decode(ByteBuf buf) throws IOException {
        String recipeId = ByteBufUtils.readUTF8(buf);
        return new DisplayedRecipeMessage(recipeId);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, DisplayedRecipeMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getRecipeId());
        return buf;
    }
}
