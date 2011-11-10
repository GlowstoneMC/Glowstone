package net.glowstone.msg;

public final class DiggingMessage extends Message {
    
    public static final int STATE_START_DIGGING = 0;
    public static final int STATE_DONE_DIGGING = 2;
    public static final int STATE_DROP_ITEM = 4;

    private final int state, x, y, z, face;

    public DiggingMessage(int state, int x, int y, int z, int face) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }

    public int getState() {
        return state;
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

    public int getFace() {
        return face;
    }

    @Override
    public String toString() {
        return "DiggingMessage{state=" + state + ",x=" + x + ",y=" + y + ",z=" + z + ",face=" + face + "}";
    }
}
