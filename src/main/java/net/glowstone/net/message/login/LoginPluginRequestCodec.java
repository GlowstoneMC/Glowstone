package net.glowstone.net.message.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.LoginPluginRequestMessage;

import java.io.IOException;

public class LoginPluginRequestCodec implements Codec<LoginPluginRequestMessage> {
    @Override
    public ByteBuf encode(ByteBuf buf, LoginPluginRequestMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTransactionId());
        ByteBufUtils.writeUTF8(buf, message.getChannel());
        buf.writeBytes(message.getData());

        return buf;
    }

    @Override
    public LoginPluginRequestMessage decode(ByteBuf buf) throws IOException {
        int transactionId = ByteBufUtils.readVarInt(buf);
        String channel = ByteBufUtils.readUTF8(buf);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);

        return new LoginPluginRequestMessage(transactionId, channel, data);
    }
}
