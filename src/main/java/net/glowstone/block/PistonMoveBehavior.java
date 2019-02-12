package net.glowstone.block;

/**
 * Behavior of a block when moved (pushed/pulled) py a piston.
 *
 * <p>MOVE - move as usual.</p>
 * <p>BREAK - break block and drop necessary items (e.g. flowers).</p>
 * <p>DONT_MOVE - don't move the block (e.g. bedrock).</p>
 * <p>MOVE_STICKY - move adjacent blocks too (e.g. slime block)</p>
 */
public enum PistonMoveBehavior {
    MOVE, BREAK, DONT_MOVE, MOVE_STICKY
}
