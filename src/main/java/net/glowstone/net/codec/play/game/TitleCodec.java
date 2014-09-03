package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.TitleMessage;

import java.io.IOException;

public final class TitleCodec implements Codec<TitleMessage> {

    @Override
    public TitleMessage decode(ByteBuf buffer) throws IOException {
        int actionId = ByteBufUtils.readVarInt(buffer);
        TitleMessage.Action action = TitleMessage.Action.getAction(actionId);
        switch (action) {
            case TITLE:
            case SUBTITLE:
                String text = ByteBufUtils.readUTF8(buffer);
                return new TitleMessage(action, text);
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
                ByteBufUtils.writeUTF8(buf, message.getText());
                break;
            case TIMES:
                buf.writeInt(message.getFadeIn());
                buf.writeInt(message.getStay());
                buf.writeInt(message.getFadeOut());
                break;
        }
        return buf;
    }
}
