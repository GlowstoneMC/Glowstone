package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class ExperienceMessage implements Message {

    private final float barValue;
    private final int level, totalExp;

    public ExperienceMessage(float barValue, int level, int totalExp) {
        this.barValue = barValue;
        this.level = level;
        this.totalExp = totalExp;
    }

    public float getBarValue() {
        return barValue;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalExp() {
        return totalExp;
    }

    @Override
    public String toString() {
        return "ExperienceMessage{barValue=" + barValue + ",level=" + level + ",totalExp=" + totalExp + "}";
    }

    public boolean isAsync() {
        return false;
    }
}
