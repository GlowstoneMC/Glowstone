package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.util.EulerAngle;

@Data
public final class SpawnPositionMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final float angle;
}
