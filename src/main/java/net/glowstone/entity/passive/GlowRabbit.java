package net.glowstone.entity.passive;

import com.google.common.collect.ImmutableBiMap;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public class GlowRabbit extends GlowAnimal implements Rabbit {

    private static final ImmutableBiMap<Type, Integer> rabbitTypeIntegerMap = ImmutableBiMap.<Type, Integer>builder()
            .put(Type.BROWN, 0)
            .put(Type.WHITE, 1)
            .put(Type.BLACK, 2)
            .put(Type.BLACK_AND_WHITE, 3)
            .put(Type.GOLD, 4)
            .put(Type.SALT_AND_PEPPER, 5)
            .put(Type.THE_KILLER_BUNNY, 99)
            .build();

    private Type rabbitType = Type.BROWN;

    public GlowRabbit(Location location) {
        super(location, EntityType.RABBIT, 10); // Needs an update with the minecraft version 1.9, then the rabbit has 3 health (1.5 hearts)
        setSize(0.6F, 0.7F);
    }

    @Override
    public Type getRabbitType() {
        return rabbitType;
    }

    @Override
    public void setRabbitType(Type type) {
        checkNotNull(type, "Cannot set a null rabbit type!");
        metadata.set(MetadataIndex.RABBIT_TYPE, rabbitTypeIntegerMap.get(getRabbitType()).byteValue());
        rabbitType = type;
    }

    @Override
    public void kill() {
        super.kill();

        Random r = new Random();

        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.RABBIT, r.nextInt(1)));
        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.RABBIT_HIDE, r.nextInt(1)));
    }
}
