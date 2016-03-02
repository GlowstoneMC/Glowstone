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

    private BlockFace face;
    private Material itemInFrame;
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
        }
        metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, 0);
        metadata.set(MetadataIndex.ITEM_FRAME_ITEM, new ItemStack(Material.AIR));
        itemInFrame = Material.AIR;
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

    // //////////////////////////////////////////////////////////////////////////
    // Overrides

    private static BlockFace inverseGetFace(int face) {
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

    public void setItemInFrame(ItemStack is) {
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

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {
            if (itemInFrame == Material.AIR) {
                ItemStack isInHand = player.getItemInHand();
                if (isInHand != null) {
                    setItemInFrame(isInHand);
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
        if (message.getAction() == InteractEntityMessage.Action.ATTACK.ordinal()) {
            if (isEmpty()) {
                remove();
            } else {
                setItemInFrame(new ItemStack(Material.AIR));
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

        return Arrays.asList((new SpawnObjectMessage(id, getUniqueId(), 71, ((location.getBlockX() + xoffset) * 32), ((location.getBlockY() * 32)), ((location.getBlockZ() + zoffset) * 32), 0, yaw, getFacingNumber(face), 0, 0, 0)), new EntityMetadataMessage(id, metadata.getEntryList()));
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
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                player.getSession().send(new EntityTeleportMessage(id, x + xoffset, y, z + zoffset, yaw, 0));
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
    public void setGlowing(boolean b) {

    }

    @Override
    public boolean isGlowing() {
        return false;
    }

    @Override
    public BlockFace getAttachedFace() {
        return inverseGetFace(getFacingNumber());
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
    public void setItem(ItemStack is) {
        setItemInFrame(is);
    }

    @Override
    public Rotation getRotation() {
        switch (rot) {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE_45;
            case 2:
                return Rotation.CLOCKWISE;
            case 3:
                return Rotation.CLOCKWISE_135;
            case 4:
                return Rotation.FLIPPED;
            case 5:
                return Rotation.FLIPPED_45;
            case 6:
                return Rotation.COUNTER_CLOCKWISE;
            case 7:
                return Rotation.COUNTER_CLOCKWISE_45;
        }

        return Rotation.NONE;
    }

    @Override
    public void setRotation(Rotation rotation) {
        rot = rotation.ordinal();
        setItemFrameRotation(rot);
    }
}
