package net.glowstone.entity.objects;

import com.flowpowered.networking.Message;
import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.Position;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;


public final class GlowItemFrame extends GlowEntity implements ItemFrame {

    BlockFace face;
    Material itemInFrame;
    private int rot = 0;

    public GlowItemFrame(GlowPlayer player, Location location, BlockFace clickedface) {

        super(location);
        this.face = clickedface;
        if (player != null) { // could be Anvil loading....
            if (player.getGameMode() != GameMode.CREATIVE) {
                ItemStack is = player.getItemInHand();
                int amount = is.getAmount();
                is.setAmount(amount - 1);
                if (is.getAmount() <= 0) {
                    is = null;
                }
                player.setItemInHand(is);
            }

            metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, 0);
            metadata.set(MetadataIndex.ITEM_FRAME_ITEM, new ItemStack(Material.AIR));
            metadata.set(MetadataIndex.AIR_TIME, 300);
            itemInFrame = Material.AIR;
        }
    }

    public void setIteminFrame(ItemStack is) {
        if (is == null) {
            is = new ItemStack(Material.AIR, 1);
        }
        is.setAmount(1);
        itemInFrame = is.getType();
        metadata.set(MetadataIndex.ITEM_FRAME_ITEM, is);
        metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, 0);
    }

    public void setItemFrameRotation(int rotation) {
        metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, (rotation));
    }

    // //////////////////////////////////////////////////////////////////////////
    // Overrides

    /**
     * Represents an item frame that is also an 0 = INTERACT, 1 = ATTACK, 2 =
     * INTERACT_AT (2 - Right/1- Left Click), 0 is always called with 1 or 2.
     */
    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == 2) {
            if (itemInFrame == Material.AIR) {
                ItemStack isInHand = player.getItemInHand();
                if (isInHand != null) {
                    setIteminFrame(isInHand);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        int amount = player.getItemInHand().getAmount();
                        isInHand.setAmount(amount - 1);
                        if (isInHand.getAmount() <= 0) {
                            isInHand = null;
                        }
                        player.setItemInHand(isInHand);
                    }
                }
            } else {
                rot++;
                if (rot > 7) {
                    rot = 0;
                }
                setItemFrameRotation(rot);
            }
        }
        if (message.getAction() == 1) {
            if (isEmpty()) {
                remove();
            } else {
                setIteminFrame(new ItemStack(Material.AIR));
                rot = 0;
            }
        }
        return true;
    }

    @Override
    public void pulse() {
        super.pulse();

        if (ticksLived % (11) == 0) {
            if ((world.getBlockAt(new Location(getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()))).getType() == Material.AIR) {
                world.dropItemNaturally(location, new ItemStack(Material.ITEM_FRAME));
                if (!isEmpty()) {
                    world.dropItemNaturally(location, new ItemStack(itemInFrame));
                }
                remove();
            }
        }
    }

    @Override
    public List<Message> createSpawnMessage() {

        int xoffset = 0;
        int zoffset = 0;
        int yaw = 0;
        switch (getFacingNumber(face)) {
            case 1:
                xoffset = -1;
                yaw = 64;
                break;
            case 2:
                zoffset = -1;
                yaw = -128;
                break;
            case 3:
                xoffset = 1;
                yaw = -64;
                break;
            case 0:
                zoffset = 1;
                yaw = 0;
                break;
        }

        return Arrays.asList((new SpawnObjectMessage(id, 71, ((location.getBlockX() + xoffset) * 32), ((location.getBlockY() * 32)), ((location.getBlockZ() + zoffset) * 32), 0, yaw, getFacingNumber(face), 0, 0, 0)), new EntityMetadataMessage(id, metadata.getEntryList()));
    }

    private static byte getFacingNumber(BlockFace face) {
        switch (face) {
            case SOUTH:
                return 0;
            case WEST:
                return 1;
            case NORTH:
                return 2;
            case EAST:
                return 3;
            default:
                return 0;
        }
    }

    private static BlockFace getFace(int face) {
        switch (face) {
            case 0:
                return BlockFace.SOUTH;
            case 1:
                return BlockFace.WEST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.EAST;
            default:
                return BlockFace.SOUTH;
        }
    }

    private static BlockFace inversegetFace(int face) {
        switch (face) {
            case 0:
                return BlockFace.NORTH;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.SOUTH;
            case 3:
                return BlockFace.WEST;
            default:
                return BlockFace.NORTH;
        }
    }

    @Override
    public boolean isEmpty() {
        return itemInFrame == null || itemInFrame == Material.AIR;
    }

    void generateTeleportMessage(BlockFace face) {
        int xoffset = 0;
        int zoffset = 0;
        int yaw = 0;
        switch (getFacingNumber(face)) {
            case 1:
                xoffset = -32;
                yaw = 64;
                break;
            case 2:
                zoffset = -32;
                yaw = -128;
                break;
            case 3:
                xoffset = 32;
                yaw = -64;
                break;
            case 0:
                zoffset = 32;
                yaw = 0;
                break;
        }
        Location itemframelocation = location;
        GlowChunk.Key key = new GlowChunk.Key(itemframelocation.getBlockX() >> 4, itemframelocation.getBlockZ() >> 4);
        for (GlowPlayer player : getWorld().getRawPlayers()) {
            if (player.canSeeChunk(key)) {
                player.getSession().send(new EntityTeleportMessage(id, Position.getIntX(location) + xoffset, Position.getIntY(location), Position.getIntZ(location) + zoffset, yaw, 0));
            }
        }
    }

    @Override
    public boolean setFacingDirection(BlockFace blockface, boolean force) {
        generateTeleportMessage(blockface);
        return true;
    }

    @Override
    public void setFacingDirection(BlockFace blockface) {
        generateTeleportMessage(blockface);
    }

    @Override
    public EntityType getType() {
        return EntityType.ITEM_FRAME;
    }

    @Override
    public BlockFace getAttachedFace() {
        return inversegetFace(getFacingNumber());
    }

    @Override
    public BlockFace getFacing() {
        return face;
    }

    public int getFacingNumber() {
        return getFacingNumber(face);
    }

    public void setFacingDirectionNumber(int direction) {
        face = getFace(direction);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(itemInFrame, 1);
    }

    @Override
    @Deprecated
    public Rotation getRotation() {
        switch (rot) {
            case 0:
            case 1:
                return Rotation.NONE;
            case 2:
            case 3:
                return Rotation.CLOCKWISE;
            case 4:
            case 5:
                return Rotation.FLIPPED;
            case 6:
            case 7:
                return Rotation.COUNTER_CLOCKWISE;
        }

        return Rotation.NONE;
    }

    @Override
    public double getRotationAngle() {
        return rot * 45;
    }

    @Override
    public void setItem(ItemStack is) {
        setIteminFrame(is);
    }

    @Override
    @Deprecated
    public void setRotation(Rotation rotation) {
        setRotationAngle(rotation.getRotation()); 
    }

    @Override
    public void setRotationAngle(double rotation) {
        rot = (int) Math.ceil((rotation % 360) / 45);
        setItemFrameRotation(rot);
    }
}
