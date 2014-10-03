package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class PlayerAbilitiesMessage implements Message {

    private final int flags;
    private final float flySpeed, walkSpeed;

}

