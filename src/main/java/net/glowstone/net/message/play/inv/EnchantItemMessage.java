package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class EnchantItemMessage implements Message {

    private final int window;
    private final int enchantment;

}
