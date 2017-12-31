package net.glowstone.testutils;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.BlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.powermock.api.mockito.PowerMockito;

public class InMemoryBlockStorage {
    private Map<Location, GlowBlock> mockBlocks = new HashMap<>();

    private GlowBlock createMockBlock(Location location, Material type, byte data) {
        GlowBlock mockBlock = PowerMockito.mock(GlowBlock.class);
        when(mockBlock.getLocation()).thenReturn(location);
        when(mockBlock.getType()).thenReturn(type);
        when(mockBlock.getTypeId()).thenReturn(type.getId());
        when(mockBlock.getData()).thenReturn(data);

        BlockEntity entity = null;
        BlockType blockType = ItemTable.instance().getBlock(type);
        if (blockType != null) {
            entity = blockType.createBlockEntity(mockBlock);
        }
        when(mockBlock.getBlockEntity()).thenReturn(entity);
        return mockBlock;
    }

    public GlowBlock getBlockAt(Location location) {
        return mockBlocks.computeIfAbsent(location, (k) -> createMockBlock(location, Material.AIR, (byte) 0));
    }

    public void setBlockType(Location location, Material type) {
        mockBlocks.put(location, createMockBlock(location, type, (byte) 0));
    }

    public void setBlockData(Location location, byte data) {
        mockBlocks.computeIfPresent(location, (k, v) -> {
            if (v.getType() == Material.AIR) {
                return v;
            } else {
                return createMockBlock(location, v.getType(), data);
            }
        });
    }
}
