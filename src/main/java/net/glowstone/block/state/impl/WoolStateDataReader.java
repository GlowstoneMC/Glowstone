package net.glowstone.block.state.impl;

import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.BlockStateReader;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.Wool;

public class WoolStateDataReader extends BlockStateReader<Wool> {

    @Override
    public Wool read(Material material, BlockStateData data) throws InvalidBlockStateException {
        Wool wool = new Wool();
        if (data.containsKey("color")) {
            DyeColor color = StateSerialization.getColor(data.get("wool"));
            if (color == null) {
                return null;
            }
            wool.setColor(color);
        }
        return wool;
    }

    @Override
    public boolean matches(BlockStateData state, Wool data) throws InvalidBlockStateException {
        if (state.containsKey("color")) {
            DyeColor color = StateSerialization.getColor(state.get("wool"));
            if (color == null) {
                throw new InvalidBlockStateException(data.getItemType(), state);
            }
            return data.getColor() == color;
        }
        return true;
    }
}
