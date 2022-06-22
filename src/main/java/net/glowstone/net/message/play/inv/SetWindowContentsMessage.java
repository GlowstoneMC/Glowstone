package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class SetWindowContentsMessage implements Message {

    private final int id;
    private final int stateId;
    private final ItemStack[] items;
    private final ItemStack currentItem;

}
