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
    Material iteminframe;
    int rot = 0;


    public GlowItemFrame(GlowPlayer player, Location location, BlockFace clickedface) {

        super(location);
        this.face = clickedface;
        if (player != null) { // could be Anvil loading....
            ItemStack is = player.getItemInHand();
            int amount = is.getAmount();
            is.setAmount(amount - 1);
            if (is.getAmount() <= 0) {
                is = null;
            }
            player.setItemInHand(is);

            metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, 0);
            metadata.set(MetadataIndex.ITEM_FRAME_ITEM, new ItemStack(Material.AIR));
            metadata.set(MetadataIndex.AIR_TIME, 300);
            iteminframe = Material.AIR;
        }
    }

    public void SetItemInFrame(ItemStack is) {
        if (is == null) {
            is = new ItemStack(Material.AIR, 1);
        }
        is.setAmount(1);
        iteminframe = is.getType();
        metadata.set(MetadataIndex.ITEM_FRAME_ITEM, is);
        metadata.set(MetadataIndex.ITEM_FRAME_ROTATION, 0);
    }

    public void SetItemFrameRotation(int rotation) {
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
            if (iteminframe == Material.AIR) {
                ItemStack isinhand = player.getItemInHand();
                int amount = player.getItemInHand().getAmount();
                SetItemInFrame(isinhand);
                isinhand.setAmount(amount - 1);
                if (isinhand.getAmount() <= 0) {
                    isinhand = null;
                }
                player.setItemInHand(isinhand);
            } else {
                rot++;
                if (rot > 7) {
                    rot = 0;
                }
                SetItemFrameRotation(rot);
            }
        }
        if (message.getAction() == 1) {
            if (isEmpty()) {
                remove();
            } else {
                SetItemInFrame(new ItemStack(Material.AIR));
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
                    world.dropItemNaturally(location, new ItemStack(iteminframe));
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
        return iteminframe == null || iteminframe == Material.AIR;
    }

    void GenerateTeleportMessage(BlockFace face) {
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
            if (player.canSee(key)) {
                player.getSession().send(new EntityTeleportMessage(id, Position.getIntX(location) + xoffset, Position.getIntY(location), Position.getIntZ(location) + zoffset, yaw, 0));
            }
        }
    }

    @Override
    public boolean setFacingDirection(BlockFace blockface, boolean force) {
        GenerateTeleportMessage(blockface);
        return true;
    }

    @Override
    public void setFacingDirection(BlockFace blockface) {
        GenerateTeleportMessage(blockface);
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
        return new ItemStack(iteminframe, 1);
    }

    /**
     * [Bukkit Missing] 1.8 has 8 directions now not 4. Bukkit does not support
     * this apparently.
     */
    @Override
    public Rotation getRotation() {
        switch (rot) {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE;
            case 2:
                return Rotation.CLOCKWISE;
            case 3:
                return Rotation.CLOCKWISE;
            case 4:
                return Rotation.FLIPPED;
            case 5:
                return Rotation.COUNTER_CLOCKWISE;
            case 6:
                return Rotation.COUNTER_CLOCKWISE;
            case 7:
                return Rotation.COUNTER_CLOCKWISE;
        }

        return Rotation.NONE;
    }

    public int getRotationNumber() {
        return rot;
    }

    public void setRotationNumber(int rotation) {
        SetItemFrameRotation(rotation);
    }

    @Override
    public void setItem(ItemStack is) {
        SetItemInFrame(is);
    }

    /**
     * [Bukkit Missing] 1.8 has 8 directions now not 4. Bukkit does not support
     * this apparently.
     */
    @Override
    public void setRotation(Rotation rotation) throws IllegalArgumentException {
        switch (rotation) {
            case NONE:
                SetItemFrameRotation(0);
                break;
            case CLOCKWISE:
                SetItemFrameRotation(2);
                break;
            case FLIPPED:
                SetItemFrameRotation(4);
                break;
            case COUNTER_CLOCKWISE:
                SetItemFrameRotation(6);
                break;
        }
    }
}
