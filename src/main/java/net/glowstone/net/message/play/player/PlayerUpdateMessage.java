package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import org.bukkit.Location;

/**
 * Base class for player update messages.
 */
public class PlayerUpdateMessage implements Message {

    private boolean onGround;

    public PlayerUpdateMessage(boolean onGround) {
        this.onGround = onGround;
    }

    public final boolean getOnGround() {
        return onGround;
    }

    public void update(Location location) {
        // do nothing
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
