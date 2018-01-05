package net.glowstone.io.structure;

import net.glowstone.generator.structures.GlowTemplePiece;
import net.glowstone.util.nbt.CompoundTag;

abstract class TemplePieceStore<T extends GlowTemplePiece> extends StructurePieceStore<T> {

    public TemplePieceStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(T structurePiece, CompoundTag compound) {
        super.load(structurePiece, compound);

        if (compound.isInt("Width")) {
            structurePiece.setWidth(compound.getInt("Width"));
        }
        if (compound.isInt("Height")) {
            structurePiece.setHeight(compound.getInt("Height"));
        }
        if (compound.isInt("Depth")) {
            structurePiece.setDepth(compound.getInt("Depth"));
        }
        if (compound.isInt("HPos")) {
            structurePiece.setHorizPos(compound.getInt("HPos"));
        }
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
