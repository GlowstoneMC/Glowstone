package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import java.util.List;
import java.util.Random;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.inventory.GlowHorseInventory;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

public class GlowHorse extends GlowAbstractHorse implements Horse {

    private Variant variant = Variant.values()[new Random().nextInt(2)];
    private Color horseColor = Color.values()[new Random().nextInt(6)];
    private Style horseStyle = Style.values()[new Random().nextInt(3)];
    private boolean eatingHay;
    private boolean hasReproduced;
    private int temper;
    private HorseInventory inventory = new GlowHorseInventory(this);

    public GlowHorse(Location location) {
        super(location, EntityType.HORSE, 15);
        setSize(1.4F, 1.6F);
    }

    @Override
    public Variant getVariant() {
        return variant;
    }

    @Override
    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    @Override
    public Color getColor() {
        return horseColor;
    }

    @Override
    public void setColor(Color color) {
        horseColor = color;
        metadata.set(MetadataIndex.HORSE_STYLE, getHorseStyleData());
    }

    @Override
    public Style getStyle() {
        return horseStyle;
    }

    @Override
    public void setStyle(Style style) {
        horseStyle = style;
        metadata.set(MetadataIndex.HORSE_STYLE, getHorseStyleData());
    }

    @Override
    public boolean isCarryingChest() {
        // Field has been removed in 1.11
        return false;
    }

    @Override
    public void setCarryingChest(boolean b) {
        // Field has been removed in 1.11
    }

    @Override
    public HorseInventory getInventory() {
        return inventory;
    }

    public void setInventory(HorseInventory inventory) {
        this.inventory = inventory;
    }

    public boolean isEatingHay() {
        return eatingHay;
    }

    public void setEatingHay(boolean eatingHay) {
        this.eatingHay = eatingHay;
    }

    public boolean hasReproduced() {
        return hasReproduced;
    }

    public void setHasReproduced(boolean hasReproduced) {
        this.hasReproduced = hasReproduced;
    }

    public int getTemper() {
        return temper;
    }

    public void setTemper(int temper) {
        this.temper = temper;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowHorse.class);
        map.set(MetadataIndex.HORSE_STYLE, getHorseStyleData());
        map.set(MetadataIndex.HORSE_ARMOR, getHorseArmorData());
        messages.add(new EntityMetadataMessage(entityId, map.getEntryList()));
        return messages;
    }

    private int getHorseStyleData() {
        return horseColor.ordinal() & 0xFF | horseStyle.ordinal() << 8;
    }

    private int getHorseArmorData() {
        if (getInventory().getArmor() != null) {
            if (getInventory().getArmor().getType() == Material.DIAMOND_BARDING) {
                return 3;
            } else if (getInventory().getArmor().getType() == Material.GOLD_BARDING) {
                return 2;
            } else if (getInventory().getArmor().getType() == Material.IRON_BARDING) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_HORSE_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_HORSE_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_HORSE_AMBIENT;
    }

    @Override
    public Ageable createBaby() {
        GlowHorse baby = (GlowHorse) super.createBaby();
        baby.setColor(getColor());
        baby.setStyle(getStyle());
        return baby;
    }
}
