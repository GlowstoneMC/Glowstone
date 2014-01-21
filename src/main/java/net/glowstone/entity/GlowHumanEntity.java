package net.glowstone.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnPlayerMessage;
import net.glowstone.util.Position;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * Represents a human entity, such as an NPC or a player.
 */
public abstract class GlowHumanEntity extends GlowLivingEntity implements HumanEntity {

    /**
     * The name of this human.
     */
    private final String name;
    
    /**
     * The inventory of this human.
     */
    private final GlowPlayerInventory inventory = new GlowPlayerInventory();
    
    /**
     * Whether this human is sleeping or not.
     */
    protected boolean sleeping = false;

    /**
     * The bed spawn location of a player
     */
    private Location bedSpawn;
    
    /**
     * How long this human has been sleeping.
     */
    private int sleepingTicks = 0;
    
    /**
     * This human's PermissibleBase for permissions.
     */
    protected PermissibleBase permissions;
    
    /**
     * Whether this human is considered an op.
     */
    private boolean isOp;

    /**
     * The player's active game mode
     */
    private GameMode gameMode;
    
    /**
     * Creates a human within the specified world and with the specified name.
     * @param world The world.
     * @param name The human's name.
     */
    public GlowHumanEntity(GlowServer server, GlowWorld world, String name) {
        super(server, world);
        this.name = name;
        permissions = new PermissibleBase(this);
        gameMode = server.getDefaultGameMode();
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        return new SpawnPlayerMessage(id, name, x, y, z, yaw, pitch, 0);
    }

    public String getName() {
        return name;
    }

    public GlowPlayerInventory getInventory() {
        return inventory;
    }

    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public int getSleepTicks() {
        return sleepingTicks;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode mode) {
        gameMode = mode;
    }
    
    protected void setSleepTicks (int ticks) {
        sleepingTicks = ticks;
    }
    
    @Override
    public void pulse() {
        super.pulse();
        if (sleeping) {
            ++sleepingTicks;
        } else {
            sleepingTicks = 0;
        }
    }

    // ---- Permissions stuff
    
    public boolean isPermissionSet(String name) {
        return permissions.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return permissions.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return permissions.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return permissions.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return permissions.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return permissions.addAttachment(plugin, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return permissions.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return permissions.addAttachment(plugin, name, value, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        permissions.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        permissions.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return permissions.getEffectivePermissions();
    }

    public boolean isOp() {
        return isOp;
    }

    public void setOp(boolean value) {
        isOp = value;
        recalculatePermissions();
    }

    public Location getBedSpawnLocation() {
        return bedSpawn;
    }

    public void setBedSpawnLocation(Location bedSpawn) {
        this.bedSpawn = bedSpawn;
    }
    
}
