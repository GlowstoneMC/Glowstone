package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A GlowLightning strike is an entity produced during thunderstorms.
 */
public class GlowLightningStrike extends GlowWeather implements LightningStrike {

    /**
     * Whether the lightning strike is just for effect.
     */
    private boolean effect;
    
    /**
     * How long this lightning strike has to remain in the world.
     */
    private final int ticksToLive;

    private final Random random;

    public GlowLightningStrike(Location location, boolean effect, Random random) {
        super(location);
        this.effect = effect;
        this.ticksToLive = 30;
        this.random = random;
    }

    @Override
    public EntityType getType() {
        return EntityType.LIGHTNING;
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
    public boolean isEffect() {
        return effect;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getTicksLived() >= ticksToLive) {
            remove();
        }
        if (getTicksLived() == 1) {
            location.getWorld().playSound(location, Sound.AMBIENCE_THUNDER, 10000, 0.8F + random.nextFloat() * 0.2F);
            location.getWorld().playSound(location, Sound.EXPLODE, 2, 0.5F + random.nextFloat() * 0.2F);
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return Arrays.<Message>asList(new SpawnLightningStrikeMessage(id, x, y, z));
    }

    @Override
    public List<Message> createUpdateMessage() {
        return Arrays.asList();
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
