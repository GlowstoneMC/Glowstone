package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class BlockPlacementMessage implements Message {

    private final int x, y, z, direction;
    private final ItemStack heldItem;
    private final int cursorX, cursorY, cursorZ;

}
