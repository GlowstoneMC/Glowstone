package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.EventFactory;
import net.glowstone.Explosion;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GlowTNTPrimed extends GlowExplosive implements TNTPrimed {

    private int fuseTicks;
    private Entity source;

    public GlowTNTPrimed(Location location, Entity source) {
        super(location, Explosion.POWER_TNT);
        this.fuseTicks = 80;
        this.source = source;
    }

    public void setIgnitedByExplosion(boolean ignitedByExplosion) {
        if (ignitedByExplosion) {
            // if ignited by an explosion, the fuseTicks should be a random number between 10 and 30 ticks
            fuseTicks = new Random().nextInt(21) + 10;
        }
    }

    @Override
    public void pulse() {
        super.pulse();

        fuseTicks--;
        if (fuseTicks <= 0) {
            explode();
        } else {
            world.showParticle(location.clone().add(0, 0.5D, 0), Particle.SMOKE, 0, 0, 0, 0, 0);
        }
    }

    private void explode() {
        ExplosionPrimeEvent event = EventFactory.callEvent(new ExplosionPrimeEvent(this));

        if (!event.isCancelled()) {
            Location location = getLocation();
            double x = location.getX() + 0.49, y = location.getY() + 0.49, z = location.getZ() + 0.49;
            world.createExplosion(this, x, y, z, event.getRadius(), event.getFire(), true);
        }

        remove();
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location),
                y = Position.getIntY(location),
                z = Position.getIntZ(location),
                pitch = Position.getIntPitch(location),
                yaw = Position.getIntYaw(location);

        LinkedList<Message> result = new LinkedList<>();
        result.add(new SpawnObjectMessage(id, 50, x, y, z, pitch, yaw));
        return result;
    }

    @Override
    public final void setFuseTicks(int i) {
        this.fuseTicks = i;
    }

    @Override
    public final int getFuseTicks() {
        return fuseTicks;
    }

    @Override
    public final Entity getSource() {
        return source.isValid() ? source : null;
    }

    @Override
    public final EntityType getType() {
        return EntityType.PRIMED_TNT;
    }

    @Override
    public void setCustomName(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCustomName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(String[] strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPermissionSet(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void recalculatePermissions() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isOp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOp(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
