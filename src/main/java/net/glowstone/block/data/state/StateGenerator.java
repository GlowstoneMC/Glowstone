package net.glowstone.block.data.state;

import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import net.glowstone.block.data.state.generator.EnumStateGenerator;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;

public interface StateGenerator<T> {

    EnumStateGenerator<BlockFace> FOUR_FACING = new EnumStateGenerator<>("facing", 0, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST);
    EnumStateGenerator<BlockFace> SIX_FACING = new EnumStateGenerator<>("facing", 0, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.UP, BlockFace.DOWN);
    EnumStateGenerator<Bed.Part> BED_PART = new EnumStateGenerator<>("foot", Bed.Part.FOOT, Bed.Part.values());

    BooleanStateGenerator OCCUPIED = new BooleanStateGenerator("occupied");
    BooleanStateGenerator DRAG = new BooleanStateGenerator("drag");
    BooleanStateGenerator HAS_BOTTLE_0 = new BooleanStateGenerator("has_bottle_0");
    BooleanStateGenerator HAS_BOTTLE_1 = new BooleanStateGenerator("has_bottle_1");
    BooleanStateGenerator HAS_BOTTLE_2 = new BooleanStateGenerator("has_bottle_2");


    IntegerStateGenerator.Ranged THREE_AGE = new IntegerStateGenerator.Ranged("age", 0, 3);
    IntegerStateGenerator.Ranged SIXTEEN_ROTATION = new IntegerStateGenerator.Ranged("rotation", 0, 16);



    interface EnumGenerator<T> extends StateGenerator<T> {

        T[] getValues();

    }

    int getNetworkId(T id);
    T deserialize(int serial);
    String getId();
    T getDefaultValue();
    StateValue createStateValue(T value);

    default StateValue<T> getDefaultStateValue(){
        return this.createStateValue(this.getDefaultValue());
    }
}
