package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.EquipmentSlot;

@Data
public final class BlockPlacementMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final int direction;
    private final int hand;
    private final float cursorX;
    private final float cursorY;
    private final float cursorZ;

    public EquipmentSlot getHandSlot() {
        return hand == 1 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
    }
}
