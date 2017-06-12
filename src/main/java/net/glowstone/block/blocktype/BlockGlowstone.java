package net.glowstone.block.blocktype;

import net.glowstone.advancement.GlowAdvancement;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class BlockGlowstone extends BlockType {

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        super.afterPlace(player, block, holding, oldState);
        GlowAdvancement advancement = (GlowAdvancement) Bukkit.getAdvancement(NamespacedKey.minecraft("story/test"));
        if (player.getAdvancementTracker().getProgress(advancement).awardCriteria("minecraft:test/criterion")) {
            player.sendMessage("Congrats, Glowstone!");
        } else {
            player.sendMessage("You already got awarded :(");
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return getMinedDrops(block);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return new BlockRandomDrops(Material.GLOWSTONE_DUST, 2, 4).getMinedDrops(block);
    }
}
