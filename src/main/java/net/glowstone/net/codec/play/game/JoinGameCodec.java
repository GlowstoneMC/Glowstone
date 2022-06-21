package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.JoinGameMessage;
import net.glowstone.util.GlobalPosition;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.NamespacedKey;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.flowpowered.network.util.ByteBufUtils.readVarInt;
import static com.flowpowered.network.util.ByteBufUtils.writeVarInt;
import static net.glowstone.net.GlowBufUtils.*;

public final class JoinGameCodec implements Codec<JoinGameMessage> {

    @Override
    public JoinGameMessage decode(ByteBuf buffer) throws IOException {
        int id = buffer.readInt();
        boolean hardcore = buffer.readBoolean();
        int mode = buffer.readUnsignedByte();
        int previousMode = buffer.readByte();

        int worldsCount = readVarInt(buffer);
        List<NamespacedKey> worlds = new ArrayList<>(worldsCount);
        for (int i = 0; i < worldsCount; i++) {
            worlds.add(readNamespacedKey(buffer));
        }

        // TODO: Decode dimension info from NBT
        CompoundTag registryCodec = readCompound(buffer);

        NamespacedKey worldType = readNamespacedKey(buffer);
        NamespacedKey currentWorld = readNamespacedKey(buffer);
        byte[] seedHash = new byte[8];
        buffer.readBytes(seedHash); // do not use buffer.readBytes(8).array() due memory leak
        int maxPlayers = readVarInt(buffer);
        int viewDistance = readVarInt(buffer);
        int simulationDistance = readVarInt(buffer);
        boolean reducedDebugInfo = buffer.readBoolean();
        boolean enableRespawnScreen = buffer.readBoolean();
        boolean debug = buffer.readBoolean();
        boolean flat = buffer.readBoolean();
        GlobalPosition globalPosition = null;
        boolean hasGlobalPosition = buffer.readBoolean();
        if (hasGlobalPosition) {
            globalPosition = readGlobalPos(buffer);
        }

        return new JoinGameMessage(
            id,
            hardcore,
            mode,
            previousMode,
            worlds,
            registryCodec,
            worldType,
            currentWorld,
            seedHash,
            maxPlayers,
            viewDistance,
            simulationDistance,
            reducedDebugInfo,
            enableRespawnScreen,
            debug,
            flat,
            globalPosition
        );
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JoinGameMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeBoolean(message.isHardcore());
        buf.writeByte(message.getMode());
        buf.writeByte(message.getPreviousMode());
        writeVarInt(buf, message.getWorlds().size());
        for (NamespacedKey world : message.getWorlds()) {
            writeNamespacedKey(buf, world);
        }

        writeCompound(buf, message.getRegistryCodec());

        writeNamespacedKey(buf, message.getWorldType());
        writeNamespacedKey(buf, message.getCurrentWorld());
        buf.writeBytes(message.getSeedHash(), 0, 8);
        writeVarInt(buf, message.getMaxPlayers());
        writeVarInt(buf, message.getViewDistance());
        writeVarInt(buf, message.getSimulationDistance());
        buf.writeBoolean(message.isReducedDebugInfo());
        buf.writeBoolean(message.isEnableRespawnScreen());
        buf.writeBoolean(message.isDebug());
        buf.writeBoolean(message.isFlat());
        GlobalPosition lastDeathLocation = message.getLastDeathLocation();
        if (lastDeathLocation != null) {
            buf.writeBoolean(true);
            writeGlobalPos(buf, lastDeathLocation);
        } else {
            buf.writeBoolean(false);
        }
        return buf;
    }
}
