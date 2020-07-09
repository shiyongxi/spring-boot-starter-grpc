package com.yx.grpc.server.serverfactory;

import io.grpc.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: shiyongxi
 * @Date: 2020-03-19 15:55
 * @Description: GrpcServerLifecycle
 */
@Slf4j
public class GrpcServerLifecycle implements SmartLifecycle {
    private static AtomicInteger serverCounter = new AtomicInteger(-1);

    private volatile Server server;
    private volatile int phase = Integer.MAX_VALUE;
    private final GrpcServerFactory factory;

    public GrpcServerLifecycle(final GrpcServerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void start() {
        try {
            createAndStartGrpcServer();
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to start the grpc server", e);
        }
    }

    @Override
    public void stop() {
        stopAndReleaseGrpcServer();
    }

    @Override
    public void stop(final Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isRunning() {
        return this.server != null && !this.server.isShutdown();
    }

    @Override
    public int getPhase() {
        return this.phase;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * Creates and starts the grpc server.
     *
     * @throws IOException If the server is unable to bind the port.
     */
    protected void createAndStartGrpcServer() throws IOException {
        final Server localServer = this.server;
        if (localServer == null) {
            this.server = this.factory.createServer();
            this.server.start();
            log.info("gRPC Server started, listening on address: " + this.factory.getAddress() + ", port: "
                    + this.factory.getPort());

            // Prevent the JVM from shutting down while the server is running
            final Thread awaitThread = new Thread(() -> {
                try {
                    this.server.awaitTermination();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "grpc-server-container-" + (serverCounter.incrementAndGet()));
            awaitThread.setDaemon(false);
            awaitThread.start();
        }
    }

    /**
     * Initiates an orderly shutdown of the grpc server and releases the references to the server. This call does not
     * wait for the server to be completely shut down.
     */
    protected void stopAndReleaseGrpcServer() {
        final Server localServer = this.server;
        if (localServer != null) {
            localServer.shutdown();
            this.server = null;
            log.info("gRPC server shutdown.");
        }
    }
}
