package net.lightstone.task;

import net.lightstone.Server;
import net.lightstone.net.SessionRegistry;
import net.lightstone.world.World;

/**
 * A simple {@link Task} which calls the {@link SessionRegistry#pulse()} and
 * {@link World#pulse()} methods every tick.
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
