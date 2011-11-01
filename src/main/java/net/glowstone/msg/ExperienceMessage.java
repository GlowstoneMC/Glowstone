package net.glowstone.msg;

public class ExperienceMessage extends Message {

    private final float barValue;
    private final short level, totalExp;

    public ExperienceMessage(float barValue, short level, short  totalExp) {
        this.barValue = barValue;
        this.level = level;
        this.totalExp = totalExp;
    }

    public float getBarValue() {
        return barValue;
    }

    public short getLevel() {
        return level;
    }

    public short getTotalExp() {
        return totalExp;
    }

    @Override
    public String toString() {
        return "ExperienceMessage{barValue=" + barValue + ",level=" + level + ",totalExp=" + totalExp + "}";
    }
}
