package net.glowstone.util.lang;

import java.util.MissingResourceException;

/**
 * This exceptino is thrown if the translation lookup engine
 * could not find the specified translation by the given key.
 */
public final class MissingTranslationException extends MissingResourceException {

    public MissingTranslationException(String s, String translation, String key) {
        super(s, translation, key);
    }

}
