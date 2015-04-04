package net.minecraft.util;

public class ChatComponentText {

    private String string;

    public ChatComponentText(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }

    public String getUnformattedText() {
        return string;
    }
}
