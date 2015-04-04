package net.minecraft.client.gui;

public class GuiTextField extends Gui {

    private String text;
    public int xPosition;
    public int yPosition;
    public int width;

    public GuiTextField(int a, FontRenderer fontRenderer, int b, int c, int d, int e) {

    }

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

    public void setEnabled(boolean enabled) {

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

    public int getCursorPosition() {
        return 0;
    }
}
