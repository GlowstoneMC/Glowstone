package net.glowstone.data;

import java.util.Set;
import org.bukkit.Keyed;
import org.bukkit.Tag;

public class GlowTag<T extends Keyed> implements Tag<T> {

    // TODO: represents a tag containing one or more keyed items

    @Override
    public boolean isTagged(T item) {
        // TODO: 1.13, whether the item is part of this tag
        return false;
    }

    @Override
    public Set<T> getValues() {
        // TODO: 1.13, get all items part of this tag
        return null;
    }
}
