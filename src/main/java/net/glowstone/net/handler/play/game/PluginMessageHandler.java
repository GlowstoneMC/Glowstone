package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.state.GlowCommandBlock;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.Material;
import org.bukkit.block.CommandBlockType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

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
            session.getServer().getMessenger().dispatchIncomingMessage(session.getPlayer(), channel, message.getData());
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

        ByteBuf buf = Unpooled.wrappedBuffer(data);
        switch (channel) {
            case "MC|Brand":
                // vanilla server doesn't handle this, for now just log it
                String brand = null;
                try {
                    brand = ByteBufUtils.readUTF8(buf);
                } catch (IOException e) {
                    GlowServer.logger.log(Level.WARNING, "Error reading client brand of " + session, e);
                }
                if (brand != null && !brand.equals("vanilla")) {
                    GlowServer.logger.info("Client brand of " + session.getPlayer().getName() + " is: " + brand);
                }
                break;
            case "MC|BEdit": {
                // read and verify stack
                ItemStack item = GlowBufUtils.readSlot(buf);
                //GlowServer.logger.info("BookEdit [" + session.getPlayer().getName() + "]: " + item);
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
                //GlowServer.logger.info("BookSign [" + session.getPlayer().getName() + "]: " + item);
                if (item == null || item.getType() != Material.WRITTEN_BOOK) {
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
            case "MC|AutoCmd":
                try {
                    int x = buf.readInt();
                    int y = buf.readInt();
                    int z = buf.readInt();
                    String command = ByteBufUtils.readUTF8(buf);
                    boolean trackOutput = buf.readBoolean();
                    String modeName = ByteBufUtils.readUTF8(buf);
                    boolean conditional = buf.readBoolean();
                    boolean automatic = buf.readBoolean();
                    GlowBlock block = session.getPlayer().getWorld().getBlockAt(x, y, z);
                    if (!(block.getState() instanceof GlowCommandBlock)) {
                        return;
                    }
                    if (!(block.getState().getData() instanceof Command)) {
                        return;
                    }
                    Command cmd = (Command) block.getState().getData();
                    GlowCommandBlock state = (GlowCommandBlock) block.getState();
                    state.setCommand(command);
                    state.setTrackOutput(trackOutput);
                    block.setType(CommandBlockType.valueOf(modeName).getMaterial());
                    state.setAuto(automatic);
                    cmd.setConditional(conditional);
                    state.update();
                    state.setData(cmd);
                    block.setData(cmd.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                GlowServer.logger.info(session + " used unknown Minecraft channel: " + channel);
                break;
        }
        buf.release();
    }

    private String string(byte... data) {
        return new String(data, StandardCharsets.UTF_8);
    }
}
