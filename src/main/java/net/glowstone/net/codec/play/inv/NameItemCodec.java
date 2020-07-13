package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import net.glowstone.net.message.play.inv.NameItemMessage;

import java.io.IOException;

@Data
public final class NameItemCodec implements Codec<NameItemMessage> {
    @Override
    public NameItemMessage decode(ByteBuf byteBuf) throws IOException {
        String itemName = ByteBufUtils.readUTF8(byteBuf);
        return new NameItemMessage(itemName);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, NameItemMessage message) throws IOException {
        ByteBufUtils.writeUTF8(byteBuf, message.getItemName());
        return byteBuf;
    }
}
