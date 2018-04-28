package net.glowstone.io.structure;

import net.glowstone.generator.structures.GlowWitchHut;
import net.glowstone.util.nbt.CompoundTag;

public class WitchHutStore extends TemplePieceStore<GlowWitchHut> {

    public WitchHutStore() {
        super(GlowWitchHut.class, "TeSH");
    }

    @Override
    public GlowWitchHut createStructurePiece() {
        return new GlowWitchHut();
    }

    @Override
    public void load(GlowWitchHut structurePiece, CompoundTag compound) {
        super.load(structurePiece, compound);
        compound.readBoolean(structurePiece::setHasWitch, "Witch");
    }

    @Override
    public void save(GlowWitchHut structurePiece, CompoundTag compound) {
        super.save(structurePiece, compound);

        compound.putByte("Witch", structurePiece.getHasWitch() ? 1 : 0);
    }
}
