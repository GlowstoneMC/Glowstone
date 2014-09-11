package net.glowstone.io.entity;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

abstract class LivingEntityStore<T extends GlowLivingEntity> extends EntityStore<T> {

    public LivingEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    // these tags that apply to living entities only are documented as global:
    // - short "Air"
    // - string "CustomName"
    // - bool "CustomNameVisible"

    // todo: the following tags
    // - float "AbsorptionAmount"
    // - short "HurtTime"
    // - int "HurtByTimestamp"
    // - short "DeathTime"
    // - compound "Attributes"
    // - bool "PersistenceRequired"
    // - bool "Leashed"
    // - compound "Leash"
    // on ActiveEffects, bool "ShowParticles"

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);

        if (compound.isShort("Air")) {
            entity.setRemainingAir(compound.getShort("Air"));
        }
        if (compound.isString("CustomName")) {
            entity.setCustomName(compound.getString("CustomName"));
        }
        if (compound.isByte("CustomNameVisible")) {
            entity.setCustomNameVisible(compound.getBool("CustomNameVisible"));
        }

        if (compound.isFloat("HealF")) {
            entity.setHealth(compound.getFloat("HealF"));
        } else if (compound.isShort("Health")) {
            entity.setHealth(compound.getShort("Health"));
        }
        if (compound.isShort("AttackTime")) {
            entity.setNoDamageTicks(compound.getShort("AttackTime"));
        }

        if (compound.isList("ActiveEffects", TagType.COMPOUND)) {
            for (CompoundTag effect : compound.getCompoundList("ActiveEffects")) {
                // should really always have every field, but be forgiving if possible
                if (!effect.isByte("Id") || !effect.isInt("Duration")) {
                    continue;
                }

                PotionEffectType type = PotionEffectType.getById(effect.getByte("Id"));
                int duration = effect.getInt("Duration");
                if (type == null || duration < 0) {
                    continue;
                }
                int amplifier = 0;
                boolean ambient = false;

                if (compound.isByte("Amplifier")) {
                    amplifier = compound.getByte("Amplifier");
                }
                if (compound.isByte("Ambient")) {
                    ambient = compound.getBool("Ambient");
                }
                // bool "ShowParticles"

                entity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient), true);
            }
        }

        EntityEquipment equip = entity.getEquipment();
        if (compound.isList("Equipment", TagType.COMPOUND)) {
            List<CompoundTag> list = compound.getCompoundList("Equipment");
            if (list.size() == 5) {
                equip.setItemInHand(NbtSerialization.readItem(list.get(0)));
                equip.setBoots(NbtSerialization.readItem(list.get(1)));
                equip.setLeggings(NbtSerialization.readItem(list.get(2)));
                equip.setChestplate(NbtSerialization.readItem(list.get(3)));
                equip.setHelmet(NbtSerialization.readItem(list.get(4)));
            }
        }
        if (compound.isList("DropChances", TagType.FLOAT)) {
            List<Float> list = compound.getList("DropChances", TagType.FLOAT);
            if (list.size() == 5) {
                equip.setItemInHandDropChance(list.get(0));
                equip.setBootsDropChance(list.get(1));
                equip.setLeggingsDropChance(list.get(2));
                equip.setChestplateDropChance(list.get(3));
                equip.setHelmetDropChance(list.get(4));
            }
        }
        if (compound.isByte("CanPickUpLoot")) {
            entity.setCanPickupItems(compound.getBool("CanPickUpLoot"));
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putShort("Air", entity.getRemainingAir());
        if (entity.getCustomName() != null && !entity.getCustomName().isEmpty()) {
            tag.putString("CustomName", entity.getCustomName());
            tag.putBool("CustomNameVisible", entity.isCustomNameVisible());
        }

        tag.putFloat("HealF", entity.getHealth());
        tag.putShort("Health", (int) entity.getHealth());
        tag.putShort("AttackTime", entity.getNoDamageTicks());

        List<CompoundTag> effects = new LinkedList<>();
        for (PotionEffect effect : entity.getActivePotionEffects()) {
            CompoundTag effectTag = new CompoundTag();
            effectTag.putByte("Id", effect.getType().getId());
            effectTag.putByte("Amplifier", effect.getAmplifier());
            effectTag.putInt("Duration", effect.getDuration());
            effectTag.putBool("Ambient", effect.isAmbient());
            effectTag.putBool("ShowParticles", true);
            effects.add(effectTag);
        }
        tag.putCompoundList("ActiveEffects", effects);

        EntityEquipment equip = entity.getEquipment();
        tag.putCompoundList("Equipment", Arrays.asList(
                NbtSerialization.writeItem(equip.getItemInHand(), -1),
                NbtSerialization.writeItem(equip.getBoots(), -1),
                NbtSerialization.writeItem(equip.getLeggings(), -1),
                NbtSerialization.writeItem(equip.getChestplate(), -1),
                NbtSerialization.writeItem(equip.getHelmet(), -1)
        ));
        tag.putList("DropChances", TagType.FLOAT, Arrays.<Float>asList(
                equip.getItemInHandDropChance(),
                equip.getBootsDropChance(),
                equip.getLeggingsDropChance(),
                equip.getChestplateDropChance(),
                equip.getHelmetDropChance()
        ));
        tag.putBool("CanPickUpLoot", entity.getCanPickupItems());
    }
}
