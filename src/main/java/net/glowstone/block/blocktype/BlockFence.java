package net.glowstone.block.blocktype;

import com.google.common.collect.ImmutableList;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowLeashHitch;
import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LeashHitch;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.util.Vector;

public class BlockFence extends BlockDirectDrops {

    public BlockFence(Material dropType, MaterialMatcher neededTool) {
        super(dropType, neededTool);
    }

    public BlockFence(Material dropType) {
        super(dropType);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
            Vector clickedLoc) {
        super.blockInteract(player, block, face, clickedLoc);

        if (!player.getLeashedEntities().isEmpty()) {
            LeashHitch leashHitch = GlowLeashHitch.getLeashHitchAt(block);

            ImmutableList.copyOf(player.getLeashedEntities()).stream()
                    .filter(e -> !(EventFactory.getInstance()
                            .callEvent(new PlayerLeashEntityEvent(e, leashHitch, player))
                            .isCancelled()))
                    .forEach(e -> e.setLeashHolder(leashHitch));
            return true;
        }
        return false;
    }
}
