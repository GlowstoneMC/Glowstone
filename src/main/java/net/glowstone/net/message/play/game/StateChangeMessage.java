package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class StateChangeMessage implements Message {

    private final int reason;
    private final float value;

    public StateChangeMessage(Reason reason, float value) {
        this.reason = reason.ordinal();
        this.value = value;
    }

    public static enum Reason {
        INVALID_BED,
        STOP_RAIN,
        START_RAIN,
        GAMEMODE,
        CREDITS,
        DEMO_MESSAGE,
        ARROW,
        FADE_VALUE,
        FADE_TIME
    }

}
