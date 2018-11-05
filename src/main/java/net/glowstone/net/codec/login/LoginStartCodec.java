package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.login.LoginStartMessage;

public final class LoginStartCodec implements Codec<LoginStartMessage> {

    @Override
    public LoginStartMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        return new LoginStartMessage(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, LoginStartMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        return buf;
    }
}
