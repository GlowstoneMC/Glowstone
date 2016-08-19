package net.glowstone.entity.passive;

import com.flowpowered.network.Message;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.inventory.GlowHorseInventory;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GlowHorse extends GlowTameable implements Horse {

    private Variant variant = Variant.values()[new Random().nextInt(2)];
    private Color horseColor = Color.values()[new Random().nextInt(6)];
    private Style horseStyle = Style.values()[new Random().nextInt(3)];
    private boolean hasChest;
    private int domestication;
    private int maxDomestication;
    private double jumpStrength;
    private boolean eatingHay;
    private boolean hasReproduced;
    private int temper;
    private UUID ownerUUID;
    private HorseInventory inventory = new GlowHorseInventory(this);

    public GlowHorse(Location location) {
        this(location, null);
        setSize(1.4F, 1.6F);
    }

    protected GlowHorse(Location location, AnimalTamer owner) {
        super(location, EntityType.HORSE, 15, owner); // todo change health later
        if (owner != null) ownerUUID = owner.getUniqueId();
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
    }

    @Override
    public Style getStyle() {
        return horseStyle;
    }

    @Override
    public void setStyle(Style style) {
        horseStyle = style;
    }

    @Override
    public boolean isCarryingChest() {
        return hasChest;
    }

    @Override
    public void setCarryingChest(boolean b) {
        if (b) {
            // TODO Manipulate the HorseInventory somehow
        }
        hasChest = b;
    }

    @Override
    public int getDomestication() {
        return domestication;
    }

    @Override
    public void setDomestication(int i) {
        domestication = i;
    }

    @Override
    public int getMaxDomestication() {
        return maxDomestication;
    }

    @Override
    public void setMaxDomestication(int i) {
        maxDomestication = i;
    }

    @Override
    public double getJumpStrength() {
        return jumpStrength;
    }

    @Override
    public void setJumpStrength(double v) {
        jumpStrength = v;
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

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowHorse.class);
        map.set(MetadataIndex.HORSE_TYPE, (byte) getVariant().ordinal());
        map.set(MetadataIndex.HORSE_FLAGS, getHorseFlags());
        map.set(MetadataIndex.HORSE_STYLE, getHorseStyleData());
        map.set(MetadataIndex.HORSE_ARMOR, getHorseArmorData());
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

    private int getHorseFlags() {
        int value = 0;
        if (isTamed()) {
            value |= 0x02;
        }
        if (getInventory() != null && getInventory().getSaddle() != null) {
            value |= 0x04;
        }
        if (hasChest) {
            value |= 0x08;
        }
        if (hasReproduced) {
            value |= 0x10;
        }
        if (isEatingHay()) {
            value |= 0x20;
        }
        return value;
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
}
