package net.glowstone.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.glowstone.io.entity.EntityStore;
import org.bukkit.plugin.Plugin;

@Data
@AllArgsConstructor
public class CustomEntityDescriptor<T extends GlowEntity> {

    private final Class<T> entityClass;
    private final Plugin plugin;
    private final String id;
    @Getter
    private final EntityStore<T> storage;
    private boolean summonable;

    public CustomEntityDescriptor(Class<T> entityClass, Plugin plugin, String id,
        EntityStore<T> storage) {
        this(entityClass, plugin, id, storage, true);
    }
}
