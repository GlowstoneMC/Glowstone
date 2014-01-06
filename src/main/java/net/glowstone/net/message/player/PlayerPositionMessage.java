package net.glowstone.net.message.player;

import org.bukkit.Location;
import org.jboss.netty.buffer.ChannelBuffer;

public class PlayerPositionMessage extends PlayerUpdateMessage {

    private final double x, stance, y, z;

    public PlayerPositionMessage(ChannelBuffer buf) {
        x = buf.readDouble();
        stance = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        super.decode(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(stance);
        buf.writeDouble(y);
        buf.writeDouble(z);
        super.encode(buf);
    }

    @Override
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
    }
}
