package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowHorse;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

class HorseStore extends AgeableStore<GlowHorse> {

    public static final String ITEMS_KEY = "Items";
    private static final String EATING_HAYSTACK_KEY = "EatingHaystack";
    private static final String BRED_KEY = "Bred";
    private static final String CHESTED_HORSE_KEY = "ChestedHorse";
    private static final String HAS_REPRODUCED_KEY = "HasReproduced";
    private static final String TYPE_KEY = "Type";
    private static final String VARIANT_KEY = "Variant";
    private static final String TEMPER_Key = "Temper";
    private static final String TAME_KEY = "Tame";
    private static final String OWNER_UUID_KEY = "OwnerUUID";
    private static final String ARMOR_ITEM_KEY = "ArmorItem";
    private static final String SADDLE_ITEM_KEY = "SaddleItem";
    private static final String SADDLE_KEY = "Saddle";

    public HorseStore() {
        super(GlowHorse.class, "EntityHorse");
    }

    @Override
    public GlowHorse createEntity(Location location, CompoundTag compound) {
        return new GlowHorse(location);
    }

    public void load(GlowHorse entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setEatingHay(compound.getBool(EATING_HAYSTACK_KEY));
        entity.setCarryingChest(compound.getBool(CHESTED_HORSE_KEY));
        entity.setHasReproduced(compound.getBool(HAS_REPRODUCED_KEY));
        entity.setStyle(Style.values()[compound.getInt(VARIANT_KEY) >>> 8]);
        entity.setColor(Color.values()[compound.getInt(VARIANT_KEY) & 0xFF]);
        entity.setVariant(Variant.values()[compound.getInt(TYPE_KEY)]);
        entity.setTemper(compound.getInt(TEMPER_Key));
        entity.setTamed(compound.getBool(TAME_KEY));
        if (compound.containsKey(OWNER_UUID_KEY)) {
            String uuidKey = compound.getString(OWNER_UUID_KEY);
            if (uuidKey.isEmpty()) {
                entity.setOwnerUUID(null);
            } else {
                entity.setOwnerUUID(UUID.fromString(uuidKey));
            }
        }
        if (compound.containsKey(ARMOR_ITEM_KEY)) {
            entity.getInventory().setArmor(NbtSerialization.readItem(compound.getCompound(ARMOR_ITEM_KEY)));
        }
        if (compound.containsKey(SADDLE_ITEM_KEY)) {
            entity.getInventory().setSaddle(NbtSerialization.readItem(compound.getCompound(SADDLE_ITEM_KEY)));
        } else if (compound.containsKey(SADDLE_KEY)) {
            if (compound.getBool(SADDLE_KEY)) {
                entity.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            }
        }
        if (entity.isCarryingChest()) {
//            NbtSerialization.readInventory(compound.getCompoundList(ITEMS_KEY), 0, 10); TODO actually implement this properly
        }

    }

    public void save(GlowHorse entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool(EATING_HAYSTACK_KEY, entity.isEatingHay());
        tag.putBool(CHESTED_HORSE_KEY, entity.isCarryingChest());
        tag.putBool(HAS_REPRODUCED_KEY, entity.hasReproduced());
        tag.putBool(BRED_KEY, true);
        tag.putInt(VARIANT_KEY, entity.getStyle().ordinal() << 8 | entity.getColor().ordinal() & 0xFF);
        tag.putInt(TYPE_KEY, entity.getVariant().ordinal());
        tag.putBool(SADDLE_KEY, entity.getInventory().getSaddle() != null);
        tag.putInt(TEMPER_Key, entity.getTemper());
        tag.putBool(TAME_KEY, entity.isTamed());
        if (entity.getInventory().getArmor() != null) {
            tag.putCompound(ARMOR_ITEM_KEY, NbtSerialization.writeItem(entity.getInventory().getArmor(), -1));
        }
        if (entity.getInventory().getSaddle() != null) {
            tag.putCompound(SADDLE_ITEM_KEY, NbtSerialization.writeItem(entity.getInventory().getSaddle(), -1));
        }
        if (entity.isCarryingChest()) {
            tag.putList(ITEMS_KEY, TagType.COMPOUND,
                    NbtSerialization.writeInventory(entity.getInventory().getContents(), entity.getInventory().getContents().length));
        }
        if (entity.getOwnerUUID() == null) {
            tag.putString(OWNER_UUID_KEY, "");
        } else {
            tag.putString(OWNER_UUID_KEY, entity.getOwnerUUID().toString());
        }
    }
}
