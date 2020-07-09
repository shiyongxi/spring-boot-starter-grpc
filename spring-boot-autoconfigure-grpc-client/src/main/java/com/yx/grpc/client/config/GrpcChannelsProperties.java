package com.yx.grpc.client.config;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:17
 * @Description: GrpcChannelsProperties
 */
@ToString
@EqualsAndHashCode
@ConfigurationProperties("spring.grpc")
public class GrpcChannelsProperties {
    /**
     * The key that will be used for the {@code GLOBAL} properties.
     */
    public static final String GLOBAL_PROPERTIES_KEY = "GLOBAL";

    private final Map<String, GrpcChannelProperties> client = new ConcurrentHashMap<>();

    private Map<String, String> serviceNames = null;

    /**
     * Gets the configuration mapping for each client.
     *
     * @return The client configuration mappings.
     */
    public final Map<String, GrpcChannelProperties> getClient() {
        return this.client;
    }

    /**
     * Gets the properties for the given channel. If the properties for the specified channel name do not yet exist,
     * they are created automatically. Before the instance is returned, the unset values are filled with values from the
     * global properties.
     *
     * @param name The name of the channel to get the properties for.
     * @return The properties for the given channel name.
     */
    public GrpcChannelProperties getChannel(final String name) {
        final GrpcChannelProperties properties = getRawChannel(name);
        properties.copyDefaultsFrom(getGlobalChannel());
        return properties;
    }

    /**
     * Gets the global channel properties. Global properties are used, if the channel properties don't overwrite them.
     * If neither the global nor the per client properties are set then default values will be used.
     *
     * @return The global channel properties.
     */
    public final GrpcChannelProperties getGlobalChannel() {
        // This cannot be moved to its own field,
        // as Spring replaces the instance in the map and inconsistencies would occur.
        return getRawChannel(GLOBAL_PROPERTIES_KEY);
    }

    /**
     * Gets or creates the channel properties for the given client.
     *
     * @param name The name of the channel to get the properties for.
     * @return The properties for the given channel name.
     */
    private GrpcChannelProperties getRawChannel(final String name) {
        return this.client.computeIfAbsent(name, key -> new GrpcChannelProperties());
    }

    public String getName(String serviceName) {
        if (serviceNames == null) {
            serviceNames = new HashMap<>();
            for (Map.Entry<String, GrpcChannelProperties> entry: this.client.entrySet()) {
                if (entry.getValue().getServiceNames() == null) {
                    continue;
                }

                for (String sn: entry.getValue().getServiceNames().split(",")) {
                    serviceNames.put(sn, entry.getKey());
                }
            }
        }

        return serviceNames.get(serviceName);
    }

    private String defaultScheme;

    /**
     * Get the default scheme that should be used, if the client doesn't specify a scheme/address.
     *
     * @return The default scheme to use or null.
     * @see #setDefaultScheme(String)
     */
    public String getDefaultScheme() {
        return this.defaultScheme;
    }

    /**
     * Sets the default scheme to use, if the client doesn't specify a scheme/address. If not specified it will default
     * to the default scheme of the {@link io.grpc.NameResolver.Factory}. Examples: {@code dns}, {@code discovery}.
     *
     * @param defaultScheme The default scheme to use or null.
     */
    public void setDefaultScheme(String defaultScheme) {
        this.defaultScheme = defaultScheme;
    }
}
