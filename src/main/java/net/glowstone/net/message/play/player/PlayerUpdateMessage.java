package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.bukkit.Location;

/**
 * Base class for player update messages.
 */
@Data
public class PlayerUpdateMessage implements Message {

    private final boolean onGround;

    public void update(Location location) {
        // do nothing
    }

}
