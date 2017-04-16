package net.glowstone.net;

import io.netty.channel.ChannelFactory;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.NameResolver;
import io.netty.resolver.ResolvedAddressTypes;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.resolver.dns.DnsServerAddresses;

import java.net.InetAddress;

public class IPv4DnsAddressResolverGroup extends DnsAddressResolverGroup {
    public IPv4DnsAddressResolverGroup(Class<? extends DatagramChannel> channelType, DnsServerAddresses nameServerAddresses) {
        super(channelType, nameServerAddresses);
    }

    @Override
    protected NameResolver<InetAddress> newNameResolver(EventLoop eventLoop, ChannelFactory<? extends DatagramChannel> channelFactory, DnsServerAddresses nameServerAddresses) throws Exception {
        // work-around from https://github.com/netty/netty/issues/6559#issuecomment-288429829
        // setting address types to IPV4_ONLY appears to fix an issue with the default DnsAddressResolver
        return (new DnsNameResolverBuilder(eventLoop)).channelFactory(channelFactory).nameServerAddresses(nameServerAddresses).resolvedAddressTypes(ResolvedAddressTypes.IPV4_ONLY).build();
    }
}
