package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import io.papermc.paper.math.Position;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;

@Data
public final class RespawnMessage implements Message {

    // TODO: Dimension (NBT compound)
    private final NamespacedKey dimType;
    private final NamespacedKey dimName;
    private final byte[] seedHash;
    private final int gamemode;
    private final int previousGamemode;
    private final boolean debug;
    private final boolean flat;
    private final boolean copyMetadata;
    private final NamespacedKey deathDimName;
    private final Location deathPosition;

}
