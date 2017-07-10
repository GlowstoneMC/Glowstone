package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.EquipmentSlot;

@Data
public final class BlockPlacementMessage implements Message {

    private final int x, y, z, direction, hand;
    private final float cursorX, cursorY, cursorZ;

    public EquipmentSlot getHandSlot() {
        return hand == 1 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
    }
}
