package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class AttachEntityMessage implements Message {

    private final int id, vehicle;
    private final boolean leash;

    public AttachEntityMessage(int id, int vehicle, boolean leash) {
        this.id = id;
        this.vehicle = vehicle;
        this.leash = leash;
    }

    public int getId() {
        return id;
    }

    public int getVehicle() {
        return vehicle;
    }

    public boolean isLeash() {
        return leash;
    }

    @Override
    public String toString() {
        return "AttachEntityMessage{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", leash=" + leash +
                '}';
    }
}
