package net.lightstone.net;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.StaticChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;

public final class MinecraftPipelineFactory implements ChannelPipelineFactory {

	private final ChannelGroup group;

	public MinecraftPipelineFactory(ChannelGroup group) {
		this.group = group;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		return new StaticChannelPipeline(
			new MinecraftDecoder(),
			new MinecraftEncoder(),
			new MinecraftHandler(group)
		);
	}

}
