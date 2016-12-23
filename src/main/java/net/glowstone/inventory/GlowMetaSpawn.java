package net.glowstone.inventory;

import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Map;

public class GlowMetaSpawn extends GlowMetaItem implements SpawnEggMeta {
    private EntityType type;

    public GlowMetaSpawn(GlowMetaItem meta) {
        super(meta);

        if (!(meta instanceof GlowMetaSpawn))
            return;

        GlowMetaSpawn spawn = (GlowMetaSpawn) meta;
        if (spawn.hasSpawnedType()) {
            this.type = spawn.getSpawnedType();
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "MONSTER_EGG");
        if (hasSpawnedType()) {
            result.put("entity-id", "minecraft:" + getSpawnedType().getName());
        }
        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if (hasSpawnedType()) {
            CompoundTag entity = new CompoundTag();
            entity.putString("id", "minecraft:" + getSpawnedType().getName());
            tag.putCompound("EntityTag", entity);
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isCompound("EntityTag")) {
            CompoundTag entity = tag.getCompound("EntityTag");
            if (entity.isString("id")) {
                String id = entity.getString("id");
                if (id.startsWith("minecraft:")) {
                    id = id.substring("minecraft:".length());
                }
                type = EntityType.fromName(id);
            }
        }
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.MONSTER_EGG;
    }

    @Override
    public boolean hasSpawnedType() {
        return this.type != null;
    }

    @Override
    public EntityType getSpawnedType() {
        return this.type;
    }

    @Override
    public void setSpawnedType(EntityType type) {
        this.type = type;
    }

    @Override
    public GlowMetaSpawn clone() {
        return new GlowMetaSpawn(this);
    }
}
