package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.ClientSettingsPacket;

import java.io.IOException;

public final class ClientSettingsCodec implements Codec<ClientSettingsPacket> {
    @Override
    public ClientSettingsPacket decode(ByteBuf buf) throws IOException {
        String locale = ByteBufUtils.readUTF8(buf);
        int viewDistance = buf.readByte();
        int chatFlags = ByteBufUtils.readVarInt(buf);
        boolean colors = buf.readBoolean();
        int skinFlags = buf.readUnsignedByte();
        int hand = ByteBufUtils.readVarInt(buf);
        return new ClientSettingsPacket(locale, viewDistance, chatFlags, colors, skinFlags, hand);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ClientSettingsPacket message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getLocale());
        buf.writeByte(message.getViewDistance());
        ByteBufUtils.writeVarInt(buf, message.getChatFlags());
        buf.writeBoolean(message.isChatColors());
        buf.writeByte(message.getSkinFlags());
        ByteBufUtils.writeVarInt(buf, message.getHand());
        return buf;
    }
}
