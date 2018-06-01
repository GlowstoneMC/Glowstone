package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.util.Vector;

@Data
@RequiredArgsConstructor
public final class EntityVelocityMessage implements Message {

    private final int id;
    private final int velocityX;
    private final int velocityY;
    private final int velocityZ;

    public EntityVelocityMessage(int id, Vector velocity) {
        this(id, convert(velocity.getX()), convert(velocity.getY()), convert(velocity.getZ()));
    }

    private static int convert(double val) {
        return (int) (val * 8000);
    }

}
