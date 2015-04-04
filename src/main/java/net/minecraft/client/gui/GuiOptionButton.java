package net.minecraft.client.gui;

public class GuiOptionButton extends GuiButton {
    public GuiOptionButton(int id, int xPos, int yPos, int width, int height, String displayString) {
        super(id, xPos, yPos, width, height, displayString);
    }

    public GuiOptionButton(int id, int xPos, int yPos, int height, String displayString) {
        super(id, xPos, yPos, 0, height, displayString);
    }

    public GuiOptionButton(int id, int xPos, int yPos, String displayString) {
        super(id, xPos, yPos, 0, 0, displayString);
    }
}
