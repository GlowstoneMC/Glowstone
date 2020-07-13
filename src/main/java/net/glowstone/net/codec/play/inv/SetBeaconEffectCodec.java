package net.glowstone.net.codec.play.inv;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.glowstone.net.message.play.inv.SetBeaconEffectMessage;

import java.io.IOException;

public final class SetBeaconEffectCodec implements Codec<SetBeaconEffectMessage> {
    @Override
    public SetBeaconEffectMessage decode(ByteBuf byteBuf) throws IOException {
        int primaryEffect = ByteBufUtils.readVarInt(byteBuf);
        int secondaryEffect = ByteBufUtils.readVarInt(byteBuf);
        return new SetBeaconEffectMessage(primaryEffect, secondaryEffect);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, SetBeaconEffectMessage message) {
        ByteBufUtils.writeVarInt(byteBuf, message.getPrimaryEffect());
        ByteBufUtils.writeVarInt(byteBuf, message.getSecondaryEffect());
        return byteBuf;
    }
}
