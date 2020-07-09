package com.yx.grpc.server.serverfactory;

import com.yx.grpc.server.service.GrpcServiceDefinition;
import io.grpc.Server;
import org.springframework.beans.factory.DisposableBean;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:49
 * @Description: GrpcServerFactory
 */
public interface GrpcServerFactory extends DisposableBean {

    /**
     * Creates a new grpc server with the stored options. The entire lifecycle management of the server should be
     * managed by the calling class. This includes starting and stopping the server.
     *
     * @return The newly created grpc server.
     */
    Server createServer();

    /**
     * Gets the IP address the created server will be bound to.
     *
     * @return The IP address the server will be bound to.
     */
    String getAddress();

    /**
     * Gets the local port the created server will use to listen to requests.
     *
     * @return Gets the local port the server will use.
     */
    int getPort();

    /**
     * Adds the given grpc service definition to this factory. The created server will serve the services described by
     * these definitions.
     *
     * <p>
     * <b>Note:</b> Adding a service does not effect servers that have already been created.
     * </p>
     *
     * @param service The service to add to the grpc server.
     */
    void addService(GrpcServiceDefinition service);

    /**
     * Destroys this factory. This does not destroy or shutdown any server that was created using this factory.
     */
    @Override
    void destroy();

}
