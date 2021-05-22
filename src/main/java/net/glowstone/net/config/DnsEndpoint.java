package net.glowstone.net.config;

import java.util.Map;

public class DnsEndpoint {
    private static final int DEFAULT_DNS_PORT = 53;

    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private final String host;
    private final int port;

    public DnsEndpoint(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Extracts the needed information from a map of key-value pairs inside a config file to
     * set the DNS server for an HTTP client.
     *
     * @param configMap The Map that was extracted from a config file.
     * @return A {@link DnsEndpoint} instance populated with whatever information we could extract.
     */
    @SuppressWarnings("unchecked")
    public static DnsEndpoint fromConfigMap(Map<?, ?> configMap) {
        String host = (String) configMap.get(HOST_KEY);
        Integer port = (Integer) configMap.get(PORT_KEY);

        if (port == null) {
            port = DEFAULT_DNS_PORT;
        }

        return new DnsEndpoint(host, port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
