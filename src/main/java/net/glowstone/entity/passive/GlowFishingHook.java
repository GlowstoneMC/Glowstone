package net.glowstone.entity.passive;

import static org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH;

import com.flowpowered.network.Message;
import com.google.common.base.Objects;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.constants.GlowBiomeClimate;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.FishingRewardManager.RewardCategory;
import net.glowstone.entity.FishingRewardManager.RewardItem;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.projectile.GlowProjectile;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class GlowFishingHook extends GlowProjectile implements FishHook {
    public static final Message[] EMPTY_MESSAGE_ARRAY = new Message[0];
    /**
     * The minimum time, in seconds, to make the player wait for a bite when using an unenchanted
     * fishing pole.
     */
    private static final int MINIMUM_BASE_WAIT = 5;
    /**
     * The maximum time, in seconds, to make the player wait for a bite.
     */
    private static final int MAXIMUM_WAIT = 45;
    /**
     * Waiting time saved per level of lure (down to a minimum of zero).
     */
    private static final int SECONDS_SAVED_PER_LURE_LEVEL = 5;
    /**
     * Waiting time in ticks after a bite, before it is considered missed if the player hasn't
     * clicked.
     */
    private static final int CLICK_TIMEOUT_TICKS = 10;
    private final ItemStack itemStack;
    @Getter
    @Setter
    private int minWaitTime;
    @Getter
    @Setter
    private int maxWaitTime;
    @Setter
    private boolean applyLure;
    private int lived;
    private int lifeTime;
    /**
     * Creates a fishing bob.
     *
     * @param location  the location
     * @param itemStack the fishing rod (used to handle enchantments) or null (equivalent to
     * @param angler    the player who is casting this fish hook (must be set at spawn time)
     */
    public GlowFishingHook(Location location, ItemStack itemStack, Player angler) {
        super(location);
        setSize(0.25f, 0.25f);
        lifeTime = calculateLifeTime();

        this.itemStack = InventoryUtil.itemOrEmpty(itemStack).clone();

        // TODO: velocity does not match vanilla
        Vector direction = location.getDirection();
        setVelocity(direction.multiply(1.5));
        super.setShooter(angler);
    }

    /**
     * Adds a random set of enchantments, which may include treasure enchantments, to an item.
     *
     * @param reward       the item to enchant
     * @param enchantLevel the level of enchantment to use
     */
    private static void enchant(ItemStack reward, int enchantLevel) {
        // TODO
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        ProjectileSource oldShooter = getShooter();
        if (oldShooter == shooter) {
            return;
        }
        // Shooter is immutable client-side (a situation peculiar to fishing hooks), so if it
        // changes then all clients who can see this fishing hook must be told that this hook has
        // despawned and a new one has spawned.
        super.setShooter(shooter);
        World world = location.getWorld();
        if (world instanceof GlowWorld) {
            List<Message> respawnMessages = new LinkedList<>();
            DestroyEntitiesMessage destroyOldCopy = new DestroyEntitiesMessage(
                Collections.singletonList(getObjectId()));
            respawnMessages.add(destroyOldCopy);
            respawnMessages.addAll(createSpawnMessage(getShooterId()));
            ((GlowWorld) world).getRawPlayers()
                .stream()
                .filter(player -> !Objects.equal(player, shooter))
                .filter(player -> player.canSeeEntity(this))
                .forEach(player -> player.getSession().sendAll(
                    respawnMessages.toArray(EMPTY_MESSAGE_ARRAY)));
            if (shooter instanceof GlowPlayer) {
                GlowSession session = ((GlowPlayer) shooter).getSession();
                session.send(destroyOldCopy);
                session.sendAll(
                    createSpawnMessage(getEntityId()).toArray(EMPTY_MESSAGE_ARRAY));
            }
        }
    }

    private int calculateLifeTime() {
        // Waiting time is 5-45 seconds
        int lifeTime = ThreadLocalRandom.current().nextInt(MINIMUM_BASE_WAIT, MAXIMUM_WAIT + 1);

        int level = getEnchantmentLevel(Enchantment.LURE);
        lifeTime -= level * SECONDS_SAVED_PER_LURE_LEVEL;
        lifeTime = Math.max(lifeTime, 0);
        lifeTime *= 20;
        return lifeTime;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return createSpawnMessage(getShooterId());
    }

    /**
     * Creates the spawn messages given the shooter ID on the receiving end (which is different for
     * the shooter than for everyone else).
     *
     * @param shooterId the shooter's ID, according to the receiving client
     * @return the spawn messages
     */
    private List<Message> createSpawnMessage(int shooterId) {
        List<Message> spawnMessage = super.createSpawnMessage();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int intPitch = Position.getIntPitch(location);
        int intHeadYaw = Position.getIntHeadYaw(location.getYaw());

        spawnMessage.set(0, new SpawnObjectMessage(getEntityId(), getUniqueId(),
            EntityNetworkUtil.getObjectId(EntityType.FISHING_HOOK),
            x, y, z, intPitch, intHeadYaw, shooterId, velocity));
        return spawnMessage;
    }

    private int getShooterId() {
        return getShooter() instanceof Entity ? ((Entity) getShooter()).getEntityId()
            : ENTITY_ID_NOBODY;
    }

    @Override
    public void collide(Block block) {
        // TODO
    }

    @Override
    public void collide(LivingEntity entity) {
        // No effect.
    }

    @Override
    protected int getObjectId() {
        return EntityNetworkUtil.getObjectId(EntityType.FISHING_HOOK);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Deprecated
    @Override
    public double getBiteChance() {
        // Not supported in newer mc versions anymore
        return 0;
    }

    @Deprecated
    @Override
    public void setBiteChance(double v) throws IllegalArgumentException {
        // Not supported in newer mc versions anymore
    }

    @Override
    public boolean isInOpenWater() {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Entity getHookedEntity() {
        return world.getEntityManager().getEntity(
            metadata.getInt(MetadataIndex.FISHING_HOOK_HOOKED_ENTITY) - 1);
    }

    public void setHookedEntity(Entity entity) {
        metadata.set(MetadataIndex.FISHING_HOOK_HOOKED_ENTITY,
            entity == null ? 0 : entity.getEntityId() + 1);
    }

    @Override
    public boolean pullHookedEntity() {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public @NotNull HookState getState() {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public void pulse() {
        super.pulse();
        // TODO: Particles
        // TODO: Bopper movement
        if (location.getBlock().getType() == Material.WATER) {
            increaseTimeLived();
        }
    }

    private void increaseTimeLived() {
        // "The window for reeling in when a fish bites is about half a second.
        // If a bite is missed, the line can be left in the water to wait for another bite."
        // TODO: Option to give high-latency players more time? Not much abuse potential!
        if (lived - lifeTime > CLICK_TIMEOUT_TICKS) {
            lifeTime = calculateLifeTime();
            lived = 0;
        }

        // "If the bobber is not directly exposed to sun or moonlight, the wait time will be
        // approximately doubled."
        Block highestBlockAt = world.getHighestBlockAt(location);
        if (location.getY() < highestBlockAt.getLocation().getY()) {
            if (ThreadLocalRandom.current().nextDouble(100) < 50) {
                return;
            }
        }

        if (GlowBiomeClimate.isRainy(location.getBlock()) && lived < lifeTime) {
            if (ThreadLocalRandom.current().nextDouble(100) < 20) {
                lived++;
            }
        }

        lived++;
    }

    /**
     * Removes this fishing hook. Drops loot and xp if a player is fishing.
     */
    public void reelIn() {
        if (location.getBlock().getType() == Material.WATER) {
            ProjectileSource shooter = getShooter();
            if (shooter instanceof Player) {
                PlayerFishEvent fishEvent
                    = new PlayerFishEvent((Player) shooter, this, null, CAUGHT_FISH);
                fishEvent.setExpToDrop(ThreadLocalRandom.current().nextInt(1, 7));
                fishEvent = EventFactory.getInstance().callEvent(fishEvent);
                if (!fishEvent.isCancelled()) {
                    // TODO: Item should "fly" towards player
                    world.dropItemNaturally(((Player) getShooter()).getLocation(), getRewardItem());
                    ((Player) getShooter()).giveExp(fishEvent.getExpToDrop());
                }
            }
        }
        remove();
    }

    private ItemStack getRewardItem() {
        RewardCategory rewardCategory = getRewardCategory();
        int level = getEnchantmentLevel(Enchantment.LUCK);

        if (rewardCategory == null || world.getServer().getFishingRewardManager()
            .getCategoryItems(rewardCategory).isEmpty()) {
            return InventoryUtil.createEmptyStack();
        }
        double rewardCategoryChance = rewardCategory.getChance()
            + rewardCategory.getModifier() * level;
        double random;
        // This loop is needed because rounding errors make the probabilities add up to less than
        // 100%. It will rarely iterate more than once.
        do {
            random = ThreadLocalRandom.current().nextDouble(100);

            for (RewardItem rewardItem
                : world.getServer().getFishingRewardManager()
                .getCategoryItems(rewardCategory)) {
                random -= rewardItem.getChance() * rewardCategoryChance / 100.0;
                if (random < 0) {
                    ItemStack reward = rewardItem.getItem().clone();
                    int enchantLevel = rewardItem.getMinEnchantmentLevel();
                    int maxEnchantLevel = rewardItem.getMaxEnchantmentLevel();
                    if (maxEnchantLevel > enchantLevel) {
                        enchantLevel = ThreadLocalRandom.current().nextInt(
                            enchantLevel, maxEnchantLevel + 1);
                    }
                    if (enchantLevel > 0) {
                        enchant(reward, enchantLevel);
                    }
                    return reward;
                }
            }
        } while (random >= 0);

        return InventoryUtil.createEmptyStack();
    }

    private int getEnchantmentLevel(Enchantment enchantment) {
        return !InventoryUtil.isEmpty(itemStack) && itemStack.getType() == Material.FISHING_ROD
            ? itemStack.getEnchantmentLevel(enchantment)
            : 0;
    }

    private RewardCategory getRewardCategory() {
        int level = getEnchantmentLevel(Enchantment.LUCK);
        double random = ThreadLocalRandom.current().nextDouble(100);

        for (RewardCategory rewardCategory : RewardCategory.values()) {
            random -= rewardCategory.getChance() + rewardCategory.getModifier() * level;
            if (random <= 0) {
                return rewardCategory;
            }
        }

        return null;
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public void setRotation(float yaw, float pitch) {

    }

    @Override
    public CreatureSpawnEvent.@NotNull SpawnReason getEntitySpawnReason() {
        return null;
    }

    @Override
    public boolean getApplyLure() {
        return this.applyLure;
    }
}
