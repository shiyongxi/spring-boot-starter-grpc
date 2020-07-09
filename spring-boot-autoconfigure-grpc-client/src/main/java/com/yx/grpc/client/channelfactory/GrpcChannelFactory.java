package com.yx.grpc.client.channelfactory;

import io.grpc.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This factory creates grpc {@link Channel}s for a given service name. Implementations are encouraged to utilize
 * connection pooling and thus {@link #close() close} should be called before disposing it.
 *
 * @Auther: shiyongxi
 * @Date: 2020-03-17 10:12
 * @Description: GrpcChannelFactory
 */
public interface GrpcChannelFactory extends AutoCloseable {
    /**
     * Creates a new channel for the given service name. The returned channel will use all globally registered
     * {@link ClientInterceptor}s.
     *
     * <p>
     * <b>Note:</b> The underlying implementation might reuse existing {@link ManagedChannel}s allow connection reuse.
     * </p>
     *
     * @param name The name of the service.
     * @return The newly created channel for the given service.
     */
    default Channel createChannel(final String name) {
        return createChannel(name, Collections.emptyList());
    }

    /**
     * Creates a new channel for the given service name. The returned channel will use all globally registered
     * {@link ClientInterceptor}s.
     *
     * <p>
     * <b>Note:</b> The underlying implementation might reuse existing {@link ManagedChannel}s allow connection reuse.
     * </p>
     *
     * <p>
     * <b>Note:</b> The given interceptors will be appended to the global interceptors and applied using
     * {@link ClientInterceptors#interceptForward(Channel, ClientInterceptor...)}.
     * </p>
     *
     * @param name The name of the service.
     * @param interceptors A list of additional client interceptors that should be added to the channel.
     * @return The newly created channel for the given service.
     */
    default Channel createChannel(final String name, final List<ClientInterceptor> interceptors) {
        return createChannel(name, interceptors, true);
    }

    /**
     * Creates a new channel for the given service name. The returned channel will use all globally registered
     * {@link ClientInterceptor}s.
     *
     * <p>
     * <b>Note:</b> The underlying implementation might reuse existing {@link ManagedChannel}s allow connection reuse.
     * </p>
     *
     * <p>
     * <b>Note:</b> The given interceptors will be appended to the global interceptors and applied using
     * {@link ClientInterceptors#interceptForward(Channel, ClientInterceptor...)}.
     * </p>
     *
     * @param name The name of the service.
     * @param interceptors A list of additional client interceptors that should be added to the channel.
     * @param sortInterceptors Whether the interceptors (both global and custom) should be sorted before being applied.
     * @return The newly created channel for the given service.
     */
    Channel createChannel(String name, List<ClientInterceptor> interceptors, boolean sortInterceptors);

    /**
     * Gets an unmodifiable map that contains the names of the created channel with their current
     * {@link ConnectivityState}. This method will return an empty map, if the feature is not supported.
     *
     * @return A map with the channel names and their connectivity state.
     */
    default Map<String, ConnectivityState> getConnectivityState() {
        return Collections.emptyMap();
    }

    @Override
    void close();
}
