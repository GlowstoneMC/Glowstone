package net.glowstone.entity.objects;

import static java.util.Comparator.comparingInt;

import com.flowpowered.network.Message;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.glowstone.EventFactory;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockFence;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowHangingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage.Action;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GlowLeashHitch extends GlowHangingEntity implements LeashHitch {

    public GlowLeashHitch(Location location) {
        this(location, BlockFace.SOUTH);
    }

    /**
     * Creates a leash hitch entity, for when a leash is hitched to a block such as a fencepost.
     *
     * @param location the location
     * @param clickedface the side of the block that was clicked
     *         (TODO: what difference does this make?)
     */
    public GlowLeashHitch(Location location, BlockFace clickedface) {
        super(location, clickedface);
        setSize(0.375f, 0.5f);
        setGravity(false);
    }

    /**
     * Get all LeashHitch Entities in the specified block.
     *
     * @param block the Block to search LeashHitch Entities in
     * @return a Stream of all found LeashHitch Entities
     */
    private static Stream<LeashHitch> getExistingLeashHitches(Block block) {
        Location location = block.getLocation().add(0.5, 0.5, 0.5);

        Collection<Entity> nearbyEntities = block.getWorld()
            .getNearbyEntities(location, 0.49, 0.49, 0.49);

        return nearbyEntities.stream()
            .filter(e -> e instanceof LeashHitch)
            .map(e -> (LeashHitch) e);
    }

    /**
     * Get the Leash Hitch to which entities should be attached at the block. Useful if multiple
     * Leash Hitches could exist.
     *
     * @param block the Block to get the relevant Leash Hitch for
     * @return either an already existing Leash Hitch, or a newly spawned one
     */
    public static LeashHitch getLeashHitchAt(Block block) {
        // Use the oldest leash entity as leash holder
        // If none found, create a new leash hitch
        Stream<LeashHitch> sorted = GlowLeashHitch.getExistingLeashHitches(block).sorted(
            comparingInt(Entity::getTicksLived)
                .reversed()
        );

        Optional<LeashHitch> first = sorted.findFirst();
        return first.orElseGet(
            () -> first.orElse(block.getWorld().spawn(block.getLocation(), LeashHitch.class)));
    }

    /**
     * Checks if an Entity of the specified type is allowed to be a leash holder.
     *
     * @param type type of the entity which wishes to become a leash holder
     * @return if the type is allowed as a leash holder true, otherwise false
     */
    public static boolean isAllowedLeashHolder(EntityType type) {
        return !(EntityType.ENDER_DRAGON.equals(type) || EntityType.WITHER.equals(type)
            || EntityType.PLAYER.equals(type) || EntityType.BAT.equals(type));
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return Lists.newArrayList(
            new SpawnObjectMessage(entityId, getUniqueId(), SpawnObjectMessage.LEASH_HITCH, x, y, z, 0,
                0),
            new EntityMetadataMessage(entityId, metadata.getEntryList())
        );
    }

    @Override
    public EntityType getType() {
        return EntityType.LEASH_HITCH;
    }

    @Override
    public boolean setFacingDirection(BlockFace blockFace, boolean force) {
        if (blockFace == null) {
            return false;
        }
        // facing most likely just does nothing for the leash hitch
        this.facing = HangingFace.getByBlockFace(blockFace);
        return true;
    }

    @Override
    public void setFacingDirection(BlockFace blockFace) {
        setFacingDirection(blockFace, false);
    }

    @Override
    public boolean shouldSave() {
        // The GlowLeashHitch on its own should never be saved
        // It is saved as part of the leashed living entity
        return false;
    }

    @Override
    public void pulse() {
        super.pulse();

        // seems like every hanging entity only checks every 5 Seconds if it can still survive
        if (ticksLived % (5 * 20) == 0) {
            Block block = location.getBlock();
            BlockType blockType = ItemTable.instance().getBlock(block.getType());

            if (!(blockType instanceof BlockFence)) {
                getExistingLeashHitches(block).forEach(Entity::remove);
            }
        }
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if ((message.getAction() == Action.ATTACK.ordinal())
            && message.getHandSlot() == EquipmentSlot.HAND) {
            remove();
        }

        if ((message.getAction() == Action.INTERACT.ordinal())
            && message.getHandSlot() == EquipmentSlot.HAND) {
            if (player.getLeashedEntities().isEmpty()) {
                List<GlowEntity> entities = ImmutableList.copyOf(getLeashedEntities());
                for (GlowEntity leashedEntity : entities) {
                    if (EventFactory.callEvent(
                        EventFactory.callEvent(new PlayerUnleashEntityEvent(leashedEntity, player)))
                        .isCancelled()) {
                        continue;
                    }
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        world.dropItemNaturally(this.location, new ItemStack(Material.LEASH));
                    }
                    leashedEntity.setLeashHolder(null);
                }
                if (getLeashedEntities().isEmpty()) {
                    remove();
                }
            } else {
                List<GlowEntity> entities = ImmutableList.copyOf(player.getLeashedEntities());
                for (GlowEntity leashedEntity : entities) {
                    if (EventFactory.callEvent(EventFactory
                        .callEvent(new PlayerLeashEntityEvent(leashedEntity, this, player)))
                        .isCancelled()) {
                        continue;
                    }
                    leashedEntity.setLeashHolder(this);
                }
            }
        }
        return super.entityInteract(player, message);
    }
}
