package net.glowstone.util;

/**
 * Represents a simple validator to compare objects on a true / false basis.
 *
 * @param <E> The type of object to compare.
 */
public interface Validator<E> {

    /**
     * Determines if the supplied object is valid as per the implementation rules defined.
     *
     * @param object The object to validate
     * @return True if valid, false otherwise
     */
    boolean isValid(E object);

}
