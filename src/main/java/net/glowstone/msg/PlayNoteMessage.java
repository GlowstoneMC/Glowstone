package net.glowstone.msg;

public final class PlayNoteMessage extends Message {

    private final int x, y, z, instrument, pitch;

    public PlayNoteMessage(int x, int y, int z, int instrument, int pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.instrument = instrument;
        this.pitch = pitch;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getInstrument() {
        return instrument;
    }

    public int getPitch() {
        return pitch;
    }

    @Override
    public String toString() {
        return "PlayNoteMessage{x=" + x + ",y=" + y + ",z=" + z + ",instrument=" + instrument + ",pitch=" + pitch + "}";
    }
}
