package net.glowstone.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

/**
 * Represents a monster such as a creeper.
 * @author Graham Edgecombe
 */
public final class GlowCreature extends GlowLivingEntity implements Creature {

    /**
     * The type of monster.
     */
    private final int type;
   
    /**
     * The monster's target.
     */
    private LivingEntity target;

    /**
     * Creates a new monster.
     * @param world The world this monster is in.
     * @param type The type of monster.
     */
    public GlowCreature(GlowServer server, GlowWorld world, int type) {
        super(server, world);
        this.type = type;
    }

    /**
     * Gets the type of monster.
     * @return The type of monster.
     */
    public int getIntType() {
        return type;
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        return new SpawnMobMessage(id, type, x, y, z, yaw, pitch, metadata);
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public LivingEntity getTarget() {
        return target;
    }

}
