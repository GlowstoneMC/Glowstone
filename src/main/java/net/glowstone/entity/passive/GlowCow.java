package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class GlowCow extends GlowAnimal implements Cow {

    public GlowCow(Location location) {
        super(location, EntityType.COW, 10);
        setSize(0.9F, 1.3F);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return false;
        if (!player.getItemInHand().getType().equals(Material.BUCKET)) return false;

        if (player.getItemInHand().getAmount() > 1) {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        } else {
            player.getItemInHand().setType(Material.AIR);
        }

        if (player.getInventory().firstEmpty() == -1) {
            Location playerheadloc = player.getLocation().clone().add(0, 1, 0);
            GlowItem item = player.getWorld().dropItem(playerheadloc, new ItemStack(Material.MILK_BUCKET, 1));
            item.setVelocity(playerheadloc.toVector().clone().subtract(this.getLocation().toVector().clone()));
        } else {
            player.getInventory().addItem(new ItemStack(Material.MILK_BUCKET, 1));
        }

        return true;
    }
}
