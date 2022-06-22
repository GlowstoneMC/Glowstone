package net.glowstone.util;

import lombok.Data;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.util.BlockVector;

@Data
public class GlobalPosition {

    private final NamespacedKey world;
    private final BlockVector position;

}
