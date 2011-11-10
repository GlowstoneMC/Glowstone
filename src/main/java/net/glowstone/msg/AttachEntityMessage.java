package net.glowstone.msg;

public final class AttachEntityMessage extends Message {

    private final int id, vehicle;

    public AttachEntityMessage(int id, int vehicle) {
        this.id = id;
        this.vehicle = vehicle;
    }

    public int getId() {
        return id;
    }

    public int getVehicle() {
        return vehicle;
    }

    @Override
    public String toString() {
        return "AttachEntityMessage{id=" + id + ",vehicle=" + vehicle + "}";
    }
}
