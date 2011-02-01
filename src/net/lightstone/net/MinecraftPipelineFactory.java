/*
 * Copyright (c) 2010-2011 Graham Edgecombe.
 *
 * This file is part of Lightstone.
 *
 * Lightstone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Lightstone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightstone.  If not, see <http://www.gnu.org/licenses/>.
 */

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
