package net.glowstone.command;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.powermock.api.mockito.PowerMockito;

import java.util.function.Supplier;

public abstract class CommandTestWithFakePlayers<T extends Command> extends CommandTest<T> {
    private final String[] names;
    protected GlowWorld world;
    protected GlowServer server;
    protected GlowPlayer[] fakePlayers;
    protected Location location;

    protected CommandTestWithFakePlayers(Supplier<T> commandSupplier, String... names) {
        super(commandSupplier);
        this.names = names;
    }

    @Override
    public void before() {
        super.before();
        server = PowerMockito.mock(GlowServer.class);
        world = PowerMockito.mock(GlowWorld.class);
        location = new Location(world, 10.5, 20.0, 30.5);
        fakePlayers = prepareMockPlayers(location, server, world, names);
    }
}
