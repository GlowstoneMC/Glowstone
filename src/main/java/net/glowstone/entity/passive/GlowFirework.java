package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.Summonable;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GlowFirework extends GlowEntity implements Firework, Summonable {

    private static final ItemStack DEFAULT_FIREWORK_ITEM = new ItemStack(Material.FIREWORK);
    @Getter
    @Setter
    private UUID spawningEntity;
    @Getter
    private LivingEntity boostedEntity;
    /**
     * The number of ticks before this fireworks rocket explodes.
     */
    @Getter
    @Setter
    private int lifeTime;

    public GlowFirework(Location location) {
        super(location);
        setSize(0.25f, 0.25f);
    }

    /**
     * Creates an instance.
     *
     * @param location the location
     * @param spawningEntity TODO: document this parameter
     * @param boostedEntity TODO: document this parameter
     * @param item the firework rocket as an item
     */
    public GlowFirework(Location location, UUID spawningEntity, LivingEntity boostedEntity,
        ItemStack item) {
        super(location);
        this.spawningEntity = spawningEntity;
        setBoostedEntity(boostedEntity);
        setSize(0.25f, 0.25f);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        setVelocity(new Vector(random.nextGaussian() * 0.001, 0.05, random.nextGaussian() * 0.001));

        setFireworkItem(item);
        int power = getFireworkMeta().getPower();
        lifeTime = calculateLifeTime(power);
    }

    @Override
    public EntityType getType() {
        return EntityType.FIREWORK;
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return Arrays.asList(
            new SpawnObjectMessage(
                    entityId, UUID.randomUUID(), SpawnObjectMessage.FIREWORK, x, y, z, 0, 0),
            new EntityMetadataMessage(entityId, metadata.getEntryList())
        );
    }

    @Override
    public FireworkMeta getFireworkMeta() {
        return ((FireworkMeta) getFireworkItem().getItemMeta()).clone();
    }

    @Override
    public void setFireworkMeta(FireworkMeta fireworkMeta) {
        if (fireworkMeta == null) {
            return;
        }

        ItemStack item = getFireworkItem();
        item.setItemMeta(fireworkMeta.clone());
        setFireworkItem(item);
        this.lifeTime = calculateLifeTime(fireworkMeta.getPower());
    }

    /**
     * Get the underlying firework item.
     *
     * @return The Firework ItemStack of this Firework entity, or a new Firework ItemStack
     */
    public ItemStack getFireworkItem() {
        ItemStack item = this.metadata.getItem(MetadataIndex.FIREWORK_INFO);
        if (InventoryUtil.isEmpty(item) || !Material.FIREWORK.equals(item.getType())) {
            item = DEFAULT_FIREWORK_ITEM.clone();
        }
        return item;
    }

    /**
     * Set the firework item of this firework entity. If an empty ItemStack, or none of the type
     * {{@link Material#FIREWORK}} was given, a new Firework ItemStack will be created.
     *
     * @param item FireWork Item this entity should use
     */
    public void setFireworkItem(ItemStack item) {
        if (InventoryUtil.isEmpty(item) || !Material.FIREWORK.equals(item.getType())) {
            item = DEFAULT_FIREWORK_ITEM.clone();
        }
        this.metadata.set(MetadataIndex.FIREWORK_INFO, item.clone());
    }

    private int calculateLifeTime(int power) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return ((power + 1) * 10 + random.nextInt(6) + random.nextInt(7));
    }

    @Override
    public void detonate() {
        if (this.isDead()) {
            return;
        }
        setTicksLived(lifeTime);
    }

    @Override
    public void pulse() {
        super.pulse();

        if (ticksLived == 1) {
            world.playSound(this.location, Sound.ENTITY_FIREWORK_LAUNCH, SoundCategory.AMBIENT, 3,
                1);
        }

        if (ticksLived > lifeTime) {
            explode();
        }
    }

    private void explode() {
        if (!EventFactory.getInstance().callEvent(new FireworkExplodeEvent(this)).isCancelled()) {
            this.playEffect(EntityEffect.FIREWORK_EXPLODE);

            int effectsSize = getFireworkMeta().getEffectsSize();
            if (effectsSize > 0) {
                if (boostedEntity != null) {
                    boostedEntity.damage((5 + effectsSize * 2), DamageCause.ENTITY_EXPLOSION);
                }

                List<Entity> nearbyEntities = this.getNearbyEntities(2.5, 2.5, 2.5);
                for (Entity nearbyEntity : nearbyEntities) {
                    if (!(nearbyEntity instanceof LivingEntity)) {
                        continue;
                    }
                    if (this.getLocation().distanceSquared(nearbyEntity.getLocation()) > 25) {
                        continue;
                    }

                    // "The explosion of firework rockets deals 2.5 hearts of damage, per firework
                    // star."
                    ((LivingEntity) nearbyEntity)
                        .damage((effectsSize * 5), DamageCause.ENTITY_EXPLOSION);
                }
            }
        }
        remove();
    }

    @Override
    protected void pulsePhysics() {
        // TODO: proper physics
        if (this.boostedEntity == null) {
            // Fireworks velocity is not affected by airdrag or gravity
            // These values are static
            velocity.setX(velocity.getX() * 1.15);
            velocity.setY(velocity.getY() + 0.04);
            velocity.setZ(velocity.getZ() * 1.15);
            setVelocity(velocity);
        } else {
            Vector direction = boostedEntity.getLocation().getDirection();
            Vector velocity = boostedEntity.getVelocity();

            // close enough approximation of vanillas velocity for boosted entity
            double dx = direction.getX() * 0.1 + (direction.getX() * 1.5 - velocity.getX()) * 0.5;
            double dy = direction.getY() * 0.1 + (direction.getY() * 1.5 - velocity.getY()) * 0.5;
            double dz = direction.getZ() * 0.1 + (direction.getZ() * 1.5 - velocity.getZ()) * 0.5;

            velocity.setX(velocity.getX() + dx);
            velocity.setY(velocity.getY() + dy);
            velocity.setZ(velocity.getZ() + dz);

            boostedEntity.setVelocity(velocity);
            this.setVelocity(velocity);

            Location location = boostedEntity.getLocation().add(velocity);
            if (boostedEntity instanceof GlowEntity) {
                ((GlowEntity) boostedEntity).setRawLocation(location, false);
            } else {
                boostedEntity.teleport(location);
            }
        }

        setRawLocation(this.location.add(velocity), false);
    }

    private void setBoostedEntity(LivingEntity boostedEntity) {
        this.boostedEntity = boostedEntity;
        if (boostedEntity != null) {
            metadata.set(MetadataIndex.FIREWORK_ENTITY, boostedEntity.getEntityId());
        }
    }
}
