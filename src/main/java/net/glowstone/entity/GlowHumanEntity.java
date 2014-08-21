package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.inventory.GlowCraftingInventory;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowInventoryView;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.util.Position;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a human entity, such as an NPC or a player.
 */
public abstract class GlowHumanEntity extends GlowLivingEntity implements HumanEntity {

    /**
     * The player profile with name and UUID information.
     */
    private final PlayerProfile profile;

    /**
     * The inventory of this human.
     */
    private final GlowPlayerInventory inventory = new GlowPlayerInventory(this);

    /**
     * The ender chest inventory of this human.
     */
    private final GlowInventory enderChest = new GlowInventory(this, InventoryType.ENDER_CHEST);

    /**
     * The item the player has on their cursor.
     */
    private ItemStack itemOnCursor;

    /**
     * Whether this human is sleeping or not.
     */
    protected boolean sleeping = false;

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
     * The player's currently open inventory
     */
    private InventoryView inventoryView;

    /**
     * Creates a human within the specified world and with the specified name.
     * @param location The location.
     * @param profile The human's profile with name and UUID information.
     */
    public GlowHumanEntity(Location location, PlayerProfile profile) {
        super(location);
        this.profile = profile;
        permissions = new PermissibleBase(this);
        gameMode = server.getDefaultGameMode();

        inventoryView = new GlowInventoryView(this);
        addViewer(inventoryView.getTopInventory());
        addViewer(inventoryView.getBottomInventory());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    @Override
    public void setUniqueId(UUID uuid) {
        // silently allow setting the same UUID again
        if (!profile.getUniqueId().equals(uuid)) {
            throw new IllegalStateException("UUID of " + this + " is already " + profile.getUniqueId());
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn player
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnPlayerMessage(id, profile, x, y, z, yaw, pitch, 0, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(id, yaw));

        // todo: equipment
        //result.add(createEquipmentMessage());
        return result;
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

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    public String getName() {
        return profile.getName();
    }

    @Override
    public UUID getUniqueId() {
        return profile.getUniqueId();
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

    public boolean isBlocking() {
        return false;
    }

    public int getExpToLevel() {
        throw new UnsupportedOperationException("Non-player HumanEntity has no level");
    }

    @Override
    public EntityEquipment getEquipment() {
        return inventory;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Permissions

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

    ////////////////////////////////////////////////////////////////////////////
    // Inventory

    public GlowPlayerInventory getInventory() {
        return inventory;
    }

    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    public ItemStack getItemOnCursor() {
        return itemOnCursor;
    }

    public void setItemOnCursor(ItemStack item) {
        itemOnCursor = item;
    }

    public Inventory getEnderChest() {
        return enderChest;
    }

    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        // nb: does not actually send anything
        return prop.getType() == inventoryView.getType();
    }

    public InventoryView getOpenInventory() {
        return inventoryView;
    }

    public InventoryView openInventory(Inventory inventory) {
        InventoryView view = new GlowInventoryView(this, inventory);
        openInventory(view);
        return view;
    }

    public InventoryView openWorkbench(Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force && location.getBlock().getType() != Material.WORKBENCH) {
            return null;
        }
        return openInventory(new GlowCraftingInventory(this, InventoryType.WORKBENCH));
    }

    public InventoryView openEnchanting(Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force && location.getBlock().getType() != Material.ENCHANTMENT_TABLE) {
            return null;
        }
        // todo: actually open
        /*InventoryView view = new GlowInventoryView(this, new GlowEnchantInventory() ...);*/
        return null;
    }

    public void openInventory(InventoryView inventory) {
        Validate.notNull(inventory);
        this.inventory.getDragTracker().reset();

        // stop viewing the old inventory and start viewing the new one
        removeViewer(inventoryView.getTopInventory());
        removeViewer(inventoryView.getBottomInventory());
        inventoryView = inventory;
        addViewer(inventoryView.getTopInventory());
        addViewer(inventoryView.getBottomInventory());
    }

    public void closeInventory() {
        // todo: drop item on cursor to ground
        setItemOnCursor(null);
        openInventory(new GlowInventoryView(this));
    }

    private void addViewer(Inventory inventory) {
        if (inventory instanceof GlowInventory) {
            ((GlowInventory) inventory).addViewer(this);
        }
    }

    private void removeViewer(Inventory inventory) {
        if (inventory instanceof GlowInventory) {
            ((GlowInventory) inventory).removeViewer(this);
        }
    }
}
