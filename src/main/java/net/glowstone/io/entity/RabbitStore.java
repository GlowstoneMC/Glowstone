package net.glowstone.io.entity;

import com.google.common.collect.ImmutableMap;
import net.glowstone.entity.passive.GlowRabbit;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.Rabbit;

import java.util.Map;

class RabbitStore extends AgeableStore<GlowRabbit> {

    private final Map<Integer, Rabbit.Type> rabbitTypeMap = ImmutableMap.<Integer, Rabbit.Type>builder()
            .put(0, Rabbit.Type.BROWN)
            .put(1, Rabbit.Type.WHITE)
            .put(2, Rabbit.Type.BLACK)
            .put(3, Rabbit.Type.BLACK_AND_WHITE)
            .put(4, Rabbit.Type.GOLD)
            .put(5, Rabbit.Type.SALT_AND_PEPPER)
            .put(99, Rabbit.Type.THE_KILLER_BUNNY)
            .build();
    private final Map<Rabbit.Type, Integer> rabbitTypeIntegerMap = ImmutableMap.<Rabbit.Type, Integer>builder()
            .put(Rabbit.Type.BROWN, 0)
            .put(Rabbit.Type.WHITE, 1)
            .put(Rabbit.Type.BLACK, 2)
            .put(Rabbit.Type.BLACK_AND_WHITE, 3)
            .put(Rabbit.Type.GOLD, 4)
            .put(Rabbit.Type.SALT_AND_PEPPER, 5)
            .put(Rabbit.Type.THE_KILLER_BUNNY, 99)
            .build();

    public RabbitStore() {
        super(GlowRabbit.class, "Rabbit");
    }

    @Override
    public GlowRabbit createEntity(Location location, CompoundTag compound) {
        return new GlowRabbit(location);
    }

    @Override
    public void load(GlowRabbit entity, CompoundTag compound) {
        super.load(entity, compound);
        Rabbit.Type rabbitType;
        int rabbitId = compound.getInt("RabbitType");
        if (rabbitTypeMap.containsKey(rabbitId)) {
            rabbitType = rabbitTypeMap.get(rabbitId);
        } else {
            rabbitType = Rabbit.Type.BROWN;
        }
        entity.setRabbitType(rabbitType);
        // TODO "MoreCarrotTicks" -> int
    }

    @Override
    public void save(GlowRabbit entity, CompoundTag tag) {
        super.save(entity, tag);
        Rabbit.Type rabbitType = entity.getRabbitType();
        if (rabbitType == null) {
            rabbitType = Rabbit.Type.BROWN;
        }
        tag.putInt("RabbitType", rabbitTypeIntegerMap.get(rabbitType));
    }
}
