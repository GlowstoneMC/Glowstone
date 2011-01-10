package net.lightstone;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.lightstone.net.MinecraftPipelineFactory;
import net.lightstone.task.TaskScheduler;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public final class Server {

	private static final Logger logger = Logger.getLogger(Server.class.getName());

	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.bind(new InetSocketAddress(25565));
			server.start();
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Error during server startup.", t);
		}
	}

	private final ServerBootstrap bootstrap = new ServerBootstrap();
	private final ChannelGroup group = new DefaultChannelGroup();
	private final TaskScheduler scheduler = new TaskScheduler();
	private final ExecutorService executor = Executors.newCachedThreadPool();

	public Server() {
		logger.info("Starting Lightstone...");
		init();
	}

	private void init() {
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new MinecraftPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);
	}

	public void bind(SocketAddress address) {
		logger.info("Binding to address: " + address + "...");
		group.add(bootstrap.bind(address));
	}

	public void start() {
		logger.info("Ready for connections.");
	}

	public ChannelGroup getChannelGroup() {
		return group;
	}

	public TaskScheduler getScheduler() {
		return scheduler;
	}

}
