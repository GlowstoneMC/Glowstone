package net.glowstone.msg;

import java.util.List;

import net.glowstone.util.Parameter;

public final class EntityMetadataMessage extends Message {

    private final int id;
    private final List<Parameter<?>> parameters;

    public EntityMetadataMessage(int id, List<Parameter<?>> parameters) {
        this.id = id;
        this.parameters = parameters;
    }

    public int getId() {
        return id;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("EntityInteractionMessage{id=");
        build.append(id).append(",metadata=[");
        for (Parameter<?> param : parameters) {
            build.append(param.toString()).append(",");
        }
        build.append("]}");
        return build.toString();
    }
}
