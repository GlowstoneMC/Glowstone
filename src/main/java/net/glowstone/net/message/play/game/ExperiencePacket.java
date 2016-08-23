package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class ExperiencePacket implements Message {

    private final float barValue;
    private final int level, totalExp;

}
