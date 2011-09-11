package net.glowstone.msg;

public class ExperienceMessage extends Message {

    private final byte barValue, level;
    private final short totalExp;

    public ExperienceMessage(byte barValue, byte level, short totalExp) {
        this.barValue = barValue;
        this.level = level;
        this.totalExp = totalExp;
    }

    public byte getBarValue() {
        return barValue;
    }

    public byte getLevel() {
        return level;
    }

    public short getTotalExp() {
        return totalExp;
    }
    
}
