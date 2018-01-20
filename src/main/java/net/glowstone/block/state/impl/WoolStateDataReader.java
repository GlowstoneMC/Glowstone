package net.glowstone.block.state.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.BlockStateReader;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Wool;

public class WoolStateDataReader extends BlockStateReader<Wool> {

    private static final Set<String> VALID_STATES = ImmutableSet.of("color");

    @Override
    public Set<String> getValidStates() {
        return VALID_STATES;
    }

    @Override
    public Wool read(Material material, BlockStateData data) throws InvalidBlockStateException {
        Wool wool = new Wool();
        if (data.contains("color")) {
            DyeColor color = StateSerialization.getColor(data.get("color"));
            if (color == null) {
                throw new InvalidBlockStateException(material, data);
            }
            wool.setColor(color);
        } else {
            wool.setColor(DyeColor.WHITE);
        }
        return wool;
    }

    @Override
    public boolean matches(BlockStateData state, Wool data) throws InvalidBlockStateException {
        if (state.contains("color")) {
            DyeColor color = StateSerialization.getColor(state.get("color"));
            if (color == null) {
                throw new InvalidBlockStateException(data.getItemType(), state);
            }
            return data.getColor() == color;
        }
        return true;
    }
}
