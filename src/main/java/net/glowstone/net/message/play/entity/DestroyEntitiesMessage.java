package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

import java.util.Arrays;
import java.util.List;

public final class DestroyEntitiesMessage implements Message {

    private final List<Integer> ids;

    public DestroyEntitiesMessage(Integer... ids) {
        this.ids = Arrays.asList(ids);
    }

    public DestroyEntitiesMessage(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return "DestroyEntitiesMessage{ids=" + ids + "}";
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
