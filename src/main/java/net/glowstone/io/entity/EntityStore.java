package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.*;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.LinkedList;
import java.util.List;
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
        if (compound.is("id", StringTag.class)) {
            String checkId = compound.get("id", StringTag.class);
            if (!id.equalsIgnoreCase(checkId)) {
                throw new IllegalArgumentException("Invalid ID loading entity, expected " + id + " got " + checkId);
            }
        }

        // determine world
        World world = null;
        if (compound.is("WorldUUIDLeast", LongTag.class) && compound.is("WorldUUIDMost", LongTag.class)) {
            long uuidLeast = compound.get("WorldUUIDLeast", LongTag.class);
            long uuidMost = compound.get("WorldUUIDMost", LongTag.class);
            world = entity.getServer().getWorld(new UUID(uuidMost, uuidLeast));
        }
        if (world == null && compound.is("World", StringTag.class)) {
            world = entity.getServer().getWorld(compound.get("World", StringTag.class));
        }
        if (world == null && compound.is("Dimension", IntTag.class)) {
            int dim = compound.get("Dimension", IntTag.class);
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
        if (compound.is("Pos", ListTag.class) && compound.is("Rotation", ListTag.class)) {
            List<DoubleTag> posTag = compound.getList("Pos", DoubleTag.class);
            List<FloatTag> rotTag = compound.getList("Rotation", FloatTag.class);
            entity.setRawLocation(NbtSerialization.listTagsToLocation(world, posTag, rotTag));
        } else {
            entity.setRawLocation(world.getSpawnLocation());
        }
        if (compound.is("Motion", ListTag.class)) {
            // entity.setVelocity(NbtFormattingUtils.listTagToVector((ListTag<DoubleTag>) compound.getValue().get("Motion")));
        }
        if (compound.is("Air", ShortTag.class)) {
            // entity.setRemainingAir(((ShortTag) compound.getValue().get("Air")).getValue());
        }
        if (compound.is("Fire", ShortTag.class)) {
            // entity.setFireTicks(((ShortTag) compound.getValue().get("Fire")).getValue());
        }
        if (compound.is("OnGround", ByteTag.class)) {
            entity.setOnGround(compound.get("OnGround", ByteTag.class) == 1);
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

    public List<Tag> save(T entity) {
        List<Tag> result = new LinkedList<Tag>();
        result.add(new StringTag("id", id));
        Location loc = entity.getLocation();
        UUID worldUUID = loc.getWorld().getUID();
        result.add(new LongTag("WorldUUIDLeast", worldUUID.getLeastSignificantBits()));
        result.add(new LongTag("WorldUUIDMost", worldUUID.getMostSignificantBits()));
        result.add(new StringTag("World", loc.getWorld().getName()));
        result.add(new IntTag("Dimension", loc.getWorld().getEnvironment().getId()));
        result.addAll(NbtSerialization.locationToListTags(loc));
        // result.put("UUIDLeast", new LongTag("UUIDLeast", entity.getUniqueId().getLeastSignificantBits()));
        // result.put("UUIDMost", new LongTag("UUIDMost", entity.getUniqueId().getMostSignificantBits()));
        // result.put("HurtTime", new ShortTag("HurtTime", (short) 0)); // NYI
        // result.put("Air", new ShortTag("Air", (short) entity.getRemainingAir()));
        // result.put("Fire", new ShortTag("Fire", (short) entity.getFireTicks()));
        result.add(NbtSerialization.vectorToListTag(entity.getVelocity()));
        result.add(new ByteTag("OnGround", (byte) (entity.isOnGround() ? 1 : 0)));
        return result;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return clazz;
    }
}
