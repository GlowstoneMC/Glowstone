package net.glowstone.block.data.state;

import net.glowstone.block.data.state.generator.BooleanStateGenerator;
import net.glowstone.block.data.state.generator.EnumStateGenerator;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import org.bukkit.Axis;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.*;

public interface StateGenerator<T> {

    EnumStateGenerator<RedstoneWire.Connection> REDSTONE_CONNECTION_NORTH = new EnumStateGenerator<>("north", 0, RedstoneWire.Connection.values());
    EnumStateGenerator<RedstoneWire.Connection> REDSTONE_CONNECTION_EAST = new EnumStateGenerator<>("east", 0, RedstoneWire.Connection.values());
    EnumStateGenerator<RedstoneWire.Connection> REDSTONE_CONNECTION_SOUTH = new EnumStateGenerator<>("south", 0, RedstoneWire.Connection.values());
    EnumStateGenerator<RedstoneWire.Connection> REDSTONE_CONNECTION_WEST = new EnumStateGenerator<>("west", 0, RedstoneWire.Connection.values());
    EnumStateGenerator<BlockFace> FOUR_FACING = new EnumStateGenerator<>("facing", 0, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST);
    EnumStateGenerator<BlockFace> SIX_FACING = new EnumStateGenerator<>("facing", 0, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.UP, BlockFace.DOWN);
    EnumStateGenerator<Bed.Part> BED_PART = new EnumStateGenerator<>("foot", Bed.Part.FOOT, Bed.Part.values());
    EnumStateGenerator<Axis> AXIS = new EnumStateGenerator<>("axis", Axis.X, Axis.values());
    EnumStateGenerator<Axis> TWO_AXIS = new EnumStateGenerator<>("axis", Axis.X, Axis.X, Axis.Z);
    EnumStateGenerator<Instrument> INSTRUMENT = new EnumStateGenerator<>("instrument", 0, Instrument.values());
    EnumStateGenerator<Note.Tone> NOTE = new EnumStateGenerator<>("note", 0, Note.Tone.values());
    EnumStateGenerator<Bisected.Half> HALF = new EnumStateGenerator<>("half", 0, Bisected.Half.values());
    EnumStateGenerator<PistonHead.Type> PISTON_TYPE = new EnumStateGenerator<>("type", 0, PistonHead.Type.values());
    EnumStateGenerator<Stairs.Shape> STAIRS_SHAPE = new EnumStateGenerator<>("shape", 0, Stairs.Shape.values());
    EnumStateGenerator<Door.Hinge> HINGE = new EnumStateGenerator<>("hinge", 0, Door.Hinge.values());
    EnumStateGenerator<Chest.Type> CHEST_TYPE = new EnumStateGenerator<>("type", 0, Chest.Type.values());
    EnumStateGenerator<Comparator.Mode> COMPARATOR_MODE = new EnumStateGenerator<>("mode", 0, Comparator.Mode.COMPARE);
    EnumStateGenerator<Slab.Type> SLAB_TYPE  = new EnumStateGenerator("type", 0, Slab.Type.values());
    EnumStateGenerator<StructureBlock.Mode> STRUCTURE_MODE = new EnumStateGenerator("mode", 0, StructureBlock.Mode.values());

    BooleanStateGenerator BOOLEAN_NORTH = new BooleanStateGenerator("north");
    BooleanStateGenerator BOOLEAN_EAST = new BooleanStateGenerator("east");
    BooleanStateGenerator BOOLEAN_SOUTH = new BooleanStateGenerator("south");
    BooleanStateGenerator BOOLEAN_WEST = new BooleanStateGenerator("west");
    BooleanStateGenerator BOOLEAN_UP = new BooleanStateGenerator("up");
    BooleanStateGenerator BOOLEAN_DOWN = new BooleanStateGenerator("down");
    BooleanStateGenerator IN_WALL = new BooleanStateGenerator("in_wall");
    BooleanStateGenerator ENABLED = new BooleanStateGenerator("enabled");
    BooleanStateGenerator HAS_RECORD = new BooleanStateGenerator("has_record");
    BooleanStateGenerator OPEN = new BooleanStateGenerator("open");
    BooleanStateGenerator WATER_LOGGED = new BooleanStateGenerator("waterlogged");
    BooleanStateGenerator SHORT = new BooleanStateGenerator("short");
    BooleanStateGenerator POWERED = new BooleanStateGenerator("powered");
    BooleanStateGenerator EXTENDED = new BooleanStateGenerator("extended");
    BooleanStateGenerator OCCUPIED = new BooleanStateGenerator("occupied");
    BooleanStateGenerator SNOWY = new BooleanStateGenerator("snowy");
    BooleanStateGenerator DRAG = new BooleanStateGenerator("drag");
    BooleanStateGenerator HAS_BOTTLE_0 = new BooleanStateGenerator("has_bottle_0");
    BooleanStateGenerator HAS_BOTTLE_1 = new BooleanStateGenerator("has_bottle_1");
    BooleanStateGenerator HAS_BOTTLE_2 = new BooleanStateGenerator("has_bottle_2");
    BooleanStateGenerator PERSISTENT = new BooleanStateGenerator("persistent");
    BooleanStateGenerator TRIGGERED = new BooleanStateGenerator("triggered");
    BooleanStateGenerator LIT = new BooleanStateGenerator("lit");
    BooleanStateGenerator LOCKED = new BooleanStateGenerator("locked");
    BooleanStateGenerator EYE = new BooleanStateGenerator("eye");
    BooleanStateGenerator ATTACHED = new BooleanStateGenerator("attached");
    BooleanStateGenerator CONDITIONAL = new BooleanStateGenerator("conditional");
    BooleanStateGenerator INVERTED = new BooleanStateGenerator("inverted");

    IntegerStateGenerator.Ranged EGGS = new IntegerStateGenerator.Ranged("eggs", 0, 5);
    IntegerStateGenerator.Ranged HATCH = new IntegerStateGenerator.Ranged("hatch", 0, 3);
    IntegerStateGenerator.Ranged PICKLES = new IntegerStateGenerator.Ranged("pickles", 0, 5);
    IntegerStateGenerator.Ranged DELAY = new IntegerStateGenerator.Ranged("delay", 0, 5);
    IntegerStateGenerator.Ranged MOISTURE = new IntegerStateGenerator.Ranged("moisture", 0, 8);
    IntegerStateGenerator.Ranged REDSTONE_POWER = new IntegerStateGenerator.Ranged("power", 0, 16);
    IntegerStateGenerator.Ranged TWO_STAGE = new IntegerStateGenerator.Ranged("stage", 0, 2);
    IntegerStateGenerator.Ranged THREE_AGE = new IntegerStateGenerator.Ranged("age", 0, 3);
    IntegerStateGenerator.Ranged FOUR_LEVEL = new IntegerStateGenerator.Ranged("level", 0, 4);
    IntegerStateGenerator.Ranged SIXTEEN_LEVEL = new IntegerStateGenerator.Ranged("level", 0, 16);
    IntegerStateGenerator.Ranged EIGHT_DISTANCE  = new IntegerStateGenerator.Ranged("level", 0, 8);
    IntegerStateGenerator.Ranged EIGHT_AGE = new IntegerStateGenerator.Ranged("age", 0, 8);
    IntegerStateGenerator.Ranged SEVEN_BITES = new IntegerStateGenerator.Ranged("bites", 0, 7);
    IntegerStateGenerator.Ranged SIXTEEN_AGE = new IntegerStateGenerator.Ranged("age", 0, 16);
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
