package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.EquipmentSlot;

@Data
@RequiredArgsConstructor
public final class InteractEntityMessage implements Message {

    private final int id;
    private final int action;
    private final float targetX;
    private final float targetY;
    private final float targetZ;
    private final int hand; // 0 = main hand, 1 = off hand

    public InteractEntityMessage(int id, int action) {
        this(id, action, 0, 0, 0, 0);
    }

    public InteractEntityMessage(int id, int action, int hand) {
        this(id, action, 0, 0, 0, hand);
    }

    public EquipmentSlot getHandSlot() {
        return hand == 1 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
    }

    public enum Action {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }
}
