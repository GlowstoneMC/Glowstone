package net.glowstone.util;

/**
 * Generic adapter. Converts or wrapps certain objects to change its class.
 */
public interface Adapter<I, O> {
	public O adapt(I input);
}
