package com.user.management;

import com.util.ServiceModule;
import com.google.inject.Injector;
import com.google.inject.Guice;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import com.util.SetupUtil;

public class UserManagementService {

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new ServiceModule());

        System.out.println("user management service");
        System.out.println(System.getenv("GOOGLE_CLIENT_ID"));
        System.out.println(System.getenv("MONGODB_URI"));
        System.out.println(System.getenv("MONGODB_DB"));

        Server pollServer = 
            ServerBuilder
                .forPort(SetupUtil.DEFAULT_SERVICE_PORT)
                .addService(injector.getInstance(UserManagementServiceImpl.class))
            .build();
        pollServer.start();
        pollServer.awaitTermination();
    }
}
