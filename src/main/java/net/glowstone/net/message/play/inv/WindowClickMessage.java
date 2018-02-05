package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class WindowClickMessage implements Message {

    private final int id;
    private final int slot;
    private final int button;
    private final int transaction;
    private final int mode;
    private final ItemStack item;

}
