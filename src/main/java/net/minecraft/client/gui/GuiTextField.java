package net.minecraft.client.gui;

public class GuiTextField {

    private String text;
    public int xPosition;
    public int yPosition;

    public void setMaxStringLength(int length) {

    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setVisible(boolean visible) {

    }

    public void drawTextBox() {
        System.out.println("GuiTextField: draw " + this.text);
    }

    public void textboxKeyTyped(int character, int eventKey) {

    }

    public void updateCursorCounter() {

    }

    public void mouseClicked(int x, int y, int mouseEvent) {

    }
}
