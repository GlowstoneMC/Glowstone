package net.glowstone.msg.handler;

import org.bukkit.block.BlockFace;

public class MessageHandlerUtils {
    public static BlockFace messageToBlockFace(int id) {
        BlockFace face;
        switch (id) {
            case 0: face = BlockFace.DOWN; break;
            case 1: face = BlockFace.UP; break;
            case 2: face = BlockFace.EAST; break;
            case 3: face = BlockFace.WEST; break;
            case 4: face = BlockFace.NORTH; break;
            case 5: face = BlockFace.SOUTH; break;
            default: face = BlockFace.SELF;
        }
        return face;
    }
}
