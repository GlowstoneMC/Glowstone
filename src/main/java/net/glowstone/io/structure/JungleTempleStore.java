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
        compound.readBoolean(structurePiece::setHasPlacedTrap1, "placedTrap1");
        compound.readBoolean(structurePiece::setHasPlacedTrap2, "placedTrap2");
        compound.readBoolean(structurePiece::setHasPlacedMainChest, "placedMainChest");
        compound.readBoolean(structurePiece::setHasPlacedHiddenChest, "placedHiddenChest");
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
