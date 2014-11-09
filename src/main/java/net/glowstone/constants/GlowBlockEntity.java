package net.glowstone.constants;

/**
 * Name mappings for magic values in UpdateBlockEntity packet
 */
public enum GlowBlockEntity {
    MOB_SPAWNER_POTENTIALS(1),
    COMMAND_BLOCK(2),
    BEACON(3),
    SKULL(4),
    FLOWER_POT(5),
    BANNER(6);

    private final int value;

    private GlowBlockEntity(final int value) {
        this.value = value;
    }

    /**
     * Gets the magic number associated with this GlowBlockEntity
     * @return the magic number
     */
    public int getValue() {
        return this.value;
    }
}
