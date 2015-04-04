package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;

public class Gui {

    public int top;
    public int bottom;
    public int left;
    public int right;
    public static String optionsBackground;

    public void handleMouseInput() throws IOException {

    }

    public void drawScreen(int a, int b, float f) {

    }

    public void drawHoveringString(List<?> list, int a, int b) {

    }

    public void onGuiClosed() {

    }

    protected void keyTyped(char eventChar, int eventKey) throws IOException {

    }

    public void displayLoadingString(String s) {
        this.drawString(s);
    }

    public void drawCenteredString(String s) {
        this.drawString(s);
    }

    public void drawString(String s) {
        System.out.println("Drawing string on screen: "+s);

    }

    public void drawCenteredString(FontRenderer fontRenderer, String s, int w, int offset, int color) {
        this.drawString(s);
    }

    public void drawString(FontRenderer fontRenderer, String s, int w, int offset, int color) {
        this.drawString(s);
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {

    }

    public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        return false;
    }

    public void setSelected(int a, int b, int c) {

    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, int z, float idx, int sixteen1, int sixteen2, float f1, float f2) {

    }
}

