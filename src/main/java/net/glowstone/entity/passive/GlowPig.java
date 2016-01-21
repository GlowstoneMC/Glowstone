package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlowPig extends GlowAnimal implements Pig {

    private boolean hasSaddle;
    private boolean saddleChanged;

    public GlowPig(Location location) {
        super(location, EntityType.PIG);
        setSize(0.9F, 0.9F);
    }

    @Override
    public boolean hasSaddle() {
        return hasSaddle;
    }

    @Override
    public void setSaddle(boolean hasSaddle) {
        this.saddleChanged = true; // todo
        this.hasSaddle = hasSaddle;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowPig.class);
        map.set(MetadataIndex.PIG_SADDLE, (byte) (this.hasSaddle ? 1 : 0));
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

    @Override
    public List<Message> createUpdateMessage() {
        List<Message> messages = super.createUpdateMessage();
        if (saddleChanged) {
            MetadataMap map = new MetadataMap(GlowPig.class);
            map.set(MetadataIndex.PIG_SADDLE, (byte) (this.hasSaddle ? 1 : 0));
            messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        }
        return messages;
    }

    @Override
    public void reset() {
        super.reset();
        saddleChanged = false;
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (!hasSaddle()) {
            ItemStack hand = player.getItemInHand();
            if (hand.getType() == Material.SADDLE) {
                setSaddle(true);
                if (hand.getAmount() > 1) {
                    hand.setAmount(hand.getAmount() - 1);
                    player.setItemInHand(hand);
                } else {
                    player.setItemInHand(null);
                }
                return true;
            }
            return false;
        }

        if (isEmpty()) {
            return this.setPassenger(player);
        }

        return false;
    }
}
