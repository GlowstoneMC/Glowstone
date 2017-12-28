package net.glowstone.entity.objects;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import com.flowpowered.network.Message;

import net.glowstone.entity.GlowEntity;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;

public class GlowEnderPearl extends GlowEntity implements EnderPearl{
    private ProjectileSource shooter;
    private float speed;
    
    public GlowEnderPearl(Location location, float speed) {
        super(location);
        setDrag(0.99, false);
        setDrag(0.99, true);
        setGravityAccel(new Vector(0,-0.06,0));
        this.speed = speed;
    }

    @Override
    public boolean doesBounce() {
        return false;
    }

    @Override
    public ProjectileSource getShooter() {
        return this.shooter;
    }

    @Override
    public void setBounce(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShooter(ProjectileSource source) {
        this.shooter = source;
    }

    @Override
    protected void pulsePhysics() {
        Location velLoc = location.clone().add(velocity);
        velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));

        velocity.setX(velocity.getX() * 0.95);
        velocity.setZ(velocity.getZ() * 0.95);

        setRawLocation(velLoc);

        //If the EnderPearl collides with anything except air/fluids
        if(!location.getBlock().isLiquid() && !location.getBlock().isEmpty()){
            ((Entity) shooter).teleport(location);
            this.remove();
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x, y, z;
        int yaw,pitch;

        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw  = Position.getIntYaw(location);
        pitch = Position.getIntPitch(location);
        
        Vector vel = location.getDirection().multiply(this.speed);
        setVelocity(vel);

        return Arrays.asList(
                new SpawnObjectMessage(id, getUniqueId(), SpawnObjectMessage.THROWN_ENDERPEARL, x, y, z, yaw, pitch),
                new EntityMetadataMessage(id, metadata.getEntryList()),
                // these keep the client from assigning a random velocity
                new EntityTeleportMessage(id, x, y, z, yaw, pitch),
                new EntityVelocityMessage(id, getVelocity())
            );
    }

}
