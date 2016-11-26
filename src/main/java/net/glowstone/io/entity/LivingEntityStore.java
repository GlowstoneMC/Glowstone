package net.glowstone.io.entity;

import net.glowstone.entity.AttributeManager;
import net.glowstone.entity.AttributeManager.Modifier;
import net.glowstone.entity.AttributeManager.Property;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.Map.Entry;

abstract class LivingEntityStore<T extends GlowLivingEntity> extends EntityStore<T> {

    public LivingEntityStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
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
        if (compound.isByte("FallFlying")) {
            entity.setFallFlying(compound.getBool("FallFlying"));
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
        if (equip != null) {
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
        }
        if (compound.isByte("CanPickUpLoot")) {
            entity.setCanPickupItems(compound.getBool("CanPickUpLoot"));
        }

        if (compound.isList("Attributes", TagType.COMPOUND)) {
            List<CompoundTag> attributes = compound.getCompoundList("Attributes");
            AttributeManager am = entity.getAttributeManager();

            for (CompoundTag tag : attributes) {
                if (tag.isString("Name") && tag.isDouble("Base")) {
                    List<Modifier> modifiers = null;
                    if (tag.isList("Modifiers", TagType.COMPOUND)) {
                        modifiers = new ArrayList<>();

                        List<CompoundTag> modifierTags = tag.getCompoundList("Modifiers");
                        for (CompoundTag modifierTag : modifierTags) {
                            if (modifierTag.isDouble("Amount") && modifierTag.isString("Name") &&
                                    modifierTag.isInt("Operation") && modifierTag.isLong("UUIDLeast") &&
                                    modifierTag.isLong("UUIDMost")) {
                                modifiers.add(new Modifier(
                                        modifierTag.getString("Name"), new UUID(modifierTag.getLong("UUIDLeast"), modifierTag.getLong("UUIDMost")),
                                        modifierTag.getDouble("Amount"), (byte) modifierTag.getInt("Operation")));
                            }
                        }
                    }

                    am.setProperty(tag.getString("Name"), tag.getDouble("Base"), modifiers);
                }
            }
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
        tag.putBool("FallFlying", entity.isFallFlying());

        AttributeManager am = entity.getAttributeManager();
        Map<String, Property> properties = am.getAllProperties();
        if (!properties.isEmpty()) {
            List<CompoundTag> attributes = new ArrayList<>();

            for (Entry<String, Property> property : properties.entrySet()) {
                CompoundTag attribute = new CompoundTag();
                attribute.putString("Name", property.getKey());

                Property p = property.getValue();
                attribute.putDouble("Base", p.getValue());
                if (p.getModifiers() != null && !p.getModifiers().isEmpty()) {
                    List<CompoundTag> modifiers = new ArrayList<>();
                    for (Modifier modifier : p.getModifiers()) {
                        CompoundTag modifierTag = new CompoundTag();
                        modifierTag.putDouble("Amount", modifier.getAmount());
                        modifierTag.putString("Name", modifier.getName());
                        modifierTag.putInt("Operation", modifier.getOperation());
                        modifierTag.putLong("UUIDLeast", modifier.getUuid().getLeastSignificantBits());
                        modifierTag.putLong("UUIDMost", modifier.getUuid().getMostSignificantBits());
                        modifiers.add(modifierTag);
                    }
                    attribute.putCompoundList("Modifiers", modifiers);
                }

                attributes.add(attribute);
            }

            tag.putCompoundList("Attributes", attributes);
        }

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
        if (equip != null) {
            tag.putCompoundList("Equipment", Arrays.asList(
                    NbtSerialization.writeItem(equip.getItemInHand(), -1),
                    NbtSerialization.writeItem(equip.getBoots(), -1),
                    NbtSerialization.writeItem(equip.getLeggings(), -1),
                    NbtSerialization.writeItem(equip.getChestplate(), -1),
                    NbtSerialization.writeItem(equip.getHelmet(), -1)
            ));
            tag.putList("DropChances", TagType.FLOAT, Arrays.asList(
                    equip.getItemInHandDropChance(),
                    equip.getBootsDropChance(),
                    equip.getLeggingsDropChance(),
                    equip.getChestplateDropChance(),
                    equip.getHelmetDropChance()
            ));
        }
        tag.putBool("CanPickUpLoot", entity.getCanPickupItems());
    }
}
