package net.glowstone.block.blocktype;

import static org.bukkit.Material.IRON_BLOCK;
import static org.bukkit.Material.PUMPKIN;
import static org.bukkit.Material.SNOW_BLOCK;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.monster.GlowIronGolem;
import net.glowstone.util.pattern.BlockPattern;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BlockPumpkin extends BlockPumpkinBase {

    private static final BlockPattern IRONGOLEM_PATTERN = new BlockPattern(
        new BlockPattern.PatternItem(PUMPKIN, (byte) -1, 1, 0),
        new BlockPattern.PatternItem(IRON_BLOCK, (byte) 0, 0, 1),
        new BlockPattern.PatternItem(IRON_BLOCK, (byte) 0, 1, 1),
        new BlockPattern.PatternItem(IRON_BLOCK, (byte) 0, 2, 1),
        new BlockPattern.PatternItem(IRON_BLOCK, (byte) 0, 1, 2)
    );

    private static final BlockPattern SNOWMAN_PATTERN = new BlockPattern(
        new BlockPattern.PatternItem(PUMPKIN, (byte) -1, 0, 0),
        new BlockPattern.PatternItem(SNOW_BLOCK, (byte) 0, 0, 1),
        new BlockPattern.PatternItem(SNOW_BLOCK, (byte) 0, 0, 2)
    );

    public BlockPumpkin() {
        super(PUMPKIN);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        super.afterPlace(player, block, holding, oldState);
        // Golems
        Location location = block.getLocation();
        if (!spawnIronGolem(location.clone())) {
            spawnSnowman(location.clone());
        }
    }

    private boolean spawnIronGolem(Location location) {
        if (IRONGOLEM_PATTERN.matches(location, true, 1, 0)) {
            Entity entity = location.getWorld()
                .spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.IRON_GOLEM);
            ((GlowIronGolem) entity).setPlayerCreated(true);
            return true;
        }
        return false;
    }

    private void spawnSnowman(Location location) {
        if (SNOWMAN_PATTERN.matches(location, true, 0, 0)) {
            location.getWorld()
                .spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.SNOWMAN);
        }
    }
}
