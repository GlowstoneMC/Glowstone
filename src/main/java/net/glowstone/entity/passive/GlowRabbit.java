package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;

import java.util.List;

public class GlowRabbit extends GlowAnimal implements Rabbit {

    private static final ImmutableBiMap<RabbitType, Integer> rabbitTypeIntegerMap = ImmutableBiMap.<RabbitType, Integer>builder()
            .put(Rabbit.RabbitType.BROWN, 0)
            .put(Rabbit.RabbitType.WHITE, 1)
            .put(Rabbit.RabbitType.BLACK, 2)
            .put(Rabbit.RabbitType.BLACK_AND_WHITE, 3)
            .put(Rabbit.RabbitType.GOLD, 4)
            .put(Rabbit.RabbitType.SALT_PEPPER, 5)
            .put(Rabbit.RabbitType.KILLER, 99)
            .build();

    private RabbitType rabbitType = RabbitType.BROWN;

    public GlowRabbit(Location location) {
        super(location, EntityType.RABBIT);
        setSize(0.3F, 0.7F);
    }

    @Override
    public RabbitType getRabbitType() {
        return rabbitType;
    }

    @Override
    public void setRabbitType(RabbitType type) {
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
