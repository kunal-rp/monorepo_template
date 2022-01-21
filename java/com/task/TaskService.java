package com.task;

import com.util.ServiceModule;
import com.google.inject.Injector;
import com.google.inject.Guice;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.util.SetupUtil;

public class TaskService {

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new ServiceModule());

        Server pollServer = 
            ServerBuilder
                .forPort(SetupUtil.DEFAULT_SERVICE_PORT)
                .intercept(new AuthenticatedInterceptor())
                .addService(injector.getInstance(TaskServiceImpl.class))
            .build();
        pollServer.start();
        pollServer.awaitTermination();
    }
}
