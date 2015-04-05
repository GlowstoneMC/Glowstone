package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a monster such as a creeper.
 * @author Graham Edgecombe
 */
public final class GlowCreature extends GlowLivingEntity implements Creature {

    /**
     * The type of monster.
     */
    private final EntityType type;
   
    /**
     * The monster's target.
     */
    private LivingEntity target;

    /**
     * Creates a new monster.
     * @param location The location of the monster.
     * @param type The type of monster.
     */
    public GlowCreature(Location location, EntityType type) {
        super(location);
        this.type = type;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn mob
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnMobMessage(id, type.getTypeId(), x, y, z, yaw, pitch, pitch, 0, 0, 0, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(id, yaw));

        // todo: equipment
        //result.add(createEquipmentMessage());
        return result;
    }

    @Override
    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    @Override
    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> materials, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Block getTargetBlock(Set<Material> materials, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> materials, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
