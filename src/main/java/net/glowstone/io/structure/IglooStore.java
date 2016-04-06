package net.glowstone.io.structure;

import net.glowstone.generator.structures.GlowIgloo;

public class IglooStore extends TemplePieceStore<GlowIgloo> {

    public IglooStore() {
        super(GlowIgloo.class, "Iglu");
    }

    @Override
    public GlowIgloo createStructurePiece() {
        return new GlowIgloo();
    }
}
