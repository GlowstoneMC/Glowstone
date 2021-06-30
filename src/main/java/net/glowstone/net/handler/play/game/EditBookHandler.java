package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.EditBookMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public final class EditBookHandler implements MessageHandler<GlowSession, EditBookMessage> {
    @Override
    public void handle(GlowSession session, EditBookMessage message) {
        GlowPlayer player = session.getPlayer();
        ItemStack item = message.getBook();

        if (!isWritableBook(item)) {
            return;
        }
        BookMeta book = (BookMeta) item.getItemMeta();

        // Ignore if book is empty
        if (!book.hasPages()) {
            return;
        }

        // If signing, the book should have a title.
        if (message.isSigning() && !book.hasTitle()) {
            return;
        }

        // Verify item in hand is also a book
        ItemStack inHand = message.getHand() == 0
            ? player.getInventory().getItemInMainHand()
            : player.getInventory().getItemInOffHand();
        if (!isWritableBook(inHand)) {
            return;
        }
        BookMeta bookInHand = (BookMeta) inHand.getItemMeta();

        // Update meta of the book in hand
        if (message.isSigning()) {
            bookInHand.setAuthor(book.getAuthor());
            bookInHand.setTitle(book.getTitle());
            bookInHand.setPages(book.getPages());
            inHand.setType(Material.WRITTEN_BOOK);
        } else {
            bookInHand.setPages(book.getPages());
        }

        // Update the book in inventory.
        inHand.setItemMeta(bookInHand);
        if (message.getHand() == 0) {
            player.getInventory().setItemInMainHand(inHand);
        } else {
            player.getInventory().setItemInOffHand(inHand);
        }
    }

    private boolean isWritableBook(ItemStack item) {
        return item != null
            && item.getType() == Material.WRITABLE_BOOK
            && item.getItemMeta() instanceof BookMeta;
    }
}
