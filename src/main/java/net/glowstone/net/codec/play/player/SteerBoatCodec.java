package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.SteerBoatMessage;

import java.io.IOException;

public class SteerBoatCodec implements Codec<SteerBoatMessage> {

    @Override
    public SteerBoatMessage decode(ByteBuf buf) throws IOException {
        boolean isRightPaddleTurning = buf.readBoolean();
        boolean isLeftPaddleTurning = buf.readBoolean();
        return new SteerBoatMessage(isRightPaddleTurning, isLeftPaddleTurning);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SteerBoatMessage steerBoatMessage) throws IOException {
        buf.writeBoolean(steerBoatMessage.isRightPaddleTurning());
        buf.writeBoolean(steerBoatMessage.isLeftPaddleTurning());
        return buf;
    }
}
