package net.glowstone.io.structure;

import net.glowstone.generator.structures.GlowJungleTemple;
import net.glowstone.util.nbt.CompoundTag;

public class JungleTempleStore extends TemplePieceStore<GlowJungleTemple> {

    public JungleTempleStore() {
        super(GlowJungleTemple.class, "TeJP");
    }

    @Override
    public GlowJungleTemple createStructurePiece() {
        return new GlowJungleTemple();
    }

    @Override
    public void load(GlowJungleTemple structurePiece, CompoundTag compound) {
        super.load(structurePiece, compound);
        compound.readBoolean("placedTrap1", structurePiece::setHasPlacedTrap1);
        compound.readBoolean("placedTrap2", structurePiece::setHasPlacedTrap2);
        compound.readBoolean("placedMainChest", structurePiece::setHasPlacedMainChest);
        compound.readBoolean("placedHiddenChest", structurePiece::setHasPlacedHiddenChest);
    }

    @Override
    public void save(GlowJungleTemple structurePiece, CompoundTag compound) {
        super.save(structurePiece, compound);

        compound.putByte("placedTrap1", structurePiece.getHasPlacedTrap1() ? 1 : 0);
        compound.putByte("placedTrap2", structurePiece.getHasPlacedTrap2() ? 1 : 0);
        compound.putByte("placedMainChest", structurePiece.getHasPlacedMainChest() ? 1 : 0);
        compound.putByte("placedHiddenChest", structurePiece.getHasPlacedHiddenChest() ? 1 : 0);
    }
}
