package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.RespawnMessage;
import net.glowstone.util.nbt.CompoundTag;

import java.io.IOException;

import static com.flowpowered.network.util.ByteBufUtils.readUTF8;
import static com.flowpowered.network.util.ByteBufUtils.writeUTF8;
import static net.glowstone.net.GlowBufUtils.readCompound;
import static net.glowstone.net.GlowBufUtils.writeCompound;

public final class RespawnCodec implements Codec<RespawnMessage> {

    @Override
    public RespawnMessage decode(ByteBuf buf) throws IOException {
        CompoundTag dimension = readCompound(buf);
        String world = readUTF8(buf);
        byte[] seedHash = buf.readBytes(8).array();
        int mode = buf.readByte();
        int previousMode = buf.readByte();
        boolean debug = buf.readBoolean();
        boolean flat = buf.readBoolean();
        boolean copyMetadata = buf.readBoolean();
        return new RespawnMessage(world, seedHash, mode, previousMode, debug, flat, copyMetadata);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RespawnMessage message) throws IOException {
        // TODO: Encode dimension data (1.15+)
        CompoundTag dimension = new CompoundTag();
        writeCompound(buf, dimension);
        writeUTF8(buf, message.getWorld());
        buf.writeBytes(message.getSeedHash(), 0, 8);
        buf.writeByte(message.getMode());
        buf.writeByte(message.getPreviousMode());
        buf.writeBoolean(message.isDebug());
        buf.writeBoolean(message.isFlat());
        buf.writeBoolean(message.isCopyMetadata());
        return buf;
    }
}
