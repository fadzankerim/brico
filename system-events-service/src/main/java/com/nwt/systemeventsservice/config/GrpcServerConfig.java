package com.nwt.systemeventsservice.config;

import com.nwt.systemeventsservice.grpc.SystemEventsGrpcServer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class GrpcServerConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private final SystemEventsGrpcServer systemEventsGrpcServer;

    private Server grpcServer;

    public GrpcServerConfig(SystemEventsGrpcServer systemEventsGrpcServer) {
        this.systemEventsGrpcServer = systemEventsGrpcServer;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            grpcServer = NettyServerBuilder.forPort(grpcPort)
                    .addService(systemEventsGrpcServer)
                    .build()
                    .start();

            log.info("gRPC server started on port {}", grpcPort);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down gRPC server");
                if (grpcServer != null && !grpcServer.isShutdown()) {
                    grpcServer.shutdown();
                    try {
                        grpcServer.awaitTermination(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("gRPC server shutdown interrupted", e);
                    }
                }
            }));

        } catch (IOException e) {
            log.error("Failed to start gRPC server on port {}", grpcPort, e);
            throw new RuntimeException("Could not start gRPC server", e);
        }
    }

    @PreDestroy
    public void stopGrpcServer() {
        if (grpcServer != null && !grpcServer.isShutdown()) {
            log.info("Stopping gRPC server gracefully");
            grpcServer.shutdown();
            try {
                grpcServer.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("gRPC server pre-destroy shutdown interrupted", e);
            }
        }
    }
}
