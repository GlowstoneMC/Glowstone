package net.glowstone.io.structure;

import net.glowstone.generator.structures.GlowTemplePiece;
import net.glowstone.util.nbt.CompoundTag;
import org.jetbrains.annotations.NonNls;

abstract class TemplePieceStore<T extends GlowTemplePiece> extends StructurePieceStore<T> {

    public TemplePieceStore(Class<T> clazz, @NonNls String id) {
        super(clazz, id);
    }

    @Override
    public void load(T structurePiece, CompoundTag compound) {
        super.load(structurePiece, compound);
        compound.readInt("Width", structurePiece::setWidth);
        compound.readInt("Height", structurePiece::setHeight);
        compound.readInt("Depth", structurePiece::setDepth);
        compound.readInt("HPos", structurePiece::setHorizPos);
    }

    @Override
    public void save(T structurePiece, CompoundTag compound) {
        super.save(structurePiece, compound);

        compound.putInt("Width", structurePiece.getWidth());
        compound.putInt("Height", structurePiece.getHeight());
        compound.putInt("Depth", structurePiece.getDepth());
        compound.putInt("HPos", structurePiece.getHorizPos());
    }
}
