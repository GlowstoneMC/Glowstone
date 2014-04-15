package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public abstract class EntityStore<T extends GlowEntity> {
    private final String id;
    private final Class<T> clazz;

    public EntityStore(Class<T> clazz, String id) {
        this.id = id;
        this.clazz = clazz;
    }

    public abstract T load(GlowServer server, GlowWorld world, CompoundTag compound);

    public void load(T entity, CompoundTag compound) {
        if (compound.isString("id")) {
            String checkId = compound.getString("id");
            if (!id.equalsIgnoreCase(checkId)) {
                throw new IllegalArgumentException("Invalid ID loading entity, expected " + id + " got " + checkId);
            }
        }

        // determine world
        World world = null;
        if (compound.isLong("WorldUUIDLeast") && compound.isLong("WorldUUIDMost")) {
            long uuidLeast = compound.getLong("WorldUUIDLeast");
            long uuidMost = compound.getLong("WorldUUIDMost");
            world = entity.getServer().getWorld(new UUID(uuidMost, uuidLeast));
        }
        if (world == null && compound.isString("World")) {
            world = entity.getServer().getWorld(compound.getString("World"));
        }
        if (world == null && compound.isInt("Dimension")) {
            int dim = compound.getInt("Dimension");
            for (World sWorld : entity.getServer().getWorlds()) {
                if (sWorld.getEnvironment().getId() == dim) {
                    world = sWorld;
                    break;
                }
            }
        }
        if (world == null) {
            world = entity.getWorld();
        }

        // determine location
        if (compound.isList("Pos", TagType.DOUBLE) && compound.isList("Rotation", TagType.FLOAT)) {
            entity.setRawLocation(NbtSerialization.listTagsToLocation(world, compound));
        } else {
            entity.setRawLocation(world.getSpawnLocation());
        }
        /*if (compound.isList("Motion", ListTag.class)) {
            // entity.setVelocity(NbtFormattingUtils.listTagToVector((ListTag<DoubleTag>) compound.getValue().get("Motion")));
        }
        if (compound.is("Air", ShortTag.class)) {
            // entity.setRemainingAir(((ShortTag) compound.getValue().get("Air")).getValue());
        }
        if (compound.is("Fire", ShortTag.class)) {
            // entity.setFireTicks(((ShortTag) compound.getValue().get("Fire")).getValue());
        }*/
        if (compound.isByte("OnGround")) {
            entity.setOnGround(compound.getByte("OnGround") != 0);
        }

        /* if (playerData.containsKey("HurtTime")) {
            ShortTag hurtTimeTag = (ShortTag) playerData.get("HurtTime");
            ret.put(PlayerData.HURT_TICKS, hurtTimeTag.getValue());
        }
        if (playerData.containsKey("AttackTime")) {
            ShortTag attackTimeTag = (ShortTag) playerData.get("AttackTime");
            ret.put(PlayerData.ATTACK_TICKS, attackTimeTag.getValue());
        }
        if (playerData.containsKey("DeathTime")) {
            ShortTag deathTimeTag = (ShortTag) playerData.get("DeathTime");
            ret.put(PlayerData.DEATH_TICKS, deathTimeTag.getValue());
        } */
    }

    public void save(T entity, CompoundTag tag) {
        tag.putString("id", id);

        Location loc = entity.getLocation();
        UUID worldUUID = loc.getWorld().getUID();
        tag.putLong("WorldUUIDLeast", worldUUID.getLeastSignificantBits());
        tag.putLong("WorldUUIDMost", worldUUID.getMostSignificantBits());
        tag.putString("World", loc.getWorld().getName());
        tag.putInt("Dimension", loc.getWorld().getEnvironment().getId());
        NbtSerialization.locationToListTags(loc, tag);
        // result.put("UUIDLeast", new LongTag("UUIDLeast", entity.getUniqueId().getLeastSignificantBits()));
        // result.put("UUIDMost", new LongTag("UUIDMost", entity.getUniqueId().getMostSignificantBits()));
        // result.put("HurtTime", new ShortTag("HurtTime", (short) 0)); // NYI
        // result.put("Air", new ShortTag("Air", (short) entity.getRemainingAir()));
        // result.put("Fire", new ShortTag("Fire", (short) entity.getFireTicks()));
        // "Motion"
        tag.putList("Motion", TagType.DOUBLE, NbtSerialization.vectorToList(entity.getVelocity()));
        tag.putByte("OnGround", entity.isOnGround() ? 1 : 0);
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return clazz;
    }
}
