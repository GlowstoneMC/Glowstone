package net.glowstone.block.state;

import net.glowstone.constants.ItemIds;
import org.bukkit.Material;

public class InvalidBlockStateException extends Exception {

    public InvalidBlockStateException(Material material, String state) {
        super("'" + state + "' is not a state for block " + ItemIds.getName(material));
    }

    public InvalidBlockStateException(Material material, BlockStateData state) {
        super("'" + state.toString() + "' is not a state for block " + ItemIds.getName(material));
    }
}
