package net.glowstone.io.entity;

import java.util.Locale;
import net.glowstone.entity.objects.GlowPainting;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;

public class PaintingStore extends HangingStore<GlowPainting> {

    public PaintingStore() {
        super(GlowPainting.class, EntityType.PAINTING);
    }

    @Override
    public GlowPainting createEntity(Location location, CompoundTag compound) {
        return new GlowPainting(location, BlockFace.SOUTH);
    }

    @Override
    public void load(GlowPainting entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isString("Motive")) {
            entity.setArtInternal(Art.getByName(tag.getString("Motive")));
        }
    }

    @Override
    public void save(GlowPainting entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putString("Motive", entity.getArt().name().toLowerCase(Locale.ENGLISH).replaceAll("_", ""));
    }
}
