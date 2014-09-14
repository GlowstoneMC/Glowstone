package net.glowstone.shiny.event;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Result;

/**
 * A base class for defining events.
 */
public class BaseEvent implements Event {

    private final Game game;
    private String simpleName;

    private boolean cancelled = false;
    private Result result = Result.DEFAULT;

    public BaseEvent(Game game) {
        this.game = game;
    }

    @Override
    public final Game getGame() {
        return game;
    }

    @Override
    public final String getSimpleName() {
        if (simpleName == null) {
            simpleName = calculateName();
        }
        return simpleName;
    }

    private String calculateName() {
        Class<?>[] intfaces = getClass().getInterfaces();
        if (intfaces.length == 1) {
            return intfaces[0].getSimpleName();
        }
        return getClass().getSimpleName();
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void setCancelled(boolean cancel) {
        if (cancel && !isCancellable()) {
            throw new IllegalArgumentException("Cannot cancel " + getSimpleName());
        }
        cancelled = cancel;
    }

    @Override
    public void setResult(Result result) {
        if (result == null) {
            throw new IllegalArgumentException("result must not be null");
        }
        this.result = result;
    }

    @Override
    public final Result getResult() {
        return result;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder(getSimpleName());
        r.append('{');
        if (isCancellable()) {
            r.append("cancelled=").append(cancelled);
        } else {
            r.append("nocancel");
        }
        r.append(", result=").append(result).append('}');
        return r.toString();
    }
}
