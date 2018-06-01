package net.glowstone.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Name mappings for magic values in UpdateBlockEntity packet.
 */
@RequiredArgsConstructor
public enum GlowBlockEntity {
    MOB_SPAWNER_POTENTIALS(1),
    COMMAND_BLOCK(2),
    BEACON(3),
    SKULL(4),
    FLOWER_POT(5),
    BANNER(6),
    STRUCTURE(7),
    END_GATEWAY(8),
    SIGN(9),
    SHULKER_BOX(10),
    BED(11),;

    /**
     * Gets the magic number associated with this GlowBlockEntity.
     *
     * @return the magic number
     */
    @Getter
    private final int value;
}
