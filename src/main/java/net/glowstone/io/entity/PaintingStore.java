package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowPainting;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class PaintingStore extends HangingStore<GlowPainting> {

    public PaintingStore() {
        super(GlowPainting.class, EntityType.PAINTING);
    }

    @Override
    public GlowPainting createEntity(Location location, CompoundTag compound) {
        return new GlowPainting(location);
    }

    @Override
    public void load(GlowPainting entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readString("Motive",
            motive -> entity.setArtInternal(GlowPainting.getArtFromTitle(motive)));
    }

    @Override
    public void save(GlowPainting entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putString("Motive", entity.getArtTitle());
        NbtSerialization.locationToListTags(entity.getArtCenter(), tag);
    }
}
