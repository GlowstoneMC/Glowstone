package net.glowstone.block.data.state;

import org.bukkit.block.BlockFace;

public interface StateUtil {

    BlockFace[] SIXTEEN_BLOCK_FACES = {BlockFace.SOUTH, BlockFace.SOUTH_SOUTH_WEST, BlockFace.SOUTH_WEST, BlockFace.WEST_SOUTH_WEST, BlockFace.WEST, BlockFace.WEST_NORTH_WEST, BlockFace.NORTH_WEST, BlockFace.NORTH_NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_EAST, BlockFace.EAST_NORTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.EAST, BlockFace.EAST_SOUTH_EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_SOUTH_EAST};

    static BlockFace getBlockFace(int id, BlockFace... faces){
        return faces[id];
    }

    static int getBlockFaceId(BlockFace face, BlockFace... faces){
        for(int i = 0; i < faces.length; i++){
            if(faces[i].equals(face)){
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
