package net.glowstone.text.serializers;

import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.SafeTextSerializer;
import org.spongepowered.api.text.serializer.TextParseException;

public class GlowPlainSerializer implements SafeTextSerializer {

    @Override
    public String serialize(Text text) {
        LiteralText literal = (LiteralText) text; // todo
        return literal.getContent();
    }

    @Override
    public Text deserialize(String content) throws TextParseException {
        return Text.of(content);
    }
}
