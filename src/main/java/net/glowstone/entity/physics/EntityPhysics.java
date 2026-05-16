package net.glowstone.entity.physics;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.util.Vector;

/**
 * Holds physics configuration for an entity type based on vanilla Minecraft behavior.
 * Physics constants are documented at https://minecraft.wiki/w/Entity#Motion
 */
@Getter
@Builder
public class EntityPhysics {

    /**
     * Gravity acceleration per tick (blocks/tick²). Negative values accelerate downward.
     */
    private final double gravity;

    /**
     * Vertical drag coefficient applied each tick (0-1). Lower = more drag.
     * For example, 0.98 means entity loses 2% of vertical velocity per tick.
     */
    private final double verticalDrag;

    /**
     * Horizontal drag coefficient applied each tick (0-1).
     * For example, 0.91 means entity loses 9% of horizontal velocity per tick.
     */
    private final double horizontalDrag;

    /**
     * The ticking order determines when velocity operations are applied.
     * Different entity types use different orders which affects their movement.
     */
    private final TickingOrder tickingOrder;

    /**
     * Water drag coefficient. Default is 0.8 (20% drag per tick in water).
     */
    @Builder.Default
    private final double waterDrag = 0.8;

    /**
     * Lava drag coefficient. Default is 0.5 (50% drag per tick in lava).
     */
    @Builder.Default
    private final double lavaDrag = 0.5;

    /**
     * Whether this entity is pushed by water flow. Default true.
     */
    @Builder.Default
    private final boolean pushedByWaterFlow = true;

    /**
     * Terminal velocity threshold. Velocities below this are set to zero.
     * Vanilla uses 0.003 for most entities.
     */
    @Builder.Default
    private final double velocityThreshold = 0.003;

    /**
     * Defines the order in which position, acceleration, and drag are applied each tick.
     * This is critical for matching vanilla behavior as different orders produce different results.
     */
    public enum TickingOrder {
        /**
         * Living entities: Position first, then acceleration, then drag.
         * Results in higher terminal velocities.
         */
        POSITION_ACCELERATION_DRAG,

        /**
         * Items, falling blocks, TNT: Acceleration first, then position, then drag.
         */
        ACCELERATION_POSITION_DRAG,

        /**
         * Boats, thrown projectiles: Acceleration, drag, then position.
         */
        ACCELERATION_DRAG_POSITION
    }

    /**
     * Physics configuration for living entities (players, mobs).
     * Gravity: -0.08, vertical drag: 0.98, horizontal drag: 0.91
     */
    public static final EntityPhysics LIVING_ENTITY = EntityPhysics.builder()
            .gravity(-0.08)
            .verticalDrag(0.98)
            .horizontalDrag(0.91)
            .tickingOrder(TickingOrder.POSITION_ACCELERATION_DRAG)
            .build();

    /**
     * Physics configuration for dropped items.
     * Gravity: -0.04, drag: 0.98 (both axes)
     */
    public static final EntityPhysics ITEM = EntityPhysics.builder()
            .gravity(-0.04)
            .verticalDrag(0.98)
            .horizontalDrag(0.98)
            .tickingOrder(TickingOrder.ACCELERATION_POSITION_DRAG)
            .build();

    /**
     * Physics configuration for experience orbs.
     * Gravity: -0.03, drag: 0.98 (both axes)
     */
    public static final EntityPhysics EXPERIENCE_ORB = EntityPhysics.builder()
            .gravity(-0.03)
            .verticalDrag(0.98)
            .horizontalDrag(0.98)
            .tickingOrder(TickingOrder.ACCELERATION_POSITION_DRAG)
            .build();

    /**
     * Physics configuration for falling blocks and TNT.
     * Gravity: -0.04, drag: 0.98 (both axes)
     */
    public static final EntityPhysics FALLING_BLOCK = EntityPhysics.builder()
            .gravity(-0.04)
            .verticalDrag(0.98)
            .horizontalDrag(0.98)
            .tickingOrder(TickingOrder.ACCELERATION_POSITION_DRAG)
            .pushedByWaterFlow(true)
            .build();

    /**
     * Physics configuration for minecarts.
     * Gravity: -0.04, drag: 0.95 (both axes)
     */
    public static final EntityPhysics MINECART = EntityPhysics.builder()
            .gravity(-0.04)
            .verticalDrag(0.95)
            .horizontalDrag(0.95)
            .tickingOrder(TickingOrder.ACCELERATION_POSITION_DRAG)
            .build();

    /**
     * Physics configuration for boats.
     * Gravity: -0.04, no vertical drag (boats float)
     */
    public static final EntityPhysics BOAT = EntityPhysics.builder()
            .gravity(-0.04)
            .verticalDrag(1.0)
            .horizontalDrag(0.9)
            .tickingOrder(TickingOrder.ACCELERATION_DRAG_POSITION)
            .pushedByWaterFlow(true)
            .build();

    /**
     * Physics configuration for thrown projectiles (eggs, snowballs, ender pearls).
     * Gravity: -0.03, drag: 0.99 (both axes)
     */
    public static final EntityPhysics THROWN_PROJECTILE = EntityPhysics.builder()
            .gravity(-0.03)
            .verticalDrag(0.99)
            .horizontalDrag(0.99)
            .tickingOrder(TickingOrder.ACCELERATION_DRAG_POSITION)
            .pushedByWaterFlow(false)
            .build();

    /**
     * Physics configuration for arrows and tridents.
     * Gravity: -0.05, drag: 0.99 (both axes)
     */
    public static final EntityPhysics ARROW = EntityPhysics.builder()
            .gravity(-0.05)
            .verticalDrag(0.99)
            .horizontalDrag(0.99)
            .tickingOrder(TickingOrder.ACCELERATION_DRAG_POSITION)
            .pushedByWaterFlow(false)
            .build();

    /**
     * Physics configuration for fireballs (no gravity, accelerates on damage).
     * Gravity: 0 (pushed by hits), drag: 0.95
     */
    public static final EntityPhysics FIREBALL = EntityPhysics.builder()
            .gravity(0.0)
            .verticalDrag(0.95)
            .horizontalDrag(0.95)
            .tickingOrder(TickingOrder.ACCELERATION_DRAG_POSITION)
            .pushedByWaterFlow(false)
            .build();

    /**
     * Physics configuration for fishing bobbers.
     * Gravity: -0.03, drag: 0.92 (both axes)
     */
    public static final EntityPhysics FISHING_BOBBER = EntityPhysics.builder()
            .gravity(-0.03)
            .verticalDrag(0.92)
            .horizontalDrag(0.92)
            .tickingOrder(TickingOrder.ACCELERATION_POSITION_DRAG)
            .pushedByWaterFlow(true)
            .build();

    /**
     * Apply velocity threshold - velocities below threshold are zeroed.
     *
     * @param velocity the velocity vector to check
     */
    public void applyVelocityThreshold(Vector velocity) {
        if (Math.abs(velocity.getX()) < velocityThreshold) {
            velocity.setX(0);
        }
        if (Math.abs(velocity.getY()) < velocityThreshold) {
            velocity.setY(0);
        }
        if (Math.abs(velocity.getZ()) < velocityThreshold) {
            velocity.setZ(0);
        }
    }
}
