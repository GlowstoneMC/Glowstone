package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.physics.BlockBoundingBox;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.util.SoundInfo;
import net.glowstone.util.SoundUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/**
 * Base class for specific types of blocks.
 */
public class BlockType extends ItemType {

    protected static final BlockFace[] SIDES = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST};
    protected static final BlockFace[] ADJACENT = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    protected List<ItemStack> drops;

    /**
     * Gets the sound that will be played when a player places the block.
     *
     * @return The sound to be played
     */
    @Getter
    protected SoundInfo placeSound = new SoundInfo(Sound.BLOCK_WOOD_BREAK, 1F, 0.75F);

    ////////////////////////////////////////////////////////////////////////////
    // Setters for subclass use

    /**
     * Gets the BlockFace opposite of the direction the location is facing. Usually used to set the
     * way container blocks face when being placed.
     *
     * @param location Location to get opposite of
     * @param inverted If up/down should be used
     * @return Opposite BlockFace or EAST if yaw is invalid
     */
    protected static BlockFace getOppositeBlockFace(Location location, boolean inverted) {
        double rot = location.getYaw() % 360;
        if (inverted) {
            // todo: Check the 67.5 pitch in source. This is based off of WorldEdit's number for
            // this.
            double pitch = location.getPitch();
            if (pitch < -67.5D) {
                return BlockFace.DOWN;
            } else if (pitch > 67.5D) {
                return BlockFace.UP;
            }
        }
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 45) {
            return BlockFace.NORTH;
        } else if (45 <= rot && rot < 135) {
            return BlockFace.EAST;
        } else if (135 <= rot && rot < 225) {
            return BlockFace.SOUTH;
        } else if (225 <= rot && rot < 315) {
            return BlockFace.WEST;
        } else if (315 <= rot && rot < 360.0) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Public accessors

    protected final void setDrops(ItemStack... drops) {
        this.drops = Arrays.asList(drops);
    }

    /**
     * Get the items that will be dropped by digging the block.
     *
     * @param block The block being dug.
     * @param tool  The tool used or {@code null} if fists or no tool was used.
     * @return The drops that should be returned.
     */
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (drops == null) {
            // default calculation
            return Arrays.asList(new ItemStack(block.getType(), 1, block.getData()));
        } else {
            return Collections.unmodifiableList(drops);
        }
    }

    /**
     * Sets the sound that will be played when a player places the block.
     *
     * @param sound The sound.
     */
    public void setPlaceSound(Sound sound) {
        placeSound = new SoundInfo(sound, 1F, 0.75F);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    /**
     * Get the items that would be dropped if the block was successfully mined. This is used f.e. to
     * calculate TNT drops.
     *
     * @param block The block.
     * @return The drops from that block.
     */
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return getDrops(block, null);
    }

    /**
     * Create a new block entity at the given location.
     *
     * @param chunk The chunk to create the block entity at.
     * @param cx    The x coordinate in the chunk.
     * @param cy    The y coordinate in the chunk.
     * @param cz    The z coordinate in the chunk.
     * @return The new BlockEntity, or null if no block entity is used.
     */
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return null;
    }

    /**
     * Check whether the block can be placed at the given location.
     *
     * @param player  The player who placed the block.
     * @param block   The location the block is being placed at.
     * @param against The face the block is being placed against.
     * @return Whether the placement is valid.
     */
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return true;
    }

    /**
     * Called when a block is placed to calculate what the block will become.
     *
     * @param player     the player who placed the block
     * @param state      the BlockState to edit
     * @param holding    the ItemStack that was being held
     * @param face       the face off which the block is being placed
     * @param clickedLoc where in the block the click occurred
     */
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        state.setType(holding.getType());
        state.setData(holding.getData());
    }

    /**
     * Called after a block has been placed by a player.
     *
     * @param player   the player who placed the block
     * @param block    the block that was placed
     * @param holding  the the ItemStack that was being held
     * @param oldState The old block state before the block was placed.
     */
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
                           GlowBlockState oldState) {
        block.applyPhysics(oldState.getType(), block.getTypeId(), oldState.getRawData(),
            block.getData());
    }

    /**
     * Called when a player attempts to interact with (right-click) a block of this type already in
     * the world.
     *
     * @param player     the player interacting
     * @param block      the block interacted with
     * @param face       the clicked face
     * @param clickedLoc where in the block the click occurred
     * @return Whether the interaction occurred.
     */
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        return false;
    }

    /**
     * Called when a player attempts to destroy a block.
     *
     * @param player The player interacting
     * @param block  The block the player destroyed
     * @param face   The block face
     */
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        // do nothing
    }

    /**
     * Called after a player successfully destroys a block.
     *
     * @param player   The player interacting
     * @param block    The block the player destroyed
     * @param face     The block face
     * @param oldState The block state of the block the player destroyed.
     */
    public void afterDestroy(GlowPlayer player, GlowBlock block, BlockFace face,
                             GlowBlockState oldState) {
        block.applyPhysics(oldState.getType(), block.getTypeId(), oldState.getRawData(),
            block.getData());
    }

    /**
     * Called when the BlockType gets pulsed as requested.
     *
     * @param block The block that was pulsed
     */
    public void receivePulse(GlowBlock block) {
        block.getWorld().cancelPulse(block);
    }

    /**
     * Called when a player attempts to place a block on an existing block of this type. Used to
     * determine if the placement should occur into the air adjacent to the block (normal behavior),
     * or absorbed into the block clicked on.
     *
     * @param block   The block the player right-clicked
     * @param face    The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @return Whether the place should occur into the block given.
     */
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return false;
    }

    /**
     * Called to check if this block can be overridden by a block place which would occur inside it.
     *
     * @param block   The block being targeted by the placement
     * @param face    The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @return Whether this block can be overridden.
     */
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return block.isLiquid();
    }

    /**
     * Called when a neighboring block (within a 3x3x3 cube) has changed its type or data and
     * physics checks should occur.
     *
     * @param block        The block to perform physics checks for
     * @param face         The BlockFace to the changed block, or null if unavailable
     * @param changedBlock The neighboring block that has changed
     * @param oldType      The old type of the changed block
     * @param oldData      The old data of the changed block
     * @param newType      The new type of the changed block
     * @param newData      The new data of the changed block
     */
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
                                   Material oldType, byte oldData, Material newType, byte newData) {

    }

    /**
     * Called when this block has just changed to some other type.
     *
     * <p>This is called whenever {@link GlowBlock#setTypeIdAndData}, {@link GlowBlock#setType}
     * or {@link GlowBlock#setData} is called with physics enabled, and might
     * be called from plugins or other means of changing the block.
     *
     * @param block   The block that was changed
     * @param oldType The old Material
     * @param oldData The old data
     * @param newType The new Material
     * @param data    The new data
     */
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType,
                               byte data) {
        // do nothing
    }

    /**
     * <p>Called when the BlockType should calculate the current physics.</p>
     * <p>Subclasses should override {@link #updatePhysicsAfterEvent(GlowBlock)}
     * if they need a custom handling of the physics calculation</p>
     *
     * @param block The block
     */
    public final void updatePhysics(GlowBlock block) {
        if (!block.getWorld().isInitialized()) {
            return;
        }
        BlockPhysicsEvent event = EventFactory.getInstance()
            .callEvent(new BlockPhysicsEvent(block, block.getTypeId()));
        if (!event.isCancelled()) {
            updatePhysicsAfterEvent(block);
        }
    }

    public void updatePhysicsAfterEvent(GlowBlock block) {
        // do nothing
    }


    @Override
    public final void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face,
                                      ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        GlowBlock target = against.getRelative(face);
        final Material targetMat = ItemTable.instance().getBlock(
            target.getRelative(face.getOppositeFace()).getType()).getMaterial();

        // prevent building above the height limit
        final int maxHeight = target.getWorld().getMaxHeight();
        if (target.getLocation().getY() >= maxHeight) {
            GlowstoneMessages.Block.MAX_HEIGHT.send(player, maxHeight);
            return;
        }

        // check whether the block clicked against should absorb the placement
        BlockType againstType = ItemTable.instance().getBlock(against.getType());
        if (againstType != null) {
            if (againstType.canAbsorb(against, face, holding)) {
                target = against;
            } else if (!target.isEmpty()) {
                // air can always be overridden
                BlockType targetType = ItemTable.instance().getBlock(target.getType());
                if (targetType != null && !targetType.canOverride(target, face, holding)) {
                    return;
                }
            }
        }

        if (getMaterial().isSolid()) {
            BlockBoundingBox box = new BlockBoundingBox(target);
            List<Entity> entities = target.getWorld().getEntityManager()
                .getEntitiesInside(box, null);
            for (Entity e : entities) {
                if (e instanceof LivingEntity) {
                    return;
                }
            }
        }

        // call canBuild event
        boolean canBuild = true;
        switch (targetMat) {
            case SIGN_POST:
            case WALL_SIGN:
                if (player.isSneaking()) {
                    canBuild = canPlaceAt(player, target, face);
                } else {
                    return;
                }
                break;
            default:
                canBuild = canPlaceAt(player, target, face);
        }
        BlockCanBuildEvent canBuildEvent = new BlockCanBuildEvent(target, getId(), canBuild);
        if (!EventFactory.getInstance().callEvent(canBuildEvent).isBuildable()) {
            //revert(player, target);
            return;
        }

        // grab states and update block
        GlowBlockState oldState = target.getState();
        GlowBlockState newState = target.getState();
        ItemType itemType = ItemTable.instance().getItem(holding.getType());
        if (itemType.getPlaceAs() == null) {
            placeBlock(player, newState, face, holding, clickedLoc);
        } else {
            placeBlock(player, newState, face,
                new ItemStack(itemType.getPlaceAs().getMaterial(), holding.getAmount(),
                    holding.getDurability()), clickedLoc);
        }
        newState.update(true);

        // call blockPlace event
        BlockPlaceEvent event = new BlockPlaceEvent(target, oldState, against, holding, player,
            canBuild, hand);
        EventFactory.getInstance().callEvent(event);
        if (event.isCancelled() || !event.canBuild()) {
            oldState.update(true);
            return;
        }

        // play the placement sound, except for the current player (handled by the client)
        SoundUtil.playSoundAtLocationExcept(target.getLocation(), getPlaceSound().getSound(),
            (getPlaceSound().getVolume() + 1F) / 2F, getPlaceSound().getPitch() * 0.8F, player);

        // do any after-place actions
        afterPlace(player, target, holding, oldState);

        // deduct from stack if not in creative mode
        if (player.getGameMode() != GameMode.CREATIVE) {
            holding.setAmount(holding.getAmount() - 1);
        }
    }

    /**
     * Called to check if this block can perform random tick updates.
     *
     * @return Whether this block updates on tick.
     */
    public boolean canTickRandomly() {
        return false;
    }

    /**
     * Called when this block needs to be updated.
     *
     * @param block The block that needs an update
     */
    public void updateBlock(GlowBlock block) {
        // do nothing
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helper methods

    /**
     * Called when a player left clicks a block.
     *
     * @param player  the player who clicked the block
     * @param block   the block that was clicked
     * @param holding the ItemStack that was being held
     */
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        // do nothing
    }

    /**
     * Display the warning for finding the wrong MaterialData subclass.
     *
     * @param clazz The expected subclass of MaterialData.
     * @param data  The actual MaterialData found.
     */
    protected void warnMaterialData(Class<?> clazz, MaterialData data) {
        ConsoleMessages.Warn.Block.WRONG_MATERIAL_DATA.log(
            getMaterial(), getClass().getSimpleName(), clazz.getSimpleName(), data);
    }

    public void onRedstoneUpdate(GlowBlock block) {
        // do nothing
    }

    /**
     * Called when an entity gets updated on top of the block.
     *
     * @param block  the block that was stepped on
     * @param entity the entity
     */
    public void onEntityStep(GlowBlock block, LivingEntity entity) {
        // do nothing
    }

    @Override
    public BlockType getPlaceAs() {
        return this;
    }

    public void requestPulse(GlowBlockState state) {
        // do nothing
    }

    /**
     * The rate at which the block should be pulsed.
     *
     * @param block the block
     * @return 0    if the block should not pulse, or a number of ticks between pulses.
     */
    public int getPulseTickSpeed(GlowBlock block) {
        // Override if needs pulse
        return 0;
    }

    /**
     * Whether the block should only be pulsed once.
     *
     * @param block the block
     * @return true if the block should be pulsed once, false otherwise.
     */
    public boolean isPulseOnce(GlowBlock block) {
        return true;
    }
}
