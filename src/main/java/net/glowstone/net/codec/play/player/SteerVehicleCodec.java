package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.SteerVehicleMessage;

import java.io.IOException;

public final class SteerVehicleCodec implements Codec<SteerVehicleMessage> {
    public SteerVehicleMessage decode(ByteBuf buf) throws IOException {
        float sideways = buf.readFloat();
        float forward = buf.readFloat();
        boolean jump = buf.readBoolean();
        boolean unmount = buf.readBoolean();
        return new SteerVehicleMessage(sideways, forward, jump, unmount);
    }

    public ByteBuf encode(ByteBuf buf, SteerVehicleMessage message) throws IOException {
        buf.writeFloat(message.getSideways());
        buf.writeFloat(message.getForward());
        buf.writeBoolean(message.isJump());
        buf.writeBoolean(message.isUnmount());
        return buf;
    }
}
