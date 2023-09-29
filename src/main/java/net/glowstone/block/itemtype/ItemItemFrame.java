package net.glowstone.block.itemtype;

import com.flowpowered.network.Message;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItemFrame;
import org.bukkit.block.BlockFace;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class ItemItemFrame extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        GlowItemFrame entity = new GlowItemFrame(player, target.getRelative(face).getLocation(),
            face);

        if (EventFactory.getInstance()
            .callEvent(new HangingPlaceEvent(entity, player, target, face, hand))
            .isCancelled()) {
            return;
        }

        List<Message> spawnMessage = entity.createSpawnMessage();
        entity.getWorld().getRawPlayers().stream().filter(p -> p.canSeeEntity(entity)).forEach(
            p -> p.getSession().sendAll(spawnMessage.toArray(new Message[spawnMessage.size()])));
    }
}
