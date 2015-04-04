package net.minecraft.client.gui;

import java.util.List;

public class Gui {

    public int top;
    public int bottom;
    public int left;
    public int right;

    public void handleMouseInput() {

    }


    public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }

    public boolean mouseClicked(int a, int b, int c) {
        return false;
    }

    protected void mouseReleased(int a, int b, int c) {

    }

    public void drawScreen(int a, int b, float f) {

    }

    public void drawHoveringString(List<?> list, int a, int b) {

    }

    public void onGuiClosed() {

    }

    protected void keyTyped(char eventChar, int eventKey) {

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
}

