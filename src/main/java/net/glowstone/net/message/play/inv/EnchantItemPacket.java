package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class EnchantItemPacket implements Message {

    private final int window, enchantment;

}
