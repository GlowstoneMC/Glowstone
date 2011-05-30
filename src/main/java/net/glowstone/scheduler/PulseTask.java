package net.glowstone.scheduler;

import org.bukkit.World;

import net.glowstone.GlowServer;
import net.glowstone.net.SessionRegistry;
import net.glowstone.GlowWorld;

/**
 * A simple {@link Task} which calls the {@link SessionRegistry#pulse()} and
 * {@link GlowWorld#pulse()} methods every tick.
 * @author Graham Edgecombe
 */
public final class PulseTask extends Task {

	/**
	 * The server.
	 */
	private final GlowServer server;

	/**
	 * Creates a new pulse task.
	 * @param server The server.
	 */
	public PulseTask(GlowServer server) {
		super(1);
		this.server = server;
	}

	@Override
	public void execute() {
        try {
            server.getSessionRegistry().pulse();
            for (World world : server.getWorlds())
                ((GlowWorld) world).pulse();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
	}

}
