package net.glowstone.data;

import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class GlowTag<T extends Keyed> implements Tag<T> {

    @Getter
    @NonNull
    private final NamespacedKey key;
    @Getter
    private Set<T> values;


    @Override
    public boolean isTagged(@NotNull T item) {
        return values.contains(item);
    }

    /**
     * Adds an item (can only be added once).
     *
     * @param item The item to add.
     * @return If the item was added, false if already added.
     */
    public boolean addItem(T item) {
        return values.add(item);
    }
}
