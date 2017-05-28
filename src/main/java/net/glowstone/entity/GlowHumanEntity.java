package net.glowstone.entity;

import com.flowpowered.network.Message;
import net.glowstone.EventFactory;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.inventory.*;
import net.glowstone.net.message.play.entity.EntityEquipmentMessage;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.Position;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
     * Whether this human is sleeping or not.
     */
    protected boolean sleeping;
    /**
     * This human's PermissibleBase for permissions.
     */
    protected PermissibleBase permissions;
    /**
     * The item the player has on their cursor.
     */
    private ItemStack itemOnCursor;
    /**
     * How long this human has been sleeping.
     */
    private int sleepingTicks;
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
     * The player's xpSeed. Used for calculation of enchantments.
     */
    private int xpSeed;

    /**
     * Whether the client needs to be notified of armor changes (set to true after joining).
     */
    private boolean needsArmorUpdate = false;

    /**
     * Creates a human within the specified world and with the specified name.
     *
     * @param location The location.
     * @param profile  The human's profile with name and UUID information.
     */
    public GlowHumanEntity(Location location, PlayerProfile profile) {
        super(location);
        this.profile = profile;
        xpSeed = new Random().nextInt(); //TODO: use entity's random instance
        permissions = new PermissibleBase(this);
        gameMode = server.getDefaultGameMode();

        inventoryView = new GlowInventoryView(this);
        addViewer(inventoryView.getTopInventory());
        addViewer(inventoryView.getBottomInventory());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = new LinkedList<>();

        // spawn player
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);
        result.add(new SpawnPlayerMessage(id, profile.getUniqueId(), x, y, z, yaw, pitch, metadata.getEntryList()));

        // head facing
        result.add(new EntityHeadRotationMessage(id, yaw));

        // equipment
        EntityEquipment equipment = getEquipment();
        result.add(new EntityEquipmentMessage(id, 0, equipment.getItemInHand()));
        for (int i = 0; i < 4; i++) {
            result.add(new EntityEquipmentMessage(id, i + 1, equipment.getArmorContents()[i]));
        }
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
        processArmorChanges();
    }

    /**
     * Process changes to the human enitity's armor, and update the entity's armor attributes accordingly.
     */
    private void processArmorChanges() {
        GlowPlayer player = null;
        if (this instanceof GlowPlayer) {
            player = ((GlowPlayer) this);
        }
        boolean armorUpdate = false;
        List<EquipmentMonitor.Entry> armorChanges = getEquipmentMonitor().getArmorChanges();
        if (armorChanges.size() > 0) {
            for (EquipmentMonitor.Entry entry : armorChanges) {
                if (player != null && needsArmorUpdate) {
                    player.getSession().send(new EntityEquipmentMessage(0, entry.slot, entry.item));
                }
                armorUpdate = true;
            }
        }
        if (armorUpdate) {
            getAttributeManager().setProperty(AttributeManager.Key.KEY_ARMOR, ArmorConstants.getDefense(getEquipment().getArmorContents()));
            getAttributeManager().setProperty(AttributeManager.Key.KEY_ARMOR_TOUGHNESS, ArmorConstants.getToughness(getEquipment().getArmorContents()));
        }
        needsArmorUpdate = true;
    }

    /**
     * Get this human entity's PlayerProfile with associated data.
     *
     * @return The PlayerProfile.
     */
    public final PlayerProfile getProfile() {
        return profile;
    }

    public int getXpSeed() {
        return xpSeed;
    }

    public void setXpSeed(int xpSeed) {
        this.xpSeed = xpSeed;
    }

    @Override
    public String getName() {
        return profile.getName();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    @Override
    public UUID getUniqueId() {
        return profile.getUniqueId();
    }

    @Override
    public void setUniqueId(UUID uuid) {
        // silently allow setting the same UUID again
        if (!profile.getUniqueId().equals(uuid)) {
            throw new IllegalStateException("UUID of " + this + " is already " + profile.getUniqueId());
        }
    }

    @Override
    public boolean isSleeping() {
        return sleeping;
    }

    @Override
    public int getSleepTicks() {
        return sleepingTicks;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public void setGameMode(GameMode mode) {
        gameMode = mode;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public int getExpToLevel() {
        throw new UnsupportedOperationException("Non-player HumanEntity has no level");
    }

    @Override
    public EntityEquipment getEquipment() {
        return inventory;
    }

    @Override
    public void setFireTicks(int ticks) {
        if (gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) {
            super.setFireTicks(ticks);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Permissions

    @Override
    public boolean isPermissionSet(String name) {
        return permissions.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return permissions.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return permissions.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return permissions.hasPermission(perm);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return permissions.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return permissions.addAttachment(plugin, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return permissions.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return permissions.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        permissions.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        permissions.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return permissions.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return isOp;
    }

    @Override
    public void setOp(boolean value) {
        isOp = value;
        recalculatePermissions();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Health

    @Override
    public boolean canTakeDamage(DamageCause damageCause) {
        return (damageCause == DamageCause.VOID || gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE) && super.canTakeDamage(damageCause);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory

    @Override
    public GlowPlayerInventory getInventory() {
        return inventory;
    }

    @Override
    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    @Override
    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    @Override
    public ItemStack getItemOnCursor() {
        return itemOnCursor;
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        itemOnCursor = item;
    }

    @Override
    public Inventory getEnderChest() {
        return enderChest;
    }

    @Override
    public boolean setWindowProperty(Property prop, int value) {
        // nb: does not actually send anything
        return prop.getType() == inventoryView.getType();
    }

    @Override
    public InventoryView getOpenInventory() {
        return inventoryView;
    }

    @Override
    public InventoryView openInventory(Inventory inventory) {
        InventoryView view = new GlowInventoryView(this, inventory);
        openInventory(view);
        return view;
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force && location.getBlock().getType() != Material.WORKBENCH) {
            return null;
        }
        return openInventory(new GlowCraftingInventory(this, InventoryType.WORKBENCH));
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean force) {
        if (location == null) {
            location = getLocation();
        }
        if (!force && location.getBlock().getType() != Material.ENCHANTMENT_TABLE) {
            return null;
        }
        return openInventory(new GlowEnchantingInventory(location, (GlowPlayer) this));
    }

    @Override
    public void openInventory(InventoryView inventory) {
        checkNotNull(inventory);
        this.inventory.getDragTracker().reset();

        // stop viewing the old inventory and start viewing the new one
        removeViewer(inventoryView.getTopInventory());
        removeViewer(inventoryView.getBottomInventory());
        inventoryView = inventory;
        addViewer(inventoryView.getTopInventory());
        addViewer(inventoryView.getBottomInventory());
    }

    @Override
    public void closeInventory() {
        EventFactory.callEvent(new InventoryCloseEvent(inventoryView));
        if (getGameMode() != GameMode.CREATIVE) {
            if (!InventoryUtil.isEmpty(getItemOnCursor())) {
                drop(getItemOnCursor());
            }
            handleUnusedInputs();
        }
        setItemOnCursor(InventoryUtil.createEmptyStack());
        resetInventoryView();
    }

    // Drop items left in crafting area.
    private void handleUnusedInputs() {
        for (int i = 0; i < getTopInventory().getSlots().size(); i++) {
            ItemStack itemStack = getOpenInventory().getItem(i);
            if (InventoryUtil.isEmpty(itemStack)) {
                continue;
            }

            if (isDroppableCraftingSlot(i)) {
                getOpenInventory().getBottomInventory().addItem(itemStack);
                getOpenInventory().getTopInventory().setItem(i, InventoryUtil.createEmptyStack());
            }
        }
    }

    private boolean isDroppableCraftingSlot(int i) {
        if (getTopInventory().getSlot(i).getType() == SlotType.CRAFTING) {
            switch (getTopInventory().getType()) {
                case BREWING:
                case FURNACE:
                    return false;
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    private GlowInventory getTopInventory() {
        return (GlowInventory) getOpenInventory().getTopInventory();
    }

    private void resetInventoryView() {
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

    /**
     * Drops the item this entity currently has in its hands and remove the
     * item from the HumanEntity's inventory.
     *
     * @param wholeStack True if the whole stack should be dropped
     */
    public void dropItemInHand(boolean wholeStack) {
        ItemStack stack = getItemInHand();
        if (InventoryUtil.isEmpty(stack)) {
            return;
        }

        ItemStack dropping = stack.clone();
        if (!wholeStack) {
            dropping.setAmount(1);
        }

        GlowItem dropped = drop(dropping);
        if (dropped == null) {
            return;
        }

        if (stack.getAmount() == 1 || wholeStack) {
            setItemInHand(InventoryUtil.createEmptyStack());
        } else {
            ItemStack now = stack.clone();
            now.setAmount(now.getAmount() - 1);
            setItemInHand(now);
        }
    }

    /**
     * Spawns a new {@link GlowItem} in the world, as if this HumanEntity had
     * dropped it. Note that this does NOT remove the item from the inventory.
     *
     * @param stack The item to drop
     * @return the GlowItem that was generated, or null if the spawning was cancelled
     * @throws IllegalArgumentException if the stack is empty
     */
    public GlowItem drop(ItemStack stack) {
        checkArgument(!InventoryUtil.isEmpty(stack), "stack must not be empty");

        Location dropLocation = location.clone().add(0, getEyeHeight(true) - 0.3, 0);
        GlowItem dropItem = world.dropItem(dropLocation, stack);
        Vector vel = location.getDirection().multiply(new Vector(0.11F, 0.0F, 0.11F));
        vel.setY(0.006F);
        dropItem.setVelocity(vel);
        return dropItem;
    }
}
