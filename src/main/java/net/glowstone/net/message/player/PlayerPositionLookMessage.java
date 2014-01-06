package net.glowstone.net.message.player;

import org.bukkit.Location;
import org.jboss.netty.buffer.ChannelBuffer;

public final class PlayerPositionLookMessage extends PlayerUpdateMessage {

    private final double x, stance, y, z;
    private final float yaw, pitch;

    public PlayerPositionLookMessage(ChannelBuffer buf) {
        x = buf.readDouble();
        stance = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        super.decode(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(stance);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        super.encode(buf);
    }

    @Override
    public void update(Location location) {
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(yaw);
        location.setPitch(pitch);
    }


}
