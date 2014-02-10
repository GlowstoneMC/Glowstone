package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;
import org.bukkit.inventory.ItemStack;

public final class BlockPlacementMessage implements Message {

    private final int x, y, z, direction;
    private final ItemStack heldItem;
    private final int cursorX, cursorY, cursorZ;

    public BlockPlacementMessage(int x, int y, int z, int direction, ItemStack heldItem, int cursorX, int cursorY, int cursorZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.heldItem = heldItem;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
        this.cursorZ = cursorZ;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDirection() {
        return direction;
    }

    public ItemStack getHeldItem() {
        return heldItem;
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }

    public int getCursorZ() {
        return cursorZ;
    }

    @Override
    public String toString() {
        return "BlockPlacementMessage{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", direction=" + direction +
                ", heldItem=" + heldItem +
                ", cursorX=" + cursorX +
                ", cursorY=" + cursorY +
                ", cursorZ=" + cursorZ +
                '}';
    }
}
