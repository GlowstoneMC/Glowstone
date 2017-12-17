package net.glowstone.inventory;

import org.bukkit.Material;

/**
 * An interface for checking predicates on Materials.
 */
public interface MaterialMatcher {

    /**
     * Returns true if the given {@link Material} matches the conditions of this MaterialMatcher.
     *
     * @param material the {@link Material} to check
     * @return true if it matches, false otherwise
     */
    boolean matches(Material material);
}
