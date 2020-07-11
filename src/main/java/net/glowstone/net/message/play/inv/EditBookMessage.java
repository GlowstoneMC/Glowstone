package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class EditBookMessage implements Message {

    public final static int MAIN_HAND = 0, OFF_HAND = 1;

    private final ItemStack newBook;
    private final boolean isSigning;
    private final int hand;
}
