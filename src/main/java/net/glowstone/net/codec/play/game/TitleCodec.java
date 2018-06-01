package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.TitleMessage;
import net.glowstone.net.message.play.game.TitleMessage.Action;
import net.glowstone.util.TextMessage;

public final class TitleCodec implements Codec<TitleMessage> {

    @Override
    public TitleMessage decode(ByteBuf buffer) throws IOException {
        int actionId = ByteBufUtils.readVarInt(buffer);
        Action action = Action.getAction(actionId);
        switch (action) {
            case TITLE:
            case SUBTITLE:
            case ACTION:
                String text = ByteBufUtils.readUTF8(buffer);
                return new TitleMessage(action, TextMessage.decode(text));
            case TIMES:
                int fadeIn = buffer.readInt();
                int stay = buffer.readInt();
                int fadeOut = buffer.readInt();
                return new TitleMessage(action, fadeIn, stay, fadeOut);
            default:
                return new TitleMessage(action);
        }
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TitleMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getAction().ordinal());
        switch (message.getAction()) {
            case TITLE:
            case SUBTITLE:
            case ACTION:
                ByteBufUtils.writeUTF8(buf, message.getText().encode());
                break;
            case TIMES:
                buf.writeInt(message.getFadeIn());
                buf.writeInt(message.getStay());
                buf.writeInt(message.getFadeOut());
                break;
            default:
                // TODO: Should this raise a warning?
                // do nothing
        }
        return buf;
    }
}
