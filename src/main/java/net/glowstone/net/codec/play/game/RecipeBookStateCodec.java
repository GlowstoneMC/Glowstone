package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.RecipeBookStateMessage;

public final class RecipeBookStateCodec implements Codec<RecipeBookStateMessage> {

    @Override
    public RecipeBookStateMessage decode(ByteBuf buf) throws IOException {
        RecipeBookStateMessage.RecipeBookType book =
            RecipeBookStateMessage.RecipeBookType.fromOrdinal(ByteBufUtils.readVarInt(buf));
        boolean bookOpen = buf.readBoolean();
        boolean filterOpen = buf.readBoolean();
        return new RecipeBookStateMessage(book, bookOpen, filterOpen);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RecipeBookStateMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getBook().ordinal());
        buf.writeBoolean(message.isBookOpen());
        buf.writeBoolean(message.isFilterOpen());
        return buf;
    }
}
