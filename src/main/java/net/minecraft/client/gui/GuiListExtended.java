package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;

public class GuiListExtended extends Gui {

    public GuiListExtended(Minecraft mc, int width, int height, int a, int b, int c) {
    }

    public int width, height;

    public interface IGuiListEntry {
    }

    public void setShowSelectionBox(boolean b) {

    }

    protected int getScrollBarX() {

    }

    public int getListWidth() {

    }

    public GuiEditArrayEntries.IArrayEntry getListEntry(int index) {

    }

    protected int getSize() {

    }

}
