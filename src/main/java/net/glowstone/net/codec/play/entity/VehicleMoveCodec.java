package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;

import java.io.IOException;

public class VehicleMoveCodec implements Codec<VehicleMoveMessage> {
    @Override
    public VehicleMoveMessage decode(ByteBuf buffer) throws IOException {
        return new VehicleMoveMessage(); //TODO: Unknown on wiki.vg so far
    }

    @Override
    public ByteBuf encode(ByteBuf buf, VehicleMoveMessage message) throws IOException {
        return buf; //TODO: Unknown on wiki.vg so far
    }
}
