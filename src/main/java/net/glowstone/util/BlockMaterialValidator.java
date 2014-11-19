package net.glowstone.util;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

public class BlockMaterialValidator implements Validator<Block> {

    private final Set<Material> validMaterials;

    public BlockMaterialValidator(Set<Material> validMaterials) {
        this.validMaterials = validMaterials;
    }

    @Override
    public boolean isValid(Block block) {
        return validMaterials.contains(block.getType());
    }

}
