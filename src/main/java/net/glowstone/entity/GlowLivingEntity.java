package net.glowstone.entity;

import com.flowpowered.networking.Message;
import gnu.trove.set.hash.TIntHashSet;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityRotationMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationMessage;
import net.glowstone.util.Position;
import net.glowstone.util.TargetBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * A GlowLivingEntity is a {@link org.bukkit.entity.Player} or {@link org.bukkit.entity.Monster}.
 * @author Graham Edgecombe.
 */
public abstract class GlowLivingEntity extends GlowEntity implements LivingEntity {

    /**
     * The monster's metadata.
     */
    protected final MetadataMap metadata = new MetadataMap(getClass());

    /**
     * Potion effects on the entity.
     */
    private final Map<PotionEffectType, PotionEffect> potionEffects = new HashMap<PotionEffectType, PotionEffect>();

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
     * @param world The world.
     */
    public GlowLivingEntity(GlowServer server, GlowWorld world) {
        super(server, world);
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
                damage(1);  // be fancier in the future
            }
        } else {
            airTicks = maximumAir;
        }

        // todo: tick down potion effects
    }

    @Override
    public Message createUpdateMessage() {
        boolean moved = hasMoved();
        boolean rotated = hasRotated();

        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int dx = x - Position.getIntX(previousLocation);
        int dy = y - Position.getIntY(previousLocation);
        int dz = z - Position.getIntZ(previousLocation);

        boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

        int yaw = Position.getIntYaw(previousLocation);
        int pitch = Position.getIntPitch(previousLocation);

        if (moved && teleport) {
            return new EntityTeleportMessage(id, x, y, z, yaw, pitch);
        } else if (moved && rotated) {
            return new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch);
        } else if (moved) {
            return new RelativeEntityPositionMessage(id, dx, dy, dz);
        } else if (rotated) {
            return new EntityRotationMessage(id, yaw, pitch);
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

    public double getEyeHeight() {
       return getEyeHeight(false);
    }

    public double getEyeHeight(boolean ignoreSneaking) {
        if (false /* TODO: sneaking */ || !ignoreSneaking) {
            return 1.6;
        } else {
            return 1.4;
        }
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

    public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks.add(0);
        }
        List<Block> ret = new ArrayList<Block>();
        TargetBlock target = new TargetBlock(this, maxDistance, 0.2, transparentBlocks);
        while (target.getNextBlock()) {
            Block block = target.getCurrentBlock().getBlock();
            if (!transparentBlocks.contains(block.getTypeId())) {
                ret.add(block);
            }
        }
        return ret;
    }

    public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks = null;
        }
        Location loc = new TargetBlock(this, maxDistance, 0.2, transparentBlocks).getSolidTargetBlock();
        return loc == null ? null : loc.getBlock();
    }

    public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
        TIntHashSet transparentBlocks = new TIntHashSet();
        if (transparent != null) {
            for (byte byt : transparent) {
                transparentBlocks.add(byt);
            }
        } else {
            transparentBlocks = null;
        }
        TargetBlock target = new TargetBlock(this, maxDistance, 0.2, transparentBlocks);
        Location last = target.getSolidTargetBlock();
        if (last == null) {
            return new ArrayList<Block>(Arrays.asList(target.getPreviousBlock().getBlock()));
        }
        return new ArrayList<Block>(Arrays.asList(target.getPreviousBlock().getBlock(), last.getBlock()));
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
