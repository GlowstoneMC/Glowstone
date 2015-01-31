package net.glowstone.io.entity;

import com.google.common.collect.ImmutableMap;
import net.glowstone.entity.passive.GlowRabbit;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.Rabbit;

import java.util.Map;

class RabbitStore extends AgeableStore<GlowRabbit> {

    private final Map<Integer, Rabbit.RabbitType> rabbitTypeMap = ImmutableMap.<Integer, Rabbit.RabbitType>builder()
            .put(0, Rabbit.RabbitType.BROWN)
            .put(1, Rabbit.RabbitType.WHITE)
            .put(2, Rabbit.RabbitType.BLACK)
            .put(3, Rabbit.RabbitType.BLACK_AND_WHITE)
            .put(4, Rabbit.RabbitType.GOLD)
            .put(5, Rabbit.RabbitType.SALT_PEPPER)
            .put(99, Rabbit.RabbitType.KILLER)
            .build();
    private final Map<Rabbit.RabbitType, Integer> rabbitTypeIntegerMap = ImmutableMap.<Rabbit.RabbitType, Integer>builder()
            .put(Rabbit.RabbitType.BROWN, 0)
            .put(Rabbit.RabbitType.WHITE, 1)
            .put(Rabbit.RabbitType.BLACK, 2)
            .put(Rabbit.RabbitType.BLACK_AND_WHITE, 3)
            .put(Rabbit.RabbitType.GOLD, 4)
            .put(Rabbit.RabbitType.SALT_PEPPER, 5)
            .put(Rabbit.RabbitType.KILLER, 99)
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
        Rabbit.RabbitType rabbitType;
        int rabbitId = compound.getInt("RabbitType");
        if (rabbitTypeMap.containsKey(rabbitId)) {
            rabbitType = rabbitTypeMap.get(rabbitId);
        } else {
            rabbitType = Rabbit.RabbitType.BROWN;
        }
        entity.setRabbitType(rabbitType);
        // TODO "MoreCarrotTicks" -> int
    }

    @Override
    public void save(GlowRabbit entity, CompoundTag tag) {
        super.save(entity, tag);
        Rabbit.RabbitType rabbitType = entity.getRabbitType();
        if (rabbitType == null) {
            rabbitType = Rabbit.RabbitType.BROWN;
        }
        tag.putInt("RabbitType", rabbitTypeIntegerMap.get(rabbitType));
    }
}
