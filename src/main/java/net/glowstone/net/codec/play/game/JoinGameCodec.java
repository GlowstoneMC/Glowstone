package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.JoinGameMessage;
import net.glowstone.util.nbt.CompoundTag;

import java.io.IOException;

import static com.flowpowered.network.util.ByteBufUtils.readUTF8;
import static com.flowpowered.network.util.ByteBufUtils.readVarInt;
import static com.flowpowered.network.util.ByteBufUtils.writeUTF8;
import static com.flowpowered.network.util.ByteBufUtils.writeVarInt;
import static net.glowstone.net.GlowBufUtils.readCompound;
import static net.glowstone.net.GlowBufUtils.writeCompound;

public final class JoinGameCodec implements Codec<JoinGameMessage> {

    @Override
    public JoinGameMessage decode(ByteBuf buffer) throws IOException {
        int id = buffer.readInt();
        boolean hardcore = buffer.readBoolean();
        int mode = buffer.readByte();
        int previousMode = buffer.readByte();

        String[] worlds = new String[readVarInt(buffer)];
        for (int i = 0; i < worlds.length; i++) {
            worlds[i] = readUTF8(buffer);
        }

        // TODO: Decode dimension info from NBT
        CompoundTag dimensionCodec = readCompound(buffer);
        CompoundTag dimension = readCompound(buffer);

        String currentWorld = readUTF8(buffer);
        byte[] seedHash = buffer.readBytes(8).array();
        int maxPlayers = readVarInt(buffer);
        int viewDistance = readVarInt(buffer);
        boolean reducedDebugInfo = buffer.readBoolean();
        boolean enableRespawnScreen = buffer.readBoolean();
        boolean debug = buffer.readBoolean();
        boolean flat = buffer.readBoolean();

        return new JoinGameMessage(
            id,
            hardcore,
            mode,
            previousMode,
            worlds,
            currentWorld,
            seedHash,
            maxPlayers,
            viewDistance,
            reducedDebugInfo,
            enableRespawnScreen,
            debug,
            flat
        );
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGameMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeBoolean(message.isHardcore());
        buf.writeByte(message.getMode());
        buf.writeByte(message.getPreviousMode());
        writeVarInt(buf, message.getWorlds().length);
        for (String world : message.getWorlds()) {
            writeUTF8(buf, world);
        }

        CompoundTag dimensionCodec = new CompoundTag(); // TODO: Serialize from message
        writeCompound(buf, dimensionCodec);

        CompoundTag dimension = new CompoundTag(); // TODO: Serialize from message
        writeCompound(buf, dimension);

        writeUTF8(buf, message.getCurrentWorld());
        buf.writeBytes(message.getSeedHash(), 0, 8);
        writeVarInt(buf, message.getMaxPlayers());
        writeVarInt(buf, message.getViewDistance());
        buf.writeBoolean(message.isReducedDebugInfo());
        buf.writeBoolean(message.isEnableRespawnScreen());
        buf.writeBoolean(message.isDebug());
        buf.writeBoolean(message.isFlat());
        return buf;
    }
}
