package net.glowstone.entity.passive;

import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;

import static com.google.common.base.Preconditions.checkNotNull;

public class GlowRabbit extends GlowAnimal implements Rabbit {

    private static final ImmutableBiMap<Rabbit.Type, Integer> rabbitTypeIntegerMap = ImmutableBiMap.<Rabbit.Type, Integer>builder()
            .put(Rabbit.Type.BROWN, 0)
            .put(Rabbit.Type.WHITE, 1)
            .put(Rabbit.Type.BLACK, 2)
            .put(Rabbit.Type.BLACK_AND_WHITE, 3)
            .put(Rabbit.Type.GOLD, 4)
            .put(Rabbit.Type.SALT_AND_PEPPER, 5)
            .put(Type.THE_KILLER_BUNNY, 99)
            .build();

    private Rabbit.Type rabbitType = Rabbit.Type.BROWN;

    public GlowRabbit(Location location) {
        super(location, EntityType.RABBIT, 10); // Needs an update with the minecraft version 1.9, then the rabbit has 3 health (1.5 hearts)
        setSize(0.6F, 0.7F);
    }

    @Override
    public Rabbit.Type getRabbitType() {
        return rabbitType;
    }

    @Override
    public void setRabbitType(Rabbit.Type type) {
        checkNotNull(type, "Cannot set a null rabbit type!");
        metadata.set(MetadataIndex.RABBIT_TYPE, rabbitTypeIntegerMap.get(this.getRabbitType()).byteValue());
        this.rabbitType = type;
    }
}
