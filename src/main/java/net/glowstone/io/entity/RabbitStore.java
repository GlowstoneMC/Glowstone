package net.glowstone.io.entity;

import com.google.common.collect.ImmutableMap;
import net.glowstone.entity.passive.GlowRabbit;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit.Type;

import java.util.Map;

class RabbitStore extends AgeableStore<GlowRabbit> {

    private final Map<Integer, Type> rabbitTypeMap = ImmutableMap.<Integer, Type>builder()
        .put(0, Type.BROWN)
        .put(1, Type.WHITE)
        .put(2, Type.BLACK)
        .put(3, Type.BLACK_AND_WHITE)
        .put(4, Type.GOLD)
        .put(5, Type.SALT_AND_PEPPER)
        .put(99, Type.THE_KILLER_BUNNY)
        .build();
    private final Map<Type, Integer> rabbitTypeIntegerMap = ImmutableMap.<Type, Integer>builder()
        .put(Type.BROWN, 0)
        .put(Type.WHITE, 1)
        .put(Type.BLACK, 2)
        .put(Type.BLACK_AND_WHITE, 3)
        .put(Type.GOLD, 4)
        .put(Type.SALT_AND_PEPPER, 5)
        .put(Type.THE_KILLER_BUNNY, 99)
        .build();

    public RabbitStore() {
        super(GlowRabbit.class, EntityType.RABBIT, GlowRabbit::new);
    }

    @Override
    public GlowRabbit createEntity(Location location, CompoundTag compound) {
        return new GlowRabbit(location);
    }

    @Override
    public void load(GlowRabbit entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setRabbitType(compound.tryGetInt("RabbitType").map(rabbitTypeMap::get)
                .orElse(Type.BROWN));
    }

    @Override
    public void save(GlowRabbit entity, CompoundTag tag) {
        super.save(entity, tag);
        Type rabbitType = entity.getRabbitType();
        if (rabbitType == null) {
            rabbitType = Type.BROWN;
        }
        tag.putInt("RabbitType", rabbitTypeIntegerMap.get(rabbitType));
    }
}
