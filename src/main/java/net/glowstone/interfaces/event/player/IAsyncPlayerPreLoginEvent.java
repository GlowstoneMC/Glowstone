package net.glowstone.interfaces.event.player;

import net.glowstone.interfaces.event.IEvent;
import org.spongepowered.api.network.RemoteConnection;

public interface IAsyncPlayerPreLoginEvent extends IEvent {

    void init(RemoteConnection connection);

}
