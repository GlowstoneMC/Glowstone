package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.glowstone.constants.GlowBiomeClimate;
import net.glowstone.entity.GlowProjectile;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GlowFishingHook extends GlowProjectile implements FishHook {
    private int lived;
    private int lifeTime;
    private ItemStack itemStack;

    private static final Multimap<RewardCategory, RewardItem> REWARDS = HashMultimap.create();

    static {
        REWARDS.put(RewardCategory.FISH, new RewardItem(new ItemStack(Material.RAW_FISH), 60.0));
        REWARDS.put(RewardCategory.FISH, new RewardItem(new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 1), 25));
        REWARDS.put(RewardCategory.FISH, new RewardItem(new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 2), 2));
        REWARDS.put(RewardCategory.FISH, new RewardItem(new ItemStack(Material.RAW_FISH, 1, (short) 0, (byte) 3), 13));

        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.BOW), 16.67));
        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.ENCHANTED_BOOK), 16.67));
        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.FISHING_ROD), 16.67));
        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.NAME_TAG), 16.67));
        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.SADDLE), 16.67));
        REWARDS.put(RewardCategory.TREASURE, new RewardItem(new ItemStack(Material.WATER_LILY), 16.67));

        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.BOWL), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.FISHING_ROD), 2.4));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.LEATHER), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.LEATHER_BOOTS), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.ROTTEN_FLESH), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.STICK), 6));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.STRING), 6));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.POTION, 1, (short) 0, (byte) 1), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.BONE), 12));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.INK_SACK, 10), 1.2));
        REWARDS.put(RewardCategory.TRASH, new RewardItem(new ItemStack(Material.TRIPWIRE_HOOK), 12));
    }

    public GlowFishingHook(Location location) {
        this(location, null);
    }

    public GlowFishingHook(Location location, ItemStack itemStack) {
        super(location);
        setSize(0.25f, 0.25f);

        // "There will be a period where the player must wait, randomly chosen from 5 to 45 seconds."
        lifeTime = ThreadLocalRandom.current().nextInt(5, 46);
        if (!InventoryUtil.isEmpty(itemStack) && itemStack.getType() == Material.FISHING_ROD) {
            // "Each level of Lure enchantment on the fishing rod will subtract 5 seconds from this wait time."
            int level = itemStack.getEnchantmentLevel(Enchantment.LURE);
            lifeTime -= level * 5;
            lifeTime = Math.max(lifeTime, 0);

            this.itemStack = itemStack.clone();
        }
        lifeTime *= 20;

        // TODO: velocity does not match vanilla
        Vector direction = location.getDirection();
        setVelocity(direction.multiply(1.5));
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> spawnMessage = super.createSpawnMessage();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        int intPitch = Position.getIntPitch(location);
        int intHeadYaw = Position.getIntHeadYaw(location.getYaw());

        spawnMessage.add(new SpawnObjectMessage(this.getEntityId(), this.getUniqueId(), SpawnObjectMessage.FISHING_HOOK, x, y, z, intPitch, intHeadYaw, 0, velocity));
        return spawnMessage;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public double getBiteChance() {
        // Not supported in newer mc versions anymore
        return 0;
    }

    @Override
    public void setBiteChance(double v) throws IllegalArgumentException {
        // Not supported in newer mc versions anymore
    }

    private Entity getHookedEntity() {
        int entityID = metadata.getInt(MetadataIndex.FISHING_HOOK_HOOKED_ENTITY);
        return world.getEntityManager().getEntity(entityID - 1);
    }

    private void setHookedEntity(Entity entity) {
        metadata.set(MetadataIndex.FISHING_HOOK_HOOKED_ENTITY, entity.getEntityId() + 1);
    }

    @Override
    public void pulse() {
        super.pulse();

        // TODO: Particles
        // TODO: Boper movement
        if (location.getBlock().getType() == Material.WATER) {
            increaseTimeLived();

        }
    }

    private void increaseTimeLived() {
        // "If the bobber is not directly exposed to sun or moonlight,[note 1] the wait time will be approximately doubled.[note 2]"
        Block highestBlockAt = world.getHighestBlockAt(location);
        if (location.getY() < highestBlockAt.getLocation().getY()) {
            if (ThreadLocalRandom.current().nextDouble(100) < 50) {
                return;
            }
        }

        if (GlowBiomeClimate.isRainy(location.getBlock())) {
            if (ThreadLocalRandom.current().nextDouble(100) < 20) {
                lived++;
            }
        }

        lived++;
    }

    public void reelIn() {
        if (location.getBlock().getType() == Material.WATER) {
            if (getShooter() instanceof Player) {
                // TODO: Item should "fly" towards player
                world.dropItemNaturally(((Player) getShooter()).getLocation(), getRewardItem());
                ((Player) getShooter()).giveExp(ThreadLocalRandom.current().nextInt(1, 7));
            }
        }
        remove();
    }

    private ItemStack getRewardItem() {
        RewardCategory rewardCategory = getRewardCategory();
        int level = getEnchantmentLevel();

        if (rewardCategory == null || REWARDS.get(rewardCategory).isEmpty()) {
            return InventoryUtil.createEmptyStack();
        }
        double rewardCategoryChance = rewardCategory.chance + rewardCategory.modifier * level;
        double random = ThreadLocalRandom.current().nextDouble(100);

        for (RewardItem rewardItem : REWARDS.get(rewardCategory)) {
            random -= rewardItem.chance * rewardCategoryChance / 100.0;
            if (random < 0) {
                // TODO: enchantments and damage on book, bow, and fishingrode
                return rewardItem.item.clone();
            }
        }

        return InventoryUtil.createEmptyStack();
    }

    private int getEnchantmentLevel() {
        return !InventoryUtil.isEmpty(itemStack) && itemStack.getType() == Material.FISHING_ROD ? itemStack.getEnchantmentLevel(Enchantment.LUCK) : 0;
    }

    private RewardCategory getRewardCategory() {
        int level = getEnchantmentLevel();
        double random = ThreadLocalRandom.current().nextDouble(100);

        for (RewardCategory rewardCategory : RewardCategory.values()) {
            random -= rewardCategory.chance + rewardCategory.modifier * level;
            if (random <= 0) {
                return rewardCategory;
            }
        }

        return null;
    }

    @Getter
    private enum RewardCategory {
        FISH(85.0, -1.15),
        TREASURE(5.0, 2.1),
        TRASH(10.0, -1.95);

        private double chance;

        /**
         * Each level of the "Luck of the Sea" enchantment will modify this categories chance to appear by modifier amount
         */
        private double modifier;

        RewardCategory(double chance, double modifier) {
            this.chance = chance;
            this.modifier = modifier;
        }
    }

    @Getter
    @AllArgsConstructor
    private static class RewardItem {
        private ItemStack item;
        /**
         * Chance to get this item in his category
         */
        private double chance;
    }
}
