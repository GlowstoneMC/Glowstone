package net.glowstone.block.blocktype;

import static org.bukkit.Material.STONE_PLATE;

import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class BlockPressurePlate extends BlockNeedsTool {
    public BlockPressurePlate(Material material) {
        setDrops(new ItemStack(material));
    }

    @Override
    public void updatePhysicsAfterEvent(GlowBlock block) {
        super.updatePhysicsAfterEvent(block);
        Material type = block.getType();
        Collection<? extends Entity> entities;
        Location collisionCenter = block.getLocation().add(0, 0.125, 0);
        if (type == STONE_PLATE) {
            entities = collisionCenter.getNearbyLivingEntities(0.5, 0.25, 0.5);
        } else {
            entities = collisionCenter.getNearbyEntities(0.5, 0.25, 0.5);
        }
        entities.removeIf(e -> !e.isOnGround());
        entities.removeIf(Entity::isDead);
        int outputLevel;
        switch (block.getType()) {
            case WOOD_PLATE:
            case STONE_PLATE:
                outputLevel = entities.isEmpty() ? 0 : 15;
                break;
            case GOLD_PLATE:
                outputLevel = Math.min(entities.size(), 15);
                break;
            case IRON_PLATE:
                outputLevel = Math.min((entities.size() + 9) / 10, 15);
                break;
            default:
                // TODO: log a warning
                return;
        }
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getRelative(BlockFace.DOWN).getType().isSolid()
                && super.canPlaceAt(player, block, against);
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
