package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.Collection;

@Data
public class ExplosionMessage implements Message {

    private final float x;
    private final float y;
    private final float z;
    private final float radius;
    private final float playerMotionX;
    private final float playerMotionY;
    private final float playerMotionZ;
    private final Collection<Record> records;

    @Override
    public String toString() {
        return "ExplosionMessage{x=" + x + ",y=" + y + ",z=" + z
            + ",radius=" + radius
            + ",motX=" + playerMotionX + ",motY=" + playerMotionY + ",motZ=" + playerMotionZ
            + ",recordCount=" + records.size() + "}";
    }

    @Data
    public static class Record {

        private final byte x;
        private final byte y;
        private final byte z;
    }
}
