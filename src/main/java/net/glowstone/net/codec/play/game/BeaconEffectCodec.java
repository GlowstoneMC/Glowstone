package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.BeaconEffectMessage;

public final class BeaconEffectCodec implements Codec<BeaconEffectMessage> {
    @Override
    public BeaconEffectMessage decode(ByteBuf buf) throws IOException {
        int primary = ByteBufUtils.readVarInt(buf);
        int secondary = ByteBufUtils.readVarInt(buf);
        return new BeaconEffectMessage(primary, secondary);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BeaconEffectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getPrimary());
        ByteBufUtils.writeVarInt(buf, message.getSecondary());
        return buf;
    }
}
