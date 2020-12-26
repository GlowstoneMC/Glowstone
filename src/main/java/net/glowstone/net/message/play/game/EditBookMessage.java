package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class EditBookMessage implements Message {

    private final ItemStack book;
    private final boolean signing;
    private final int hand;
}
