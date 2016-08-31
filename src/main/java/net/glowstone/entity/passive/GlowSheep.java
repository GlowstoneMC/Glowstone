package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.Random;

public class GlowSheep extends GlowAnimal implements Sheep {

    private boolean sheared;
    private DyeColor color;

    public GlowSheep(Location location) {
        super(location, EntityType.SHEEP, 8);
        setSize(0.9F, 1.3F);
        Random r = new Random();
        int colorpc = r.nextInt(10000);
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
        return (byte) (getColor().getData() | (sheared ? 0x10 : 0x00));
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        super.entityInteract(player, message);

        if (!isAdult()) return false;

        if (player.getGameMode().equals(GameMode.SPECTATOR)) return false;
        if (player.getItemInHand() == null) return false;
        switch (player.getItemInHand().getType()) {
            case SHEARS:
                if (isSheared()) return false;

                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    ItemStack shears = player.getItemInHand();

                    if (shears.getDurability() < 238) {
                        shears.setDurability((short) (shears.getDurability() + 1));
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    }
                }

                getWorld().playSound(getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 1);

                Random r = new Random();

                getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.WOOL, r.nextInt(3) + 1, getColor().getWoolData()));

                setSheared(true);
                return true;
            case INK_SACK: {
                Dye dye = (Dye) player.getItemInHand().getData();
                DyeColor color = dye.getColor();

                SheepDyeWoolEvent event = new SheepDyeWoolEvent(this, color);
                if (event.isCancelled()) return false;

                color = event.getColor();

                if (color.equals(getColor())) {
                    return false;
                }

                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    if (player.getItemInHand().getAmount() > 1) {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                    } else {
                        player.getInventory().clear(player.getInventory().getHeldItemSlot());
                    }
                }

                setColor(color);
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void kill() {
        super.kill();

        Random r = new Random();

        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.MUTTON, r.nextInt(2)));

        if (!sheared) {
            getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.WOOL, r.nextInt(1)));
        }
    }
}
