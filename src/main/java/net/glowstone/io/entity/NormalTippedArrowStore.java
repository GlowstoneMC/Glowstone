package net.glowstone.io.entity;

import java.util.stream.Collectors;
import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.inventory.GlowMetaPotion;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TippedArrow;
import org.bukkit.potion.PotionData;

public class NormalTippedArrowStore extends ArrowStore<GlowArrow> {
    private static final String COLOR = "Color";
    private static final String POTION = "Potion";
    private static final String CUSTOM_POTION_EFFECTS = "CustomPotionEffects";

    public NormalTippedArrowStore() {
        super(GlowArrow.class, "arrow", GlowArrow::new);
    }

    @Override
    public void save(GlowArrow entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity instanceof TippedArrow) {
            PotionData potion = ((TippedArrow) entity).getBasePotionData();
            if (potion != null) {
                tag.putString(POTION, GlowMetaPotion.dataToString(potion));
            }
            tag.putCompoundList(CUSTOM_POTION_EFFECTS,
                    ((TippedArrow) entity).getCustomEffects()
                    .stream()
                    .map(GlowMetaPotion::toNbt)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void load(GlowArrow entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof TippedArrow) {
            TippedArrow tippedArrow = (TippedArrow) entity;
            handleIntIfPresent(tag, COLOR, rgb -> tippedArrow.setColor(Color.fromRGB(rgb)));
            // TODO: POTION
            tag.consumeCompoundList(list -> list
                    .stream()
                    .map(GlowMetaPotion::fromNbt)
                    .forEach(effect -> tippedArrow.addCustomEffect(effect, false)),
                    CUSTOM_POTION_EFFECTS);
        }
    }

    @Override
    public GlowArrow createEntity(Location location, CompoundTag compound) {
        if (compound.isCompound(POTION) || compound.isCompound(CUSTOM_POTION_EFFECTS)) {
            // arrows with these fields are tipped
            return new GlowTippedArrow(location);
        }
        return super.createEntity(location, compound);
    }
}
