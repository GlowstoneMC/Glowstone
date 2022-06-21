package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.GlobalPosition;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.NamespacedKey;

import java.util.List;

@Data
public final class JoinGameMessage implements Message {

    private final int id;
    private final boolean hardcore;
    private final int mode;
    private final int previousMode;
    private final List<NamespacedKey> worlds;
    private final CompoundTag registryCodec;
    private final NamespacedKey worldType;
    private final NamespacedKey currentWorld;
    private final byte[] seedHash;
    private final int maxPlayers;
    private final int viewDistance;
    private final int simulationDistance;
    private final boolean reducedDebugInfo;
    private final boolean enableRespawnScreen;
    private final boolean debug;
    private final boolean flat;
    private final GlobalPosition lastDeathLocation;

}
