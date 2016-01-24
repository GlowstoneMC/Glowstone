package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public final class BlockPlacementMessage implements Message {

    private final int x, y, z, direction, hand, cursorX, cursorY, cursorZ;

}
