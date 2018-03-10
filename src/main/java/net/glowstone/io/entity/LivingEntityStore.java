package net.glowstone.io.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.glowstone.entity.AttributeManager;
import net.glowstone.entity.AttributeManager.Modifier;
import net.glowstone.entity.AttributeManager.Property;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.objects.GlowLeashHitch;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class LivingEntityStore<T extends GlowLivingEntity> extends EntityStore<T> {

    public LivingEntityStore(Class<T> clazz, String type) {
        super(clazz, type);
    }

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
            loadEquipment(entity, equip, compound);
        }
        if (compound.isByte("CanPickUpLoot")) {
            entity.setCanPickupItems(compound.getBool("CanPickUpLoot"));
        }

        if (compound.isList("Attributes", TagType.COMPOUND)) {
            List<CompoundTag> attributes = compound.getCompoundList("Attributes");
            AttributeManager am = entity.getAttributeManager();

            for (CompoundTag tag : attributes) {
                if (!tag.isString("Name") || !tag.isDouble("Base")) {
                    continue;
                }
                List<Modifier> modifiers = null;
                if (tag.isList("Modifiers", TagType.COMPOUND)) {
                    modifiers = new ArrayList<>();

                    List<CompoundTag> modifierTags = tag.getCompoundList("Modifiers");
                    for (CompoundTag modifierTag : modifierTags) {
                        if (modifierTag.isDouble("Amount")
                                && modifierTag.isString("Name")
                                && modifierTag.isInt("Operation")
                                && modifierTag.isLong("UUIDLeast")
                                && modifierTag.isLong("UUIDMost")) {
                            modifiers.add(new Modifier(
                                    modifierTag.getString("Name"),
                                    new UUID(modifierTag.getLong("UUIDLeast"),
                                            modifierTag.getLong("UUIDMost")),
                                    modifierTag.getDouble("Amount"),
                                    (byte) modifierTag.getInt("Operation")));
                        }
                    }
                }

                am.setProperty(tag.getString("Name"), tag.getDouble("Base"), modifiers);
            }
        }

        if (compound.isByte("Leashed") && compound.getBool("Leashed") && !compound
                .isCompound("Leash")) {
            // We know that there was something leashed, but not what entity it was
            // This can happen, when for example Minecart got leashed
            // We still have to make sure that we drop a Leash Item
            entity.setLeashHolderUniqueId(UUID.randomUUID());
        } else if (compound.isCompound("Leash")) {
            CompoundTag leash = compound.getCompound("Leash");
            if (leash.isLong("UUIDMost") && leash.isLong("UUIDLeast")) {
                UUID uuid = new UUID(leash.getLong("UUIDMost"), leash.getLong("UUIDLeast"));
                entity.setLeashHolderUniqueId(uuid);
            } else if (leash.isInt("X") && leash.isInt("Y") && leash.isInt("Z")) {
                int x = leash.getInt("X");
                int y = leash.getInt("Y");
                int z = leash.getInt("Z");

                LeashHitch leashHitch = GlowLeashHitch
                        .getLeashHitchAt(new Location(entity.getWorld(), x, y, z).getBlock());
                entity.setLeashHolder(leashHitch);
            }
        }
    }

    private void loadEquipment(T entity, EntityEquipment equip, CompoundTag compound) {
        // Deprecated since 15w31a, left here for compatibilty for now
        if (compound.isList("Equipment", TagType.COMPOUND)) {
            List<CompoundTag> list = compound.getCompoundList("Equipment");

            equip.setItemInMainHand(getItem(list, 0));
            equip.setBoots(getItem(list, 1));
            equip.setLeggings(getItem(list, 2));
            equip.setChestplate(getItem(list, 3));
            equip.setHelmet(getItem(list, 4));
        }
        // Deprecated since 15w31a, left here for compatibilty for now
        if (compound.isList("DropChances", TagType.FLOAT)) {
            List<Float> list = compound.getList("DropChances", TagType.FLOAT);

            equip.setItemInMainHandDropChance(getOrDefault(list, 0, 1f));
            equip.setBootsDropChance(getOrDefault(list, 1, 1f));
            equip.setLeggingsDropChance(getOrDefault(list, 2, 1f));
            equip.setChestplateDropChance(getOrDefault(list, 3, 1f));
            equip.setHelmetDropChance(getOrDefault(list, 4, 1f));
        }

        if (compound.isList("HandItems", TagType.COMPOUND)) {
            List<CompoundTag> list = compound.getCompoundList("HandItems");

            equip.setItemInMainHand(getItem(list, 0));
            equip.setItemInOffHand(getItem(list, 1));
        }
        if (compound.isList("ArmorItems", TagType.COMPOUND)) {
            List<CompoundTag> list = compound.getCompoundList("ArmorItems");

            equip.setBoots(getItem(list, 0));
            equip.setLeggings(getItem(list, 1));
            equip.setChestplate(getItem(list, 2));
            equip.setHelmet(getItem(list, 3));
        }

        // set of dropchances on a player throws an UnsupportedOperationException
        if (!(entity instanceof Player)) {
            if (compound.isList("HandDropChances", TagType.FLOAT)) {
                List<Float> list = compound.getList("HandDropChances", TagType.FLOAT);

                equip.setItemInMainHandDropChance(getOrDefault(list, 0, 1f));
                equip.setItemInOffHandDropChance(getOrDefault(list, 1, 1f));
            }
            if (compound.isList("ArmorDropChances", TagType.FLOAT)) {
                List<Float> list = compound.getList("ArmorDropChances", TagType.FLOAT);

                equip.setBootsDropChance(getOrDefault(list, 0, 1f));
                equip.setLeggingsDropChance(getOrDefault(list, 1, 1f));
                equip.setChestplateDropChance(getOrDefault(list, 2, 1f));
                equip.setHelmetDropChance(getOrDefault(list, 3, 1f));
            }
        }
    }

    private ItemStack getItem(List<CompoundTag> list, int index) {
        if (list == null) {
            return InventoryUtil.createEmptyStack();
        }

        if (index >= list.size()) {
            return InventoryUtil.createEmptyStack();
        }

        return NbtSerialization.readItem(list.get(index));
    }

    private float getOrDefault(List<Float> list, int index, float defaultValue) {
        if (list == null) {
            return defaultValue;
        }

        if (index >= list.size()) {
            return defaultValue;
        }

        return list.get(index);
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
                        modifierTag
                                .putLong("UUIDLeast", modifier.getUuid().getLeastSignificantBits());
                        modifierTag
                                .putLong("UUIDMost", modifier.getUuid().getMostSignificantBits());
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
            tag.putCompoundList("HandItems", Arrays.asList(
                    NbtSerialization.writeItem(equip.getItemInMainHand(), -1),
                    NbtSerialization.writeItem(equip.getItemInOffHand(), -1)
            ));
            tag.putCompoundList("ArmorItems", Arrays.asList(
                    NbtSerialization.writeItem(equip.getBoots(), -1),
                    NbtSerialization.writeItem(equip.getLeggings(), -1),
                    NbtSerialization.writeItem(equip.getChestplate(), -1),
                    NbtSerialization.writeItem(equip.getHelmet(), -1)
            ));

            tag.putFloatList("HandDropChances", Arrays.asList(
                    equip.getItemInMainHandDropChance(),
                    equip.getItemInOffHandDropChance()
            ));
            tag.putFloatList("ArmorDropChances", Arrays.asList(
                    equip.getBootsDropChance(),
                    equip.getLeggingsDropChance(),
                    equip.getChestplateDropChance(),
                    equip.getHelmetDropChance()
            ));
        }
        tag.putBool("CanPickUpLoot", entity.getCanPickupItems());

        tag.putBool("Leashed", entity.isLeashed());

        if (entity.isLeashed()) {
            Entity leashHolder = entity.getLeashHolder();
            CompoundTag leash = new CompoundTag();

            // "Non-living entities excluding leashes will not persist as leash holders."
            // The empty Leash tag is still persisted tough
            if (leashHolder instanceof LeashHitch) {
                Location location = leashHolder.getLocation();

                leash.putInt("X", location.getBlockX());
                leash.putInt("Y", location.getBlockY());
                leash.putInt("Z", location.getBlockZ());
            } else if (leashHolder instanceof LivingEntity) {
                leash.putLong("UUIDMost", entity.getUniqueId().getMostSignificantBits());
                leash.putLong("UUIDLeast", entity.getUniqueId().getLeastSignificantBits());
            }
            tag.putCompound("Leash", leash);
        }
    }
}
