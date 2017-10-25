package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.concurrent.ThreadLocalRandom;

public class GlowSheep extends GlowAnimal implements Sheep {

    private boolean sheared;
    private DyeColor color;

    public GlowSheep(Location location) {
        super(location, EntityType.SHEEP, 8);
        setSize(0.9F, 1.3F);
        int colorpc = ThreadLocalRandom.current().nextInt(10000);
        if (colorpc < 8184) {
            setColor(DyeColor.WHITE);
        } else if (colorpc >= 8184 && 8684 > colorpc) {
            setColor(DyeColor.BLACK);
        } else if (colorpc >= 8684 && 9184 > colorpc) {
            setColor(DyeColor.SILVER);
        } else if (colorpc >= 9184 && 9684 > colorpc) {
            setColor(DyeColor.GRAY);
        } else if (colorpc >= 9684 && 9984 > colorpc) {
            setColor(DyeColor.BROWN);
        } else {
            setColor(DyeColor.PINK);
        }
        setSheared(false);
        // todo implement the regrow of wool
    }

    @Override
    public boolean isSheared() {
        return sheared;
    }

    @Override
    public void setSheared(boolean sheared) {
        this.sheared = sheared;
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor dyeColor) {
        color = dyeColor;
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
    }

    private byte getColorByte() {
        return (byte) (getColor().getWoolData() | (sheared ? 0x10 : 0x00));
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);
        if (message.getAction() == InteractEntityMessage.Action.INTERACT.ordinal()) {

            if (!isAdult()) return false;
            ItemStack hand = InventoryUtil.itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

            if (player.getGameMode().equals(GameMode.SPECTATOR)) return false;
            if (InventoryUtil.isEmpty(hand)) return false;
            switch (hand.getType()) {
                case SHEARS:
                    if (isSheared()) return false;

                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        if (hand.getDurability() < 238) {
                            hand.setDurability((short) (hand.getDurability() + 1));
                            player.getInventory().setItem(message.getHandSlot(), hand);
                        } else {
                            player.getInventory().setItem(message.getHandSlot(), InventoryUtil.createEmptyStack());
                        }
                    }

                    getWorld().playSound(getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 1);

                    getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.WOOL, ThreadLocalRandom.current().nextInt(3) + 1, getColor().getWoolData()));

                    setSheared(true);
                    return true;
                case INK_SACK: {
                    Dye dye = (Dye) hand.getData();
                    DyeColor color = dye.getColor();

                    SheepDyeWoolEvent event = new SheepDyeWoolEvent(this, color);
                    if (event.isCancelled()) return false;

                    color = event.getColor();

                    if (color.equals(getColor())) {
                        return false;
                    }

                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        if (hand.getAmount() > 1) {
                            hand.setAmount(hand.getAmount() - 1);
                            player.getInventory().setItem(message.getHandSlot(), hand);
                        } else {
                            player.getInventory().setItem(message.getHandSlot(), InventoryUtil.createEmptyStack());
                        }
                    }

                    setColor(color);
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_SHEEP_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_SHEEP_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_SHEEP_AMBIENT;
    }
}
