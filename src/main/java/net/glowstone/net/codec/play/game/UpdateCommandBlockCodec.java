package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.glowstone.net.message.play.game.UpdateCommandBlockMessage;

import java.io.IOException;

public final class UpdateCommandBlockCodec implements Codec<UpdateCommandBlockMessage> {
    @Override
    public UpdateCommandBlockMessage decode(ByteBuf byteBuf) throws IOException {
        long location = byteBuf.readLong();
        int x = ((Long)(location >> 38)).intValue();
        int y = ((Long)((location >> 26) & 0xFFF)).intValue();
        int z = ((Long)(location << 38 >> 38)).intValue();
        String command = ByteBufUtils.readUTF8(byteBuf);
        int mode = ByteBufUtils.readVarInt(byteBuf);
        byte flags = byteBuf.readByte();
        boolean trackOutput = (flags & 1) == 1;
        boolean isConditional = ((flags >> 1) & 1) == 1;
        boolean automatic = ((flags >> 3) & 1) == 1;
        return new UpdateCommandBlockMessage(x, y, z, command, mode, trackOutput, isConditional, automatic);
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, UpdateCommandBlockMessage message) throws IOException {
        long x = ((Integer) message.getX()).longValue();
        long y = ((Integer) message.getY()).longValue();
        long z = ((Integer) message.getZ()).longValue();
        byteBuf.writeLong(((x & 0x3FFFFFF) << 38) | ((y & 0xFFF) << 26) | (z & 0x3FFFFFF));
        ByteBufUtils.writeUTF8(byteBuf, message.getCommand());
        ByteBufUtils.writeVarInt(byteBuf, message.getMode());
        byte trackOutput = booleanToByte(message.isTrackOutput());
        byte isConditional = booleanToByte(message.isConditional());
        byte automatic = booleanToByte(message.isAutomatic());
        byteBuf.writeByte(trackOutput | isConditional | automatic);
        return byteBuf;
    }

    private static byte booleanToByte(boolean b) {
        if (b)
            return 1;
        else
            return 0;
    }
}
