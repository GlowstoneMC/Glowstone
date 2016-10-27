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

        if (compound.isByte("placedTrap1")) {
            structurePiece.setHasPlacedTrap1(compound.getByte("placedTrap1") != 0);
        }
        if (compound.isByte("placedTrap2")) {
            structurePiece.setHasPlacedTrap2(compound.getByte("placedTrap2") != 0);
        }
        if (compound.isByte("placedMainChest")) {
            structurePiece.setHasPlacedMainChest(compound.getByte("placedMainChest") != 0);
        }
        if (compound.isByte("placedHiddenChest")) {
            structurePiece.setHasPlacedHiddenChest(compound.getByte("placedHiddenChest") != 0);
        }
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
