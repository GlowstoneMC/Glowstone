package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;

public class GuiListExtended extends Gui {

    public GuiListExtended(Minecraft mc, int width, int height, int a, int b, int c) {
    }

    public int width, height;

    public interface IGuiListEntry {
        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY);

        void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY);

        void setSelected(int a, int b, int c);
    }

    public void setShowSelectionBox(boolean b) {

    }

    protected int getScrollBarX() {
        return 0;
    }

    public int getListWidth() {
        return 0;
    }

    //public GuiConfigEntries.IConfigEntry getListEntry(int index) {
    public Object getListEntry(int index) {
        return null;
    }

    protected int getSize() {
        return 0;
    }

    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {

    }

    protected boolean isSelected(int index) {

    }
}
