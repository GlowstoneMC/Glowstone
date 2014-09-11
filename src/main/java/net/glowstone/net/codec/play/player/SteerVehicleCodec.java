package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.SteerVehicleMessage;

import java.io.IOException;

public final class SteerVehicleCodec implements Codec<SteerVehicleMessage> {
    @Override
    public SteerVehicleMessage decode(ByteBuf buf) throws IOException {
        float sideways = buf.readFloat();
        float forward = buf.readFloat();
        int flags = buf.readUnsignedByte();

        boolean jump = (flags & 0x1) != 0;
        boolean unmount = (flags & 0x2) != 0;
        return new SteerVehicleMessage(sideways, forward, jump, unmount);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SteerVehicleMessage message) throws IOException {
        buf.writeFloat(message.getSideways());
        buf.writeFloat(message.getForward());
        buf.writeByte((message.isJump() ? 1 : 0) | (message.isUnmount() ? 2 : 0));
        return buf;
    }
}
