package net.glowstone.block.itemtype;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemWrittenBook extends ItemType {

    @Override
    public Context getContext() {
        return Context.ANY;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        openBook(player);
    }

    @Override
    public void rightClickBlock(
            GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding,
            Vector clickedLoc, EquipmentSlot hand) {
        openBook(player);
    }

    private void openBook(GlowPlayer player) {
        ByteBuf buf = Unpooled.buffer();
        GlowBufUtils.writeHand(buf, player.getMainHand());
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        player.getSession().send(new PluginMessage("MC|BOpen", data));
    }
}
