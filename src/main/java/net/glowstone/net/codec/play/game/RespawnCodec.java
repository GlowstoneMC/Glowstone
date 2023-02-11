package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.RespawnMessage;
import net.glowstone.util.Position;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

import static com.flowpowered.network.util.ByteBufUtils.readUTF8;
import static com.flowpowered.network.util.ByteBufUtils.writeUTF8;
import static net.glowstone.net.GlowBufUtils.readCompound;
import static net.glowstone.net.GlowBufUtils.writeCompound;

public final class RespawnCodec implements Codec<RespawnMessage> {

    @Override
    public RespawnMessage decode(ByteBuf buf) throws IOException {
        throw new RuntimeException("Decoding RespawnMessages Unsupported");
        /**
        CompoundTag dimension = readCompound(buf);
        String world = readUTF8(buf);
        byte[] seedHash = new byte[8];
        buf.readBytes(seedHash);
        int mode = buf.readByte();
        int previousMode = buf.readByte();
        boolean debug = buf.readBoolean();
        boolean flat = buf.readBoolean();
        boolean copyMetadata = buf.readBoolean();
        return new RespawnMessage(world, seedHash, mode, previousMode, debug, flat, copyMetadata);
         **/
    }

    @Override
    public ByteBuf encode(ByteBuf buf, RespawnMessage message) throws IOException {
        GlowBufUtils.writeNamespacedKey(buf, message.getDimName());
        GlowBufUtils.writeNamespacedKey(buf, message.getDimType());
        buf.writeBytes(message.getSeedHash(), 0, 8);
        buf.writeByte(message.getGamemode());
        buf.writeByte(message.getPreviousGamemode());
        buf.writeBoolean(message.isDebug());
        buf.writeBoolean(message.isFlat());
        buf.writeBoolean(message.isCopyMetadata());
        boolean writeDeathInfo = message.getDeathDimName() != null;
        buf.writeBoolean(writeDeathInfo);
        if (writeDeathInfo) {
            GlowBufUtils.writeNamespacedKey(buf, message.getDimType());
            GlowBufUtils.writeBlockPosition(buf, message.getDeathPosition().toVector());
        }
        return buf;
    }
}
