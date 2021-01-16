package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.inventory.GlowAnvilInventory;
import net.glowstone.inventory.GlowBeaconInventory;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class PluginMessageHandler implements MessageHandler<GlowSession, PluginMessage> {

    @Override
    public void handle(GlowSession session, PluginMessage message) {
        String channel = message.getChannel();

        // register and unregister: NUL-separated list of channels

        if (channel.equals("REGISTER")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " registered channel: " + regChannel);
                session.getPlayer().addChannel(regChannel);
            }
        } else if (channel.equals("UNREGISTER")) {
            for (String regChannel : string(message.getData()).split("\0")) {
                GlowServer.logger.info(session + " unregistered channel: " + regChannel);
                session.getPlayer().removeChannel(regChannel);
            }
        } else if (channel.startsWith("MC|")) {
            // internal Minecraft channels
            handleInternal(session, channel, message.getData());
        } else {
            session.getServer().getMessenger()
                .dispatchIncomingMessage(session.getPlayer(), channel, message.getData());
        }
    }

    private void handleInternal(GlowSession session, String channel, byte... data) {
        /*
        MC|Brand
            entire data: string of client's brand (e.g. "vanilla")
        MC|BEdit
            item stack: new book item (should be verified)
        MC|BSign
            item stack: new book item (should be verified)
        MC|TrSel
            int: villager trade to select
        MC|AdvCdm
            byte: mode
            if 0:
                int x, int y, int z (command block in world)
            if 1:
                int entity (command block minecart)
            string: command to set
        MC|Beacon
            two ints, presumably the selected enchants
        MC|ItemName
            entire data: name to apply to item in anvil
         */

        ByteBuf buf = null;
        try {
            buf = Unpooled.wrappedBuffer(data);
            switch (channel) {
                case "MC|Brand":
                    // vanilla server doesn't handle this, for now just log it
                    String brand = null;
                    try {
                        brand = ByteBufUtils.readUTF8(buf);
                    } catch (IOException e) {
                        GlowServer.logger
                            .log(Level.WARNING, "Error reading client brand of " + session, e);
                    }
                    if (brand != null && !brand.equals("vanilla")) {
                        GlowServer.logger
                            .info("Client brand of " + session.getPlayer().getName() + " is: "
                                + brand);
                    }
                    break;
                case "MC|BEdit": {
                    // read and verify stack
                    ItemStack item = GlowBufUtils.readSlot(buf);
                    //GlowServer.logger.info(
                    //        "BookEdit [" + session.getPlayer().getName() + "]: " + item);
                    if (item == null || item.getType() != Material.BOOK_AND_QUILL) {
                        return;
                    }
                    ItemMeta meta = item.getItemMeta();
                    if (!(meta instanceof BookMeta)) {
                        return;
                    }
                    BookMeta book = (BookMeta) meta;
                    if (!book.hasPages()) {
                        return;
                    }

                    // verify item in hand
                    ItemStack inHand = session.getPlayer().getItemInHand();
                    if (inHand == null || inHand.getType() != Material.BOOK_AND_QUILL) {
                        return;
                    }
                    ItemMeta handMeta = inHand.getItemMeta();
                    if (!(handMeta instanceof BookMeta)) {
                        return;
                    }
                    BookMeta handBook = (BookMeta) handMeta;

                    // apply pages to book
                    handBook.setPages(book.getPages());
                    inHand.setItemMeta(handBook);
                    session.getPlayer().setItemInHand(inHand);
                    break;
                }
                case "MC|BSign":
                    // read and verify stack
                    ItemStack item = GlowBufUtils.readSlot(buf);
                    //GlowServer.logger.info(
                    //        "BookSign [" + session.getPlayer().getName() + "]: " + item);
                    if (item == null || item.getType() != Material.BOOK_AND_QUILL) {
                        return;
                    }
                    ItemMeta meta = item.getItemMeta();
                    if (!(meta instanceof BookMeta)) {
                        return;
                    }
                    BookMeta book = (BookMeta) meta;
                    if (!book.hasPages() || !book.hasTitle()) {
                        return;
                    }

                    // verify item in hand
                    ItemStack inHand = session.getPlayer().getItemInHand();
                    if (inHand == null || inHand.getType() != Material.BOOK_AND_QUILL) {
                        return;
                    }
                    ItemMeta handMeta = inHand.getItemMeta();
                    if (!(handMeta instanceof BookMeta)) {
                        return;
                    }
                    BookMeta handBook = (BookMeta) handMeta;

                    // apply pages, title, and author to book
                    handBook.setAuthor(session.getPlayer().getName());
                    handBook.setTitle(book.getTitle());
                    handBook.setPages(book.getPages());
                    inHand.setType(Material.WRITTEN_BOOK);
                    inHand.setItemMeta(handBook);
                    session.getPlayer().setItemInHand(inHand);
                    break;
                case "MC|ItemName":
                    if (session.getPlayer().getOpenInventory() == null) {
                        break;
                    }
                    // check if player is in an anvil inventory
                    if (session.getPlayer().getOpenInventory().getType() != InventoryType.ANVIL) {
                        break;
                    }
                    // get the new name for the item
                    String name;
                    try {
                        name = ByteBufUtils.readUTF8(buf);
                    } catch (IOException e) {
                        GlowServer.logger
                            .log(Level.WARNING, "Error reading anvil item name by " + session, e);
                        break;
                    }
                    ((GlowAnvilInventory) session.getPlayer().getOpenInventory().getTopInventory())
                        .setRenameText(name);
                    break;
                case "MC|Beacon": {
                    Player player = session.getPlayer();

                    if (player.getOpenInventory() == null || player.getOpenInventory().getType() != InventoryType.BEACON) {
                        break;
                    }

                    GlowBeaconInventory inventory = (GlowBeaconInventory) player.getOpenInventory().getTopInventory();
                    inventory.setActiveEffects(buf.readInt(), buf.readInt());

                    break;
                }
                default:
                    GlowServer.logger.info(session + " used unknown Minecraft channel: " + channel);
                    break;
            }
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
    }

    private String string(byte... data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
