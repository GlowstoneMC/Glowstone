package net.glowstone;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockTNT;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.ExplosionMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;

public final class Explosion {

    public static final int POWER_TNT = 4;
    public static final int POWER_BED = 5;
    public static final int POWER_CREEPER = 3;
    public static final int POWER_CHARGED_CREEPER = 6;
    public static final int POWER_GHAST = 1;
    public static final int POWER_WITHER_SKULL = 1;
    public static final int POWER_WITHER_CREATION = 7;
    public static final int POWER_ENDER_CRYSTAL = 6;

    private float power;
    private final Entity source;
    private final Location location;
    private final boolean incendiary;
    private final boolean breakBlocks;
    private final GlowWorld world;
    private float yield = 0.3f;

    private static final Random random = new Random();

    /**
     * Creates a new explosion
     * @param source The entity causing this explosion
     * @param world The world this explosion is in
     * @param x The X location of the explosion
     * @param y The Y location of the explosion
     * @param z The Z location of the explosion
     * @param power The power of the explosion
     * @param incendiary Whether or not blocks should be set on fire
     * @param breakBlocks Whether blocks should break through this explosion
     */
    public Explosion(Entity source, GlowWorld world, double x, double y, double z, float power, boolean incendiary, boolean breakBlocks) {
        this(source, new Location(world, x, y, z), power, incendiary, breakBlocks);
    }

    /**
     * Creates a new explosion
     * @param source The entity causing this explosion
     * @param location The location this explosion is occuring at. Must contain a GlowWorld
     * @param power The power of the explosion
     * @param incendiary Whether or not blocks should be set on fire
     * @param breakBlocks Whether blocks should break through this explosion
     */
    public Explosion(Entity source, Location location, float power, boolean incendiary, boolean breakBlocks) {
        if (!(location.getWorld() instanceof GlowWorld)) {
            throw new IllegalArgumentException("Supplied location does not have a valid GlowWorld");
        }

        this.source = source;
        this.location = location.clone();
        this.power = power;
        this.incendiary = incendiary;
        this.breakBlocks = breakBlocks;
        this.world = (GlowWorld) location.getWorld();
    }

    public boolean explodeWithEvent() {
        if (power < 0.1f)
            return true;

        Set<BlockVector> droppedBlocks = calculateBlocks();

        EntityExplodeEvent event = EventFactory.callEvent(new EntityExplodeEvent(source, location, toBlockList(droppedBlocks), yield));
        if (event.isCancelled()) return false;

        this.yield = event.getYield();

        playOutSoundAndParticles();

        List<Block> blocks = toBlockList(droppedBlocks);

        for (Block block : blocks) {
            handleBlockExplosion((GlowBlock) block);
        }

        if (incendiary) {
            for (Block block : blocks) {
                setBlockOnFire((GlowBlock) block);
            }
        }

        Collection<GlowPlayer> affectedPlayers = damageEntities();
        for (GlowPlayer player : affectedPlayers) {
            playOutExplosion(player, droppedBlocks);
        }

        return true;
    }

    ///////////////////////////////////////////////////
    // Calculate all the dropping blocks

    private Set<BlockVector> calculateBlocks() {
        if (!breakBlocks)
            return new HashSet<>();

        Set<BlockVector> blocks = new HashSet<>();

        final int value = 16;

        for (int x = 0; x < value; x++) {
            for (int y = 0; y < value; y++) {
                for (int z = 0; z < value; z++) {
                    if (!(x == 0 || x == value - 1 || y == 0 || y == value - 1 || z == 0 || z == value - 1)) {
                        continue;
                    }
                    calculateRay(x, y, z, blocks);
                }
            }
        }

        return blocks;
    }

    private void calculateRay(int ox, int oy, int oz, Collection<BlockVector> result) {
        double x = ox / 7.5 - 1;
        double y = oy / 7.5 - 1;
        double z = oz / 7.5 - 1;
        Vector direction = new Vector(x, y, z);
        direction.normalize();
        direction.multiply(0.3f); // 0.3 blocks away with each step

        Location current = location.clone();

        float currentPower = calculateStartPower();

        while (currentPower > 0) {
            GlowBlock block = world.getBlockAt(current);

            if (block.getType() != Material.AIR) {
                double blastDurability = getBlastDurability(block) / 5d;
                blastDurability += 0.3F;
                blastDurability *= 0.3F;
                currentPower -= blastDurability;

                if (currentPower > 0) {
                    result.add(new BlockVector(block.getX(), block.getY(), block.getZ()));
                }
            }

            current.add(direction);
            currentPower -= 0.225f;
        }
    }

    private void handleBlockExplosion(GlowBlock block) {
        if (block.getType() == Material.AIR) {
            return;
        } else if (block.getType() == Material.TNT) {
            BlockTNT.igniteBlock(block, true);
            return;
        }

        block.breakNaturally(yield);
    }

    private float calculateStartPower() {
        float rand = random.nextFloat();
        rand *= 0.6F; // (max - 0.7)
        rand += 0.7; // min
        return rand * power;
    }

    private double getBlastDurability(GlowBlock block) {
        // TODO: return the block's blast durability
        return 2.5;
    }

    private List<Block> toBlockList(Collection<BlockVector> locs) {
        List<Block> blocks = new ArrayList<>(locs.size());
        for (BlockVector location : locs)
            blocks.add(world.getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        return blocks;
    }

    private void setBlockOnFire(GlowBlock block) {
        if (random.nextInt(3) != 0)
            return;

        Block below = block.getRelative(BlockFace.DOWN);
        // TODO: check for flammable blocks
        Material belowType = below.getType();
        if (belowType == Material.AIR || belowType == Material.FIRE) return;

        BlockIgniteEvent event = EventFactory.callEvent(new BlockIgniteEvent(block, BlockIgniteEvent.IgniteCause.EXPLOSION, source));
        if (event.isCancelled())
            return;

        block.setType(Material.FIRE);
    }

    /////////////////////////////////////////
    // Damage entities

    private Collection<GlowPlayer> damageEntities() {
        float power = this.power;
        this.power *= 2f;

        Collection<GlowPlayer> affectedPlayers = new ArrayList<>();

        Collection<GlowLivingEntity> entities = getNearbyEntities();
        for (GlowLivingEntity entity : entities) {
            double disDivPower = distanceTo(entity) / (double) this.power;
            if (disDivPower > 1.0D) continue;

            Vector vecDistance = distanceToHead(entity);
            if (vecDistance.length() == 0.0) continue;

            vecDistance.normalize();

            double basicDamage = calculateDamage(entity, disDivPower);
            int explosionDamage = (int) ((basicDamage * basicDamage + basicDamage) * 4 * (double) power + 1.0D);

            if (!(entity instanceof GlowHumanEntity && ((GlowHumanEntity) entity).getGameMode() == GameMode.CREATIVE)) {
                EntityDamageEvent.DamageCause damageCause;
                if (source == null || source.getType() == EntityType.PRIMED_TNT) {
                    damageCause = EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
                } else {
                    damageCause = EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
                }
                entity.damage(explosionDamage, source, damageCause);
            }

            double enchantedDamage = calculateEnchantedDamage(basicDamage, entity);
            vecDistance.multiply(enchantedDamage);

            Vector currentVelocity = entity.getVelocity();
            currentVelocity.add(vecDistance);
            entity.setVelocity(currentVelocity);

            if (entity instanceof GlowPlayer) {
                affectedPlayers.add((GlowPlayer) entity);
            }
        }

        this.power = power;

        return affectedPlayers;
    }

    private double calculateEnchantedDamage(double basicDamage, GlowLivingEntity entity) {
        int level = 0; // TODO: calculate explosion protection level of entity's equipment

        if (level > 0) {
            float sub = level * 0.15f;
            double damage = basicDamage * sub;
            damage = Math.floor(damage);
            return basicDamage - damage;
        }

        return basicDamage;
    }

    private double calculateDamage(GlowEntity entity, double disDivPower) {
        double damage = world.rayTrace(location, entity);
        return (damage * (1D - disDivPower));
    }

    private Collection<GlowLivingEntity> getNearbyEntities() {
        // TODO: fetch only necessary entities
        List<LivingEntity> entities = world.getLivingEntities();
        List<GlowLivingEntity> nearbyEntities = new ArrayList<>();

        for (LivingEntity entity : entities) {
            if (distanceTo(entity) / (double) power < 1.) {
                nearbyEntities.add((GlowLivingEntity) entity);
            }
        }

        return nearbyEntities;
    }

    private double distanceTo(LivingEntity entity) {
        return location.clone().subtract(entity.getLocation()).length();
    }

    private Vector distanceToHead(LivingEntity entity) {
        return entity.getLocation().clone().subtract(location.clone().subtract(0, entity.getEyeHeight(), 0)).toVector();
    }

    ///////////////////////////////////////
    // Visualize
    private void playOutSoundAndParticles() {
        world.playSound(location, Sound.EXPLODE, 4, (1.0F + (random.nextFloat() - random.nextFloat()) * 0.2F) * 0.7F);

        if (this.power >= 2.0F && this.breakBlocks) {
            // send huge explosion
            world.showParticle(location, Particle.EXPLOSION_HUGE, 0, 0, 0, 0, 0);
        } else {
            // send large explosion
            world.showParticle(location, Particle.EXPLOSION_LARGE, 0, 0, 0, 0, 0);
        }
    }

    private void playOutExplosion(GlowPlayer player, Iterable<BlockVector> blocks) {
        Collection<ExplosionMessage.Record> records = new ArrayList<>();

        Location clientLoc = location.clone();
        clientLoc.setX((int) clientLoc.getX());
        clientLoc.setY((int) clientLoc.getY());
        clientLoc.setZ((int) clientLoc.getZ());

        for (BlockVector block : blocks) {
            byte x = (byte) (block.getBlockX() - clientLoc.getBlockX());
            byte y = (byte) (block.getBlockY() - clientLoc.getBlockY());
            byte z = (byte) (block.getBlockZ() - clientLoc.getBlockZ());
            records.add(new ExplosionMessage.Record(x, y, z));
        }

        Vector velocity = player.getVelocity();
        ExplosionMessage message = new ExplosionMessage((float) location.getX(), (float) location.getY(), (float) location.getZ(),
                5,
                (float) velocity.getX(), (float) velocity.getY(), (float) velocity.getZ(),
                records);

        player.getSession().send(message);
    }
}
