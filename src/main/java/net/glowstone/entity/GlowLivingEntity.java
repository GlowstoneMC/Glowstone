package net.glowstone.entity;

import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * A GlowLivingEntity is a {@link org.bukkit.entity.Player} or {@link org.bukkit.entity.Monster}.
 * @author Graham Edgecombe.
 */
public abstract class GlowLivingEntity extends GlowEntity implements LivingEntity {

    /**
     * Potion effects on the entity.
     */
    private final Map<PotionEffectType, PotionEffect> potionEffects = new HashMap<>();

    /**
     * The entity's health.
     */
    protected double health;

    /**
     * The entity's maximum health.
     */
    protected double maxHealth;

    /**
     * The magnitude of the last damage the entity took.
     */
    private double lastDamage;

    /**
     * How long the entity has until it runs out of air.
     */
    private int airTicks = 300;

    /**
     * The maximum amount of air the entity can hold.
     */
    private int maximumAir = 300;

    /**
     * The number of ticks remaining in the invincibility period.
     */
    private int noDamageTicks = 0;

    /**
     * The default length of the invincibility period.
     */
    private int maxNoDamageTicks = 20;

    /**
     * A custom overhead name to be shown for non-Players.
     */
    private String customName;

    /**
     * Whether the custom name is shown.
     */
    private boolean customNameVisible;

    /**
     * Whether the entity should be removed if it is too distant from players.
     */
    private boolean removeDistance;

    /**
     * Whether the (non-Player) entity can pick up armor and tools.
     */
    private boolean pickupItems;

    /**
     * Creates a mob within the specified world.
     * @param location The location.
     */
    public GlowLivingEntity(Location location) {
        super(location);
        resetMaxHealth();
        health = maxHealth;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    @Override
    public void pulse() {
        super.pulse();

        if (noDamageTicks > 0) {
            --noDamageTicks;
        }

        Material mat = getEyeLocation().getBlock().getType();
        if (mat == Material.WATER || mat == Material.STATIONARY_WATER) {
            --airTicks;
            if (airTicks <= -20) {
                airTicks = 0;
                // todo: indicate that the damage was caused by drowning
                damage(1);
            }
        } else {
            airTicks = maximumAir;
        }

        // todo: tick down potion effects
    }

    protected final void updateMetadata() {
        EntityMetadataMessage message = new EntityMetadataMessage(id, metadata.getEntryList());
        for (GlowPlayer player : world.getRawPlayers()) {
            if (player != this && player.canSee(this)) {
                player.getSession().send(message);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    public double getEyeHeight() {
       return 0;
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        return getEyeHeight();
    }

    public Location getEyeLocation() {
        return getLocation().add(0, getEyeHeight(), 0);
    }

    public Player getKiller() {
        return null;
    }

    public boolean hasLineOfSight(Entity other) {
        return false;
    }

    public EntityEquipment getEquipment() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    public int getNoDamageTicks() {
        return noDamageTicks;
    }

    public void setNoDamageTicks(int ticks) {
        noDamageTicks = ticks;
    }

    public int getMaximumNoDamageTicks() {
        return maxNoDamageTicks;
    }

    public void setMaximumNoDamageTicks(int ticks) {
        maxNoDamageTicks = ticks;
    }

    public int getRemainingAir() {
        return airTicks;
    }

    public void setRemainingAir(int ticks) {
        airTicks = Math.min(ticks, maximumAir);
    }

    public int getMaximumAir() {
        return maximumAir;
    }

    public void setMaximumAir(int ticks) {
        maximumAir = Math.max(0, ticks);
    }

    public boolean getRemoveWhenFarAway() {
        return removeDistance;
    }

    public void setRemoveWhenFarAway(boolean remove) {
        removeDistance = remove;
    }

    public boolean getCanPickupItems() {
        return pickupItems;
    }

    public void setCanPickupItems(boolean pickup) {
        pickupItems = pickup;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Line of Sight

    private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
        // same limit as CraftBukkit
        if (maxDistance > 120) {
            maxDistance = 120;
        }

        LinkedList<Block> blocks = new LinkedList<>();
        Iterator<Block> itr = new BlockIterator(this, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength) {
                blocks.removeFirst();
            }
            int id = block.getTypeId();
            if (transparent == null) {
                if (id != 0) {
                    break;
                }
            } else {
                if (!transparent.contains((byte) id)) {
                    break;
                }
            }
        }
        return blocks;
    }

    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 0);
    }

    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 1).get(0);
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 2);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Projectiles

    public Egg throwEgg() {
        return launchProjectile(Egg.class);
    }

    public Snowball throwSnowball() {
        return launchProjectile(Snowball.class);
    }

    public Arrow shootArrow() {
        return launchProjectile(Arrow.class);
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return launchProjectile(projectile, getLocation().getDirection());  // todo: multiply by some speed
    }

    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Health

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        if (health < 0) health = 0;
        if (health > maxHealth) health = maxHealth;
        this.health = health;
    }

    public void damage(double amount) {
        damage(amount, null);
    }

    public void damage(double amount, Entity source) {
        // todo: handle noDamageTicks
        lastDamage = amount;
        health -= amount;
        // todo: death, events, so on
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double health) {
        maxHealth = health;
    }

    public void resetMaxHealth() {
        maxHealth = 20;
    }

    public double getLastDamage() {
        return lastDamage;
    }

    public void setLastDamage(double damage) {
        lastDamage = damage;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Invalid health methods

    public void _INVALID_damage(int amount) {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public int _INVALID_getLastDamage() {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public void _INVALID_setLastDamage(int damage) {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public void _INVALID_setMaxHealth(int health) {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public int _INVALID_getMaxHealth() {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public void _INVALID_damage(int amount, Entity source) {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public int _INVALID_getHealth() {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    public void _INVALID_setHealth(int health) {
        throw new UnsupportedOperationException("Invalid/deprecated method");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Potion effects

    public boolean addPotionEffect(PotionEffect effect) {
        return addPotionEffect(effect, false);
    }

    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        if (potionEffects.containsKey(effect.getType())) {
            if (force) {
                removePotionEffect(effect.getType());
            } else {
                return false;
            }
        }

        potionEffects.put(effect.getType(), effect);

        // todo: this, updated, only players in range
        /*EntityEffectMessage msg = new EntityEffectMessage(getEntityId(), effect.getType().getId(), effect.getAmplifier(), effect.getDuration());
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(msg);
        }*/
        return true;
    }

    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        boolean result = true;
        for (PotionEffect effect : effects) {
            if (!addPotionEffect(effect)) {
                result = false;
            }
        }
        return result;
    }

    public boolean hasPotionEffect(PotionEffectType type) {
        return potionEffects.containsKey(type);
    }

    public void removePotionEffect(PotionEffectType type) {
        if (!hasPotionEffect(type)) return;
        potionEffects.remove(type);

        // todo: this, improved, for players in range
        /*EntityRemoveEffectMessage msg = new EntityRemoveEffectMessage(getEntityId(), type.getId());
        for (Player player : server.getOnlinePlayers()) {
            ((GlowPlayer) player).getSession().send(msg);
        }*/
    }

    public Collection<PotionEffect> getActivePotionEffects() {
        return Collections.unmodifiableCollection(potionEffects.values());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Custom name

    public void setCustomName(String name) {
        customName = name;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomNameVisible(boolean flag) {
        customNameVisible = flag;
    }

    public boolean isCustomNameVisible() {
        return customNameVisible;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Leashes

    public boolean isLeashed() {
        return false;
    }

    public Entity getLeashHolder() throws IllegalStateException {
        return null;
    }

    public boolean setLeashHolder(Entity holder) {
        return false;
    }
}
