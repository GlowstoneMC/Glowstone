package net.glowstone.net.message.player;

import org.bukkit.Location;
import org.jboss.netty.buffer.ChannelBuffer;

public class PlayerLookMessage extends PlayerUpdateMessage {

    private final float yaw, pitch;

    public PlayerLookMessage(ChannelBuffer buf) {
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        super.decode(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        super.encode(buf);
    }

    @Override
    public void update(Location location) {
        location.setYaw(yaw);
        location.setPitch(pitch);
    }
}
