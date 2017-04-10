package net.glowstone.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MobState {

    public static final MobState NO_AI = new MobState("no_ai");
    public static final MobState IDLE = new MobState("idle");
    public static final MobState WANDER = new MobState("wander");
    public static final MobState ATTACKED = new MobState("attacked");

    @Getter
    private String name;
}
