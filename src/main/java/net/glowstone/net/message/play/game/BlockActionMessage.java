package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

@Data
public final class BlockActionMessage implements Message {

    public BlockActionMessage(int x, int y, int z, int actionId, int actionParam, int blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.actionId = actionId;
        this.actionParam = actionParam;
        this.blockType = blockType;
    }

    public BlockActionMessage(int x, int y, int z, int actionId, int actionParam, Material type) {
        this(x, y, z, actionId, actionParam, MaterialUtil.getId(type));
    }

    public BlockActionMessage(int x, int y, int z, int actionId, int actionParam, BlockData blockData) {
        this(x, y, z, actionId, actionParam, MaterialUtil.getId(blockData));
    }

    private final int x;
    private final int y;
    private final int z;
    private final int actionId;
    private final int actionParam;
    private final int blockType;

}
