package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class WindowSlotPacket implements Message {

    private final int id, slot;
    private final ItemStack item;

}
