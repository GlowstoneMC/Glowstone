package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;

import java.util.List;

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
        super(location, EntityType.RABBIT);
        setSize(0.3F, 0.7F);
    }

    @Override
    public Rabbit.Type getRabbitType() {
        return rabbitType;
    }

    @Override
    public void setRabbitType(Rabbit.Type type) {
        Validate.notNull(type, "Cannot set a null rabbit type!");
        this.rabbitType = type;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowRabbit.class);
        map.set(MetadataIndex.RABBIT_TYPE, rabbitTypeIntegerMap.get(this.getRabbitType()).byteValue());
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }
}
