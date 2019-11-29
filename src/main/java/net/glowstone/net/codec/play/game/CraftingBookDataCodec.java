package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.CraftingBookDataMessage;

public final class CraftingBookDataCodec implements Codec<CraftingBookDataMessage> {

    @Override
    public CraftingBookDataMessage decode(ByteBuf buf) throws IOException {
        int type = ByteBufUtils.readVarInt(buf);
        if (type == CraftingBookDataMessage.TYPE_DISPLAYED_RECIPE) {
            int recipeId = buf.readInt();
            return new CraftingBookDataMessage(type, recipeId);
        } else if (type == CraftingBookDataMessage.TYPE_STATUS) {
            boolean bookOpen = buf.readBoolean();
            boolean filterOpen = buf.readBoolean();
            boolean smeltingBookOpen = buf.readBoolean();
            boolean smeltingFilterOpen = buf.readBoolean();
            return new CraftingBookDataMessage(type, bookOpen, filterOpen,
                    smeltingBookOpen, smeltingFilterOpen);
        }
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CraftingBookDataMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getType());
        if (message.getType() == CraftingBookDataMessage.TYPE_DISPLAYED_RECIPE) {
            buf.writeInt(message.getRecipeId());
        } else if (message.getType() == CraftingBookDataMessage.TYPE_STATUS) {
            buf.writeBoolean(message.isBookOpen());
            buf.writeBoolean(message.isFilterOpen());
            buf.writeBoolean(message.isSmeltingBookOpen());
            buf.writeBoolean(message.isSmeltingFilterOpen());
        }
        return buf;
    }
}
