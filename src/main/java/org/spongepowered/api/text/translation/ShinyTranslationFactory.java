package org.spongepowered.api.text.translation;

import com.google.common.base.Optional;
import net.glowstone.shiny.text.ShinyTranslation;

/**
 * Todo: Javadoc for ShinyTranslationFactory.
 */
class ShinyTranslationFactory implements TranslationFactory {

    public static final TranslationFactory factory = new ShinyTranslationFactory();

    @Override
    public Optional<Translation> getTranslationFromId(String id) {
        return Optional.<Translation>of(new ShinyTranslation(id));
    }

}
