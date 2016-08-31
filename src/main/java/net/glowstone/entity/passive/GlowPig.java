package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GlowPig extends GlowAnimal implements Pig {

    public GlowPig(Location location) {
        super(location, EntityType.PIG, 10);
        setSize(0.9F, 0.9F);
    }

    @Override
    public boolean hasSaddle() {
        return metadata.getBoolean(MetadataIndex.PIG_SADDLE);
    }

    @Override
    public void setSaddle(boolean hasSaddle) {
        metadata.set(MetadataIndex.PIG_SADDLE, hasSaddle);
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);

        if (!isAdult()) return false;

        if (!hasSaddle()) {
            ItemStack hand = player.getItemInHand();
            if (hand.getType() == Material.SADDLE) {
                setSaddle(true);
                if (player.getGameMode() != GameMode.CREATIVE) {
                    if (hand.getAmount() > 1) {
                        hand.setAmount(hand.getAmount() - 1);
                    } else {
                        player.setItemInHand(null);
                    }
                }
                return true;
            }
            return false;
        }

        return isEmpty() && setPassenger(player);

    }

    @Override
    public void kill()
    {
        super.kill();

        Random r = new Random();

        int porkDrop = r.nextInt(3);
        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.PORK, porkDrop));
    }

}
