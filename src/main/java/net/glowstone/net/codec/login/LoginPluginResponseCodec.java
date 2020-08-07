package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.LoginPluginResponseMessage;

import java.io.IOException;

public class LoginPluginResponseCodec implements Codec<LoginPluginResponseMessage> {
    @Override
    public LoginPluginResponseMessage decode(ByteBuf buf) throws IOException {
        int transactionId = ByteBufUtils.readVarInt(buf);
        boolean successful = buf.readBoolean();
        int remainingBytes = buf.readableBytes();
        ByteBuf data = buf.readBytes(remainingBytes);

        return new LoginPluginResponseMessage(transactionId, successful, data);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginPluginResponseMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getTransactionId());
        buf.writeBoolean(message.isSuccessful());
        buf.writeBytes(message.getData());

        return buf;
    }
}
