package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.Collection;
import net.glowstone.net.message.play.game.ExplosionMessage;
import net.glowstone.net.message.play.game.ExplosionMessage.Record;

public class ExplosionCodec implements Codec<ExplosionMessage> {

    @Override
    public ExplosionMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode ExplosionMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, ExplosionMessage message) throws IOException {
        buf.writeFloat(message.getX());
        buf.writeFloat(message.getY());
        buf.writeFloat(message.getZ());
        buf.writeFloat(message.getRadius());

        Collection<Record> records = message.getRecords();
        buf.writeInt(records.size());
        for (Record record : records) {
            buf.writeByte(record.getX());
            buf.writeByte(record.getY());
            buf.writeByte(record.getZ());
        }

        buf.writeFloat(message.getPlayerMotionX());
        buf.writeFloat(message.getPlayerMotionY());
        buf.writeFloat(message.getPlayerMotionZ());

        return buf;
    }
}
