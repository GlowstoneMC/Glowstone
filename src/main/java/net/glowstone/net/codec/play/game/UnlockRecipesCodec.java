package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.UnlockRecipesMessage;

import java.io.IOException;

public final class UnlockRecipesCodec implements Codec<UnlockRecipesMessage> {

    @Override
    public UnlockRecipesMessage decode(ByteBuf buf) throws IOException {
        int action = ByteBufUtils.readVarInt(buf);
        boolean craftingBookOpen = buf.readBoolean();
        boolean craftingBookFilter = buf.readBoolean();
        boolean smeltingBookOpen = buf.readBoolean();
        boolean smeltingBookFilter = buf.readBoolean();
        boolean blastBookOpen = buf.readBoolean();
        boolean blastBookFilter = buf.readBoolean();
        boolean smokerBookOpen = buf.readBoolean();
        boolean smokerBookFilter = buf.readBoolean();

        int sizeOfRecipes = ByteBufUtils.readVarInt(buf);
        int[] recipes = new int[sizeOfRecipes];
        for (int i = 0; i < sizeOfRecipes; i++) {
            recipes[i] = ByteBufUtils.readVarInt(buf);
        }
        if (action != UnlockRecipesMessage.ACTION_INIT) {
            return new UnlockRecipesMessage(action, craftingBookOpen, craftingBookFilter,
                    smeltingBookOpen, smeltingBookFilter, blastBookOpen, blastBookFilter,
                    smokerBookOpen, smokerBookFilter, recipes);
        }
        // action = INIT (0)
        sizeOfRecipes = ByteBufUtils.readVarInt(buf);
        int[] allRecipes = new int[sizeOfRecipes];
        for (int i = 0; i < sizeOfRecipes; i++) {
            allRecipes[i] = ByteBufUtils.readVarInt(buf);
        }
        return new UnlockRecipesMessage(action, craftingBookOpen, craftingBookFilter,
                smeltingBookOpen, smeltingBookFilter, blastBookOpen, blastBookFilter,
                smokerBookOpen, smokerBookFilter, recipes, allRecipes);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UnlockRecipesMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getAction());
        buf.writeBoolean(message.isCraftingBookOpen());
        buf.writeBoolean(message.isCraftingBookFilter());
        buf.writeBoolean(message.isSmeltingBookOpen());
        buf.writeBoolean(message.isSmeltingBookFilter());
        buf.writeBoolean(message.isBlastBookOpen());
        buf.writeBoolean(message.isBlastBookFilter());
        buf.writeBoolean(message.isSmokerBookOpen());
        buf.writeBoolean(message.isSmokerBookFilter());
        ByteBufUtils.writeVarInt(buf, message.getRecipes().length);
        for (int recipe : message.getRecipes()) {
            ByteBufUtils.writeVarInt(buf, recipe);
        }
        if (message.getAction() == UnlockRecipesMessage.ACTION_INIT
            && message.getAllRecipes() != null) {
            ByteBufUtils.writeVarInt(buf, message.getAllRecipes().length);
            for (int recipe : message.getAllRecipes()) {
                ByteBufUtils.writeVarInt(buf, recipe);
            }
        }
        return buf;
    }
}
