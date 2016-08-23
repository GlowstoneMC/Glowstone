package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class WindowClickPacket implements Message {

    private final int id, slot, button, transaction, mode;
    private final ItemStack item;

}
