package net.glowstone.msg;

public final class ProgressBarMessage extends Message {

    private final int id, progressBar, value;

    public ProgressBarMessage(int id, int progressBar, int value) {
        this.id = id;
        this.progressBar = progressBar;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getProgressBar() {
        return progressBar;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ProgressBarMessage{id=" + id + ",progressBar=" + progressBar + ",value=" + value + "}";
    }
}
