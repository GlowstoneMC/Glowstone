package net.glowstone.inventory;

import net.glowstone.testutils.ServerShim;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * An attempt at test casing the ridiculously complicated window click logic.
 */
public class WindowClickLogicTest {

    // maybe use parameterized tests sometime

    // mode, button, slot, clickType
    private static final String[][] clickMap = {
        {"0", "0", "-1", "WINDOW_BORDER_LEFT"},
        {"0", "0", "-999", "LEFT"},
        {"0", "0", "0", "LEFT"},
        {"0", "1", "-1", "WINDOW_BORDER_RIGHT"},
        {"0", "1", "-999", "RIGHT"},
        {"0", "1", "0", "RIGHT"},
        {"1", "0", "0", "SHIFT_LEFT"},
        {"1", "1", "0", "SHIFT_RIGHT"},
        {"2", "0", "0", "NUMBER_KEY"},
        {"2", "1", "0", "NUMBER_KEY"},
        {"2", "2", "0", "NUMBER_KEY"},
        {"2", "3", "0", "NUMBER_KEY"},
        {"2", "4", "0", "NUMBER_KEY"},
        {"2", "5", "0", "NUMBER_KEY"},
        {"2", "6", "0", "NUMBER_KEY"},
        {"2", "7", "0", "NUMBER_KEY"},
        {"2", "8", "0", "NUMBER_KEY"},
        {"3", "0", "0", "UNKNOWN"},
        {"4", "0", "0", "DROP"},
        {"4", "1", "0", "CONTROL_DROP"},
        {"6", "0", "0", "DOUBLE_CLICK"},
    };
    // clickType, slot, cursor, slot item, action
    private static final String[][] actionMap = {
        {"CONTROL_DROP", "0", "null", "COBBLESTONE x 1", "DROP_ALL_SLOT"},
        {"CONTROL_DROP", "0", "null", "IRON_HELMET x 1", "DROP_ALL_SLOT"},
        {"DOUBLE_CLICK", "0", "COBBLESTONE x 33", "null", "COLLECT_TO_CURSOR"},
        {"DOUBLE_CLICK", "0", "RAILS x 1", "null", "COLLECT_TO_CURSOR"},
        {"DOUBLE_CLICK", "0", "RAILS x 4", "null", "COLLECT_TO_CURSOR"},
        {"DOUBLE_CLICK", "0", "RAILS x 58", "null", "COLLECT_TO_CURSOR"},
        {"DOUBLE_CLICK", "0", "RAILS x 61", "null", "COLLECT_TO_CURSOR"},
        {"DROP", "0", "null", "COBBLESTONE x 2", "DROP_ONE_SLOT"},
        {"DROP", "0", "null", "IRON_HELMET x 1", "DROP_ONE_SLOT"},
        {"DROP", "0", "null", "RAILS x 56", "DROP_ONE_SLOT"},
        {"DROP", "0", "null", "RAILS x 64", "DROP_ONE_SLOT"},
        {"LEFT", "-999", "GRASS x 1", "null", "DROP_ALL_CURSOR"},
        {"LEFT", "-999", "GRASS x 2", "null", "DROP_ALL_CURSOR"},
        {"LEFT", "0", "COBBLESTONE x 1", "COBBLESTONE x 32", "PLACE_ONE"},
        {"LEFT", "0", "COBBLESTONE x 1", "COBBLESTONE x 64", "NOTHING"},
        {"LEFT", "0", "COBBLESTONE x 1", "GRASS x 64", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "COBBLESTONE x 1", "RAILS x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "COBBLESTONE x 1", "null", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 2", "GRASS x 28", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "COBBLESTONE x 31", "COBBLESTONE x 1", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 32", "null", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 33", "RAILS x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "COBBLESTONE x 60", "COBBLESTONE x 1", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 62", "null", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 63", "null", "PLACE_ALL"},
        {"LEFT", "0", "COBBLESTONE x 64", "COBBLESTONE x 1", "PLACE_SOME"},
        {"LEFT", "0", "COBBLESTONE x 64", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 1", "GRASS x 1", "PLACE_ONE"},
        {"LEFT", "0", "GRASS x 1", "GRASS x 3", "PLACE_ONE"},
        {"LEFT", "0", "GRASS x 1", "GRASS x 8", "PLACE_ONE"},
        {"LEFT", "0", "GRASS x 1", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 28", "COBBLESTONE x 2", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "GRASS x 28", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 29", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 2", "GRASS x 4", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 2", "GRASS x 6", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 32", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 54", "GRASS x 6", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 54", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 56", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 5", "GRASS x 60", "PLACE_SOME"},
        {"LEFT", "0", "GRASS x 5", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 61", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 62", "RAILS x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "GRASS x 62", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 63", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 64", "RAILS x 64", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "GRASS x 64", "null", "PLACE_ALL"},
        {"LEFT", "0", "GRASS x 7", "GRASS x 57", "PLACE_ALL"},
        {"LEFT", "0", "IRON_HELMET x 1", "COBBLESTONE x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "IRON_HELMET x 1", "GRASS x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "IRON_HELMET x 1", "IRON_HELMET x 1", "NOTHING"},
        {"LEFT", "0", "IRON_HELMET x 1", "RAILS x 55", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "IRON_HELMET x 1", "RAILS x 64", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "IRON_HELMET x 1", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 10", "RAILS x 55", "PLACE_SOME"},
        {"LEFT", "0", "RAILS x 1", "GRASS x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "RAILS x 1", "GRASS x 54", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "RAILS x 1", "RAILS x 1", "PLACE_ONE"},
        {"LEFT", "0", "RAILS x 1", "RAILS x 5", "PLACE_ONE"},
        {"LEFT", "0", "RAILS x 1", "RAILS x 62", "PLACE_ONE"},
        {"LEFT", "0", "RAILS x 1", "RAILS x 64", "NOTHING"},
        {"LEFT", "0", "RAILS x 1", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 28", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 2", "RAILS x 6", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 55", "RAILS x 1", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 55", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 56", "GRASS x 5", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "RAILS x 57", "RAILS x 1", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 61", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 63", "null", "PLACE_ALL"},
        {"LEFT", "0", "RAILS x 64", "IRON_HELMET x 1", "SWAP_WITH_CURSOR"},
        {"LEFT", "0", "RAILS x 64", "null", "PLACE_ALL"},
        {"LEFT", "0", "null", "COBBLESTONE x 1", "PICKUP_ALL"},
        {"LEFT", "0", "null", "COBBLESTONE x 32", "PICKUP_ALL"},
        {"LEFT", "0", "null", "COBBLESTONE x 33", "PICKUP_ALL"},
        {"LEFT", "0", "null", "COBBLESTONE x 63", "PICKUP_ALL"},
        {"LEFT", "0", "null", "COBBLESTONE x 64", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 10", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 1", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 2", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 32", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 3", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 54", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 5", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 60", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 62", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 64", "PICKUP_ALL"},
        {"LEFT", "0", "null", "GRASS x 7", "PICKUP_ALL"},
        {"LEFT", "0", "null", "IRON_HELMET x 1", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 10", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 1", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 27", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 2", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 4", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 56", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 58", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 61", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 63", "PICKUP_ALL"},
        {"LEFT", "0", "null", "RAILS x 64", "PICKUP_ALL"},
        {"LEFT", "0", "null", "null", "NOTHING"},
        {"NUMBER_KEY", "0", "null", "COBBLESTONE x 1", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "COBBLESTONE x 32", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "COBBLESTONE x 33", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "COBBLESTONE x 64", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "GRASS x 10", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "GRASS x 1", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "IRON_HELMET x 1", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "RAILS x 1", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "RAILS x 62", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "RAILS x 63", "HOTBAR_SWAP"},
        {"NUMBER_KEY", "0", "null", "null", "HOTBAR_SWAP"},
        {"RIGHT", "-999", "GRASS x 30", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 31", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 32", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 57", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 58", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 59", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "GRASS x 60", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "IRON_HELMET x 1", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "-999", "RAILS x 1", "null", "DROP_ONE_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 1", "IRON_HELMET x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 1", "RAILS x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 2", "GRASS x 28", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 33", "GRASS x 63", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 61", "GRASS x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 62", "null", "PLACE_ONE"},
        {"RIGHT", "0", "COBBLESTONE x 63", "COBBLESTONE x 1", "PLACE_ONE"},
        {"RIGHT", "0", "COBBLESTONE x 63", "null", "PLACE_ONE"},
        {"RIGHT", "0", "COBBLESTONE x 64", "IRON_HELMET x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "COBBLESTONE x 64", "null", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 10", "GRASS x 1", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 1", "GRASS x 9", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 1", "null", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 28", "COBBLESTONE x 2", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "GRASS x 29", "null", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 30", "GRASS x 2", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 31", "GRASS x 1", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 32", "null", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 60", "RAILS x 64", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "GRASS x 64", "IRON_HELMET x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "GRASS x 64", "RAILS x 64", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "GRASS x 64", "null", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 6", "GRASS x 5", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 7", "GRASS x 4", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 8", "GRASS x 3", "PLACE_ONE"},
        {"RIGHT", "0", "GRASS x 9", "GRASS x 2", "PLACE_ONE"},
        {"RIGHT", "0", "IRON_HELMET x 1", "GRASS x 64", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "IRON_HELMET x 1", "RAILS x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "IRON_HELMET x 1", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 1", "COBBLESTONE x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 1", "COBBLESTONE x 64", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 1", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 56", "RAILS x 4", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 57", "RAILS x 3", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 58", "RAILS x 2", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 59", "RAILS x 1", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 60", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 61", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 62", "IRON_HELMET x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 62", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 63", "null", "PLACE_ONE"},
        {"RIGHT", "0", "RAILS x 64", "COBBLESTONE x 1", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 64", "COBBLESTONE x 61", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 64", "COBBLESTONE x 63", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 64", "GRASS x 64", "SWAP_WITH_CURSOR"},
        {"RIGHT", "0", "RAILS x 64", "null", "PLACE_ONE"},
        {"RIGHT", "0", "null", "COBBLESTONE x 1", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "COBBLESTONE x 62", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "COBBLESTONE x 64", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "GRASS x 1", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "GRASS x 64", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "RAILS x 1", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "RAILS x 55", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "RAILS x 64", "PICKUP_HALF"},
        {"RIGHT", "0", "null", "null", "NOTHING"},
        {"SHIFT_LEFT", "0", "COBBLESTONE x 33", "COBBLESTONE x 32", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "COBBLESTONE x 33", "GRASS x 64", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "COBBLESTONE x 33", "RAILS x 2", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "COBBLESTONE x 32", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "COBBLESTONE x 62", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "COBBLESTONE x 64", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "GRASS x 1", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "GRASS x 29", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "GRASS x 32", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "GRASS x 64", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "IRON_HELMET x 1", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "RAILS x 1", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "RAILS x 64", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_LEFT", "0", "null", "null", "NOTHING"},
        {"SHIFT_RIGHT", "0", "COBBLESTONE x 33", "COBBLESTONE x 32", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_RIGHT", "0", "COBBLESTONE x 33", "GRASS x 64", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_RIGHT", "0", "null", "COBBLESTONE x 1", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_RIGHT", "0", "null", "GRASS x 28", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_RIGHT", "0", "null", "GRASS x 3", "MOVE_TO_OTHER_INVENTORY"},
        {"SHIFT_RIGHT", "0", "null", "null", "NOTHING"},
        {"UNKNOWN", "0", "null", "COBBLESTONE x 1", "UNKNOWN"},
        {"UNKNOWN", "0", "null", "GRASS x 54", "UNKNOWN"},
        {"UNKNOWN", "0", "null", "IRON_HELMET x 1", "UNKNOWN"},
        {"WINDOW_BORDER_LEFT", "-1", "GRASS x 1", "null", "NOTHING"},
        {"WINDOW_BORDER_LEFT", "-1", "IRON_HELMET x 1", "null", "NOTHING"},
        {"WINDOW_BORDER_LEFT", "-1", "RAILS x 55", "null", "NOTHING"},
        {"WINDOW_BORDER_RIGHT", "-1", "IRON_HELMET x 1", "null", "NOTHING"},
        {"WINDOW_BORDER_RIGHT", "-1", "RAILS x 1", "null", "NOTHING"},
        {"WINDOW_BORDER_RIGHT", "-1", "RAILS x 63", "null", "NOTHING"},
    };

    @BeforeAll
    public static void initShim() {
        ServerShim.install();
    }

    @Test
    public void testClickType() {
        for (String[] testCase : clickMap) {
            int mode = Integer.parseInt(testCase[0]);
            int button = Integer.parseInt(testCase[1]);
            int slot = Integer.parseInt(testCase[2]);
            String expected = testCase[3];

            String actual = String.valueOf(WindowClickLogic.getClickType(mode, button, slot));
            assertThat("Failure for mode=" + mode + ", button=" + button + ", slot=" + slot, actual,
                is(expected));
        }
    }

    // slot numbers are normalized: slot > 0 is 0 instead

    @Test
    public void testAction() {
        for (String[] testCase : actionMap) {
            ClickType clickType = ClickType.valueOf(testCase[0]);
            int slot = Integer.parseInt(testCase[1]);
            ItemStack cursor = parseItemStack(testCase[2]);
            ItemStack slotItem = parseItemStack(testCase[3]);
            String expected = testCase[4];

            InventoryType.SlotType slotType =
                (slot < 0) ? InventoryType.SlotType.OUTSIDE : InventoryType.SlotType.CONTAINER;

            String actual = String
                .valueOf(WindowClickLogic.getAction(clickType, slotType, cursor, slotItem));
            assertThat(
                "Failure for click=" + clickType + ", slot=" + slot + ", cursor=" + testCase[2]
                    + ", slotItem=" + testCase[3], actual, is(expected));
        }
    }

    private ItemStack parseItemStack(String s) {
        if (s.equalsIgnoreCase("null")) {
            return null;
        }
        int index = s.indexOf(" x ");
        String before = s.substring(0, index);
        String after = s.substring(index + 3);
        Material mat = Material.getMaterial(before);
        int amount = Integer.parseInt(after);
        return new ItemStack(mat, amount);
    }

}
