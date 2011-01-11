package net.lightstone.task;

import net.lightstone.Server;

public final class PulseTask extends Task {

	private final Server server;

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
