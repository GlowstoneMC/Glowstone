package net.minecraft.util;

public class ChatComponentText implements IChatComponent {

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
