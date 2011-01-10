package net.lightstone.net;

import net.lightstone.Server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.StaticChannelPipeline;

public final class MinecraftPipelineFactory implements ChannelPipelineFactory {

	private final Server server;

	public MinecraftPipelineFactory(Server server) {
		this.server = server;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return new StaticChannelPipeline(
			new MinecraftDecoder(),
			new MinecraftEncoder(),
			new MinecraftHandler(server)
		);
	}

}
