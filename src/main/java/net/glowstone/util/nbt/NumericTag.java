package net.glowstone.util.nbt;

public abstract class NumericTag<T extends Number> extends Tag<T> {
    public NumericTag(TagType type) {
        super(type);
    }
}
