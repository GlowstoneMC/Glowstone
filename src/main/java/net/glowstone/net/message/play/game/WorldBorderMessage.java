package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Documented at http://wiki.vg/Protocol#World_Border
 */
@Data
@RequiredArgsConstructor
public final class WorldBorderMessage implements Message {

    private final double x;
    private final double z;
    private final double oldRadius;
    private final double newRadius;
    private final long speed;
    private final int portalTeleportBoundary;
    private final int warningTime;
    private final int warningBlocks;

}
