package net.glowstone.block.blocktype;

import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.FurnaceEntity;
import net.glowstone.block.function.BlockFunctions.BlockFunctionAbsorb;
import net.glowstone.block.function.BlockFunctions.BlockFunctionDestroy;
import net.glowstone.block.function.BlockFunctions.BlockFunctionDestroyAfter;
import net.glowstone.block.function.BlockFunctions.BlockFunctionInteract;
import net.glowstone.block.function.BlockFunctions.BlockFunctionPhysics;
import net.glowstone.block.function.BlockFunctions.BlockFunctionPlaceAllow;
import net.glowstone.block.function.BlockFunctions.BlockFunctionStep;
import net.glowstone.block.function.BlockFunctions.BlockFunctionTick;
import net.glowstone.block.function.ItemFunction;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.physics.BlockBoundingBox;
import net.glowstone.inventory.GlowAnvilInventory;
import net.glowstone.util.SoundInfo;
import net.glowstone.util.SoundUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DoublePlantSpecies;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Base class for specific types of blocks.
 */
public class BlockType extends ItemType {
    protected static final BlockFace[] SIDES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST};
    protected static final BlockFace[] ADJACENT = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    protected List<ItemStack> drops;

    /**
     * Gets the sound that will be played when a player places the block.
     *
     * @return The sound to be played
     */
    @Getter
    protected SoundInfo placeSound = new SoundInfo(Sound.BLOCK_WOOD_BREAK, 1F, 0.75F);

    public BlockType() {
        super();
        addFunction(Functions.DestroyAfter.DEFAULT);
    }

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
     * @param tool The tool used or {@code null} if fists or no tool was used.
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
     * @param cx The x coordinate in the chunk.
     * @param cy The y coordinate in the chunk.
     * @param cz The z coordinate in the chunk.
     * @return The new BlockEntity, or null if no block entity is used.
     */
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return null;
    }

    /**
     * Check whether the block can be placed at the given location.
     *
     * @param block The location the block is being placed at.
     * @param against The face the block is being placed against.
     * @return Whether the placement is valid.
     */
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        List<ItemFunction> funcs = functions.get("block.place.allow");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                if (!((BlockFunctionPlaceAllow) function).apply(block, against)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Called when a block is placed to calculate what the block will become.
     *
     * @param player the player who placed the block
     * @param state the BlockState to edit
     * @param holding the ItemStack that was being held
     * @param face the face off which the block is being placed
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
     * @param player the player who placed the block
     * @param block the block that was placed
     * @param holding the the ItemStack that was being held
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
     * @param player the player interacting
     * @param block the block interacted with
     * @param face the clicked face
     * @param clickedLoc where in the block the click occurred
     * @return Whether the interaction occurred.
     */
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        List<ItemFunction> funcs = functions.get("block.interact");
        if (funcs != null) {
            boolean result = false;
            for (ItemFunction function : funcs) {
                if (result) {
                    ((BlockFunctionInteract) function).apply(player, block, face, clickedLoc);
                } else {
                    result = ((BlockFunctionInteract) function).apply(player, block, face,
                        clickedLoc);
                }
            }
            return result;
        }
        return false;
    }

    /**
     * Called when a player attempts to destroy a block.
     *
     * @param player The player interacting
     * @param block The block the player destroyed
     * @param face The block face
     */
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        List<ItemFunction> funcs = functions.get("block.destroy");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                ((BlockFunctionDestroy) function).apply(player, block, face);
            }
        }
    }

    /**
     * Called after a player successfully destroys a block.
     *
     * @param player The player interacting
     * @param block The block the player destroyed
     * @param face The block face
     * @param oldState The block state of the block the player destroyed.
     */
    public void afterDestroy(GlowPlayer player, GlowBlock block, BlockFace face,
        GlowBlockState oldState) {
        List<ItemFunction> funcs = functions.get("block.destroy.after");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                ((BlockFunctionDestroyAfter) function).apply(player, block, face, oldState);
            }
        }
    }

    /**
     * Called when the BlockType gets pulsed as requested.
     *
     * @param block The block that was pulsed
     */
    public void receivePulse(GlowBlock block) {
        List<ItemFunction> funcs = functions.get("block.tick");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                ((BlockFunctionTick) function).apply(block);
            }
        } else {
            block.getWorld().cancelPulse(block);
        }
    }

    /**
     * Called when a player attempts to place a block on an existing block of this type. Used to
     * determine if the placement should occur into the air adjacent to the block (normal behavior),
     * or absorbed into the block clicked on.
     *
     * @param block The block the player right-clicked
     * @param face The face on which the click occurred
     * @param holding The ItemStack the player was holding
     * @return Whether the place should occur into the block given.
     */
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        List<ItemFunction> funcs = functions.get("block.absorb");
        if (funcs != null) {
            boolean result = false;
            for (ItemFunction function : funcs) {
                if (!result) {
                    ((BlockFunctionAbsorb) function).apply(block, face, holding);
                } else {
                    result = ((BlockFunctionAbsorb) function).apply(block, face, holding);
                }
            }
            return result;
        }
        return false;
    }

    /**
     * Called to check if this block can be overridden by a block place which would occur inside it.
     *
     * @param block The block being targeted by the placement
     * @param face The face on which the click occurred
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
     * @param block The block to perform physics checks for
     * @param face The BlockFace to the changed block, or null if unavailable
     * @param changedBlock The neighboring block that has changed
     * @param oldType The old type of the changed block
     * @param oldData The old data of the changed block
     * @param newType The new type of the changed block
     * @param newData The new data of the changed block
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
     * @param block The block that was changed
     * @param oldType The old Material
     * @param oldData The old data
     * @param newType The new Material
     * @param data The new data
     */
    public void onBlockChanged(GlowBlock block, Material oldType, byte oldData, Material newType,
        byte data) {
        // do nothing
    }

    /**
     * Called when the BlockType should calculate the current physics.
     *
     * @param block The block
     */
    public void updatePhysics(GlowBlock block) {
        List<ItemFunction> funcs = functions.get("block.physics");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                ((BlockFunctionPhysics) function).apply(block);
            }
        }
    }

    @Override
    public final void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        GlowBlock target = against.getRelative(face);
        final Material targetMat = ItemTable.instance().getBlock(
            target.getRelative(face.getOppositeFace()).getType()).getMaterial();

        // prevent building above the height limit
        if (target.getLocation().getY() >= target.getWorld().getMaxHeight()) {
            player.sendMessage(
                ChatColor.RED + "The height limit for this world is " + target.getWorld()
                    .getMaxHeight() + " blocks");
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
                    canBuild = canPlaceAt(target, face);
                } else {
                    return;
                }
                break;
            default:
                canBuild = canPlaceAt(target, face);
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
     * @param player the player who clicked the block
     * @param block the block that was clicked
     * @param holding the ItemStack that was being held
     */
    public void leftClickBlock(GlowPlayer player, GlowBlock block, ItemStack holding) {
        // do nothing
    }

    /**
     * Display the warning for finding the wrong MaterialData subclass.
     *
     * @param clazz The expected subclass of MaterialData.
     * @param data The actual MaterialData found.
     */
    protected void warnMaterialData(Class<?> clazz, MaterialData data) {
        GlowServer.logger.warning(
            "Wrong MaterialData for " + getMaterial() + " (" + getClass().getSimpleName()
                + "): expected " + clazz.getSimpleName() + ", got " + data);
    }

    public void onRedstoneUpdate(GlowBlock block) {
        // do nothing
    }

    /**
     * Called when an entity gets updated on top of the block.
     *
     * @param block the block that was stepped on
     * @param entity the entity
     */
    public void onEntityStep(GlowBlock block, LivingEntity entity) {
        List<ItemFunction> funcs = functions.get("block.step");
        if (funcs != null) {
            for (ItemFunction function : funcs) {
                ((BlockFunctionStep) function).apply(block, entity);
            }
        }
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

    /**
     * Vanilla builtins for block functions.
     */
    public static class Functions {
        public static class PlaceAllow {

        }

        public static class Interact {
            /**
             * Opens an anvil inventory.
             */
            public static final BlockFunctionInteract ANVIL = (player, block, face, clickedLoc) -> player.openInventory(new GlowAnvilInventory(player)) != null;

            /**
             * Opens a chest.
             */
            public static final BlockFunctionInteract CHEST = (player, block, face, clickedLoc) -> {
                Chest chest = (Chest) block.getState();
                player.openInventory(chest.getInventory());
                player.incrementStatistic(Statistic.CHEST_OPENED);
                return true;
            };

            /**
             * Plays a record.
             */
            public static final BlockFunctionInteract JUKEBOX = (player, block, face, clickedLoc) -> {
                Jukebox jukebox = (Jukebox) block.getState();
                if (jukebox.isPlaying()) {
                    jukebox.eject();
                    jukebox.update();
                    return true;
                }
                ItemStack handItem = player.getItemInHand();
                if (handItem != null && handItem.getType().isRecord()) {
                    jukebox.setPlaying(handItem.getType());
                    jukebox.update();
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        handItem.setAmount(handItem.getAmount() - 1);
                        player.setItemInHand(handItem);
                    }
                    return true;
                }
                return false;
            };

            /**
             * Tunes a note.
             */
            public static final BlockFunctionInteract NOTE = (player, block, face, clickedLoc) -> {
                NoteBlock noteBlock = (NoteBlock) block.getState();
                Note note = noteBlock.getNote();
                noteBlock.setNote(new Note(note.getId() == 24 ? 0 : note.getId() + 1));
                noteBlock.update();
                return noteBlock.play();
            };
        }

        public static class Destroy {
            public static final BlockFunctionDestroy DOUBLE_PLANT = (player, block, face) -> {
                DoublePlantSpecies species = ((DoublePlant) block.getState().getData()).getSpecies();
                if (species == DoublePlantSpecies.PLANT_APEX) {
                    GlowBlock blockUnder = block.getRelative(BlockFace.DOWN);
                    if (!(blockUnder.getState().getData() instanceof DoublePlant)) {
                        return;
                    }
                    blockUnder.setType(Material.AIR);
                } else {
                    GlowBlock blockTop = block.getRelative(BlockFace.UP);
                    if (!(blockTop.getState().getData() instanceof DoublePlant)) {
                        return;
                    }
                    blockTop.setType(Material.AIR);
                }
            };

            public static final BlockFunctionDestroy JUKEBOX = (player, block, face) -> {
                Jukebox jukebox = (Jukebox) block.getState();
                if (jukebox.eject()) {
                    jukebox.update();
                }
            };
        }

        public static class DestroyAfter {
            public static final BlockFunctionDestroyAfter DEFAULT = (player, block, face, oldState) -> block.applyPhysics(oldState.getType(), block.getTypeId(), oldState.getRawData(), block.getData());
        }

        public static class Tick {
            public static final BlockFunctionTick FURNACE = block -> ((FurnaceEntity) block.getBlockEntity()).burn();
            public static final BlockFunctionTick UPDATE_PHYSICS = block -> ItemTable.instance().getBlock(block.getType()).updatePhysics(block);
            public static final BlockFunctionTick UPDATE_BLOCK = block -> ItemTable.instance().getBlock(block.getType()).updateBlock(block);
        }

        public static class Absorb {
            public static final BlockFunctionAbsorb ALWAYS = (block, face, holding) -> true;
            public static final BlockFunctionAbsorb SNOW = (block, face, holding) -> holding.getType() == Material.SNOW && block.getData() < 7 || block.getData() == 0;
        }

        public static class Physics {

        }

        public static class Step {
            public static final BlockFunctionStep MAGMA = (block, entity) -> entity.damage(1.0, EntityDamageEvent.DamageCause.FIRE);
        }
    }
}
