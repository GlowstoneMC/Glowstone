package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class EnchantItemMessage implements Message {

    private final int window, enchantment;

}
