package net.glowstone.net.message.player;

import net.glowstone.net.message.Message;
import org.bukkit.Location;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Base class for player update messages.
 */
public class PlayerUpdateMessage extends Message {

    private boolean onGround;

    protected PlayerUpdateMessage() {
    }

    public PlayerUpdateMessage(ChannelBuffer buf) {
        decode(buf);
    }

    protected void decode(ChannelBuffer buf) {
        onGround = buf.readByte() != 0;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeByte(onGround ? 1 : 0);
    }

    public final boolean getOnGround() {
        return onGround;
    }

    public void update(Location location) {
        // do nothing
    }

}
