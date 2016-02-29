package net.glowstone.sponge;

import net.glowstone.text.serializers.GlowPlainSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ConstantTexts {

    private ConstantTexts() { }

    public static void setTextSerializers() {

        RegistryHelper.setFinalStatic(TextSerializers.class, "PLAIN", new GlowPlainSerializer());

    }

}
