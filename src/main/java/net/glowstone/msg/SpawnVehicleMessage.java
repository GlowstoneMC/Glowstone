package net.glowstone.msg;

public final class SpawnVehicleMessage extends Message {

    private final int id, type, x, y, z, fireballId, fireballX, fireballY, fireballZ;

    public SpawnVehicleMessage(int id, int type, int x, int y, int z) {
        this(id, type, x, y, z, 0, 0, 0, 0);
    }
    public SpawnVehicleMessage(int id, int type, int x, int y, int z, int fbId, int fbX, int fbY, int fbZ) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.fireballId = fbId;
        this.fireballX = fbX;
        this.fireballY = fbY;
        this.fireballZ = fbZ;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
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

    public boolean hasFireball() {
        return fireballId != 0;
    }

    public int getFireballId() {
        return fireballId;
    }

    public int getFireballX() {
        return fireballX;
    }

    public int getFireballY() {
        return fireballY;
    }

    public int getFireballZ() {
        return fireballZ;
    }

}
