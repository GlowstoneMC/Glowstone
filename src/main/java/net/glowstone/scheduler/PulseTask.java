package net.glowstone.scheduler;

import net.glowstone.Server;
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
	private final Server server;

	/**
	 * Creates a new pulse task.
	 * @param server The server.
	 */
	public PulseTask(Server server) {
		super(1);
		this.server = server;
	}

	@Override
	public void execute() {
		server.getSessionRegistry().pulse();
		server.getWorld().pulse();
	}

}
