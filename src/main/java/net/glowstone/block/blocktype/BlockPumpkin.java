package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.monster.GlowIronGolem;
import net.glowstone.inventory.ToolType;
import net.glowstone.util.pattern.BlockPattern;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Pumpkin;
import org.bukkit.util.Vector;

import static org.bukkit.Material.*;

public class BlockPumpkin extends BlockDirectDrops {

    private static final BlockPattern IRONGOLEM_PATTERN = new BlockPattern(
            new BlockPattern.PatternItem(PUMPKIN,       (byte) -1, 1, 0),
            new BlockPattern.PatternItem(IRON_BLOCK,    (byte) 0, 0, 1),
            new BlockPattern.PatternItem(IRON_BLOCK,    (byte) 0, 1, 1),
            new BlockPattern.PatternItem(IRON_BLOCK,    (byte) 0, 2, 1),
            new BlockPattern.PatternItem(IRON_BLOCK,    (byte) 0, 1, 2)
    );

    private static final BlockPattern SNOWMAN_PATTERN = new BlockPattern(
            new BlockPattern.PatternItem(PUMPKIN,       (byte) -1, 0, 0),
            new BlockPattern.PatternItem(SNOW_BLOCK,    (byte) 0, 0, 1),
            new BlockPattern.PatternItem(SNOW_BLOCK,    (byte) 0, 0, 2)
    );

    public BlockPumpkin() {
        super(PUMPKIN, ToolType.AXE);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Pumpkin) {
            ((Pumpkin) data).setFacingDirection(player.getDirection());
            state.setData(data);
        } else {
            warnMaterialData(Pumpkin.class, data);
        }
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        super.afterPlace(player, block, holding, oldState);
        // Golems
        Location location = block.getLocation();
        if (!spawnIronGolem(location.clone())) {
            spawnSnowman(location.clone());
        }
    }

    private boolean spawnIronGolem(Location location) {
        if (IRONGOLEM_PATTERN.matches(location, true, 1, 0)) {
            Entity entity = location.getWorld().spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.IRON_GOLEM);
            ((GlowIronGolem) entity).setPlayerCreated(true);
            return true;
        }
        return false;
    }

    private void spawnSnowman(Location location) {
        if (SNOWMAN_PATTERN.matches(location, true, 0, 0)) {
            location.getWorld().spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.SNOWMAN);
        }
    }
}
