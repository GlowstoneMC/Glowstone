package net.glowstone.entity.passive;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.WoolUtil;
import org.bukkit.DyeColor;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class GlowSheep extends GlowAnimal implements Sheep {

    private static final Set<Material> BREEDING_FOODS = Sets.immutableEnumSet(Material.WHEAT);

    @Getter
    private boolean sheared;
    @Getter
    private DyeColor color;

    /**
     * Creates a sheep with a random color.
     *
     * @param location the location
     */
    public GlowSheep(Location location) {
        super(location, EntityType.SHEEP, 8);
        setSize(0.9F, 1.3F);

        // represents the percentage of the wool becoming a certain color
        int colorPc = ThreadLocalRandom.current().nextInt(10000);
        if (colorPc < 8184) {
            setColor(DyeColor.WHITE);
        } else if (colorPc < 8684) {
            setColor(DyeColor.BLACK);
        } else if (colorPc < 9184) {
            setColor(DyeColor.LIGHT_GRAY);
        } else if (colorPc < 9684) {
            setColor(DyeColor.GRAY);
        } else if (colorPc < 9984) {
            setColor(DyeColor.BROWN);
        } else {
            setColor(DyeColor.PINK);
        }
        setSheared(false);
        // todo implement the regrow of wool
    }

    @Override
    public void setSheared(boolean sheared) {
        this.sheared = sheared;
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
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

            // if the sheep is not adult or the player is spectator abort
            if (!isAdult() || player.getGameMode().equals(GameMode.SPECTATOR)) {
                return false;
            }

            final ItemStack handItem = InventoryUtil
                    .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

            if (InventoryUtil.isEmpty(handItem)) {
                return false;
            }

            switch (handItem.getType()) {
                case SHEARS:
                    // If already sheared return
                    if (isSheared()) {
                        return false;
                    }

                    //noinspection deprecation
                    PlayerShearEntityEvent shearEvent = new PlayerShearEntityEvent(player, this);

                    shearEvent = EventFactory.getInstance().callEvent(shearEvent);
                    if (shearEvent.isCancelled()) {
                        return false;
                    }

                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        if (handItem.getDurability() < 238) {
                            handItem.setDurability((short) (handItem.getDurability() + 1));
                            player.getInventory().setItem(message.getHandSlot(), handItem);
                        } else {
                            player.getInventory()
                                    .setItem(message.getHandSlot(), InventoryUtil.createEmptyStack());
                        }
                    }

                    final GlowWorld world = getWorld();
                    world.playSound(getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1, 1);

                    final Material mat = WoolUtil.getWoolMaterialByDye(getColor());

                    world.dropItemNaturally(getLocation(),
                            new ItemStack(mat, ThreadLocalRandom.current().nextInt(3) + 1));

                    setSheared(true);
                    return true;

                case INK_SAC: {
                    final Dye dye = (Dye) handItem.getData();

                    DyeColor color = dye.getColor();


                    SheepDyeWoolEvent dyeEvent = new SheepDyeWoolEvent(this, color);
                    dyeEvent = EventFactory.getInstance().callEvent(dyeEvent);
                    if (dyeEvent.isCancelled()) {
                        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte(), true);
                        player.updateInventory();
                        return false;
                    }

                    color = dyeEvent.getColor();

                    // If same color, we're done
                    if (color.equals(getColor())) {
                        return false;
                    }

                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                        player.getInventory().consumeItemInHand(message.getHandSlot());
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
    public void pulse() {
        super.pulse();

        // wool regrowth
        final int rand = ThreadLocalRandom.current().nextInt(1000);
        if ((isAdult() && rand == 1) || (!isAdult() && rand < 20)) {

            // Check that the block underneath the sheep is grass
            final Block block = getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType() != Material.GRASS_BLOCK) {
                return;
            }

            SheepRegrowWoolEvent event = new SheepRegrowWoolEvent(this);
            event = EventFactory.getInstance().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            // Make the entity animate
            playEffect(EntityEffect.SHEEP_EAT);

            // After 40 ticks (the animation duration) make the wool regrow
            getServer().getScheduler().runTaskLater(null, () -> {

                // Replace the grass with dirt
                block.setType(Material.DIRT);

                // Make the wool regrow
                if (isSheared()) {
                    setSheared(false);
                }

            }, 40);

        }
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

    @Override
    public Set<Material> getBreedingFoods() {
        return BREEDING_FOODS;
    }
}
