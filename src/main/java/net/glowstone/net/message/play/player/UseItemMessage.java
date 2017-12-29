package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.inventory.EquipmentSlot;

@Data
public class UseItemMessage implements Message {

    private final int hand;

    public EquipmentSlot getEquipmentSlot() {
        return hand == 1 ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND;
    }
}
