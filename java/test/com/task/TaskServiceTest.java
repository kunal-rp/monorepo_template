package com.task;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import io.grpc.ManagedChannel;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.common.collect.Iterables;
import com.task.TaskServiceGrpc;
import com.user.util.JWTUtil;
import com.task.TaskServiceProto.ActionRequest;
import com.task.TaskServiceProto.ActionResponse;
import com.task.TaskServiceImpl;
import com.task.Constants;
import com.task.AuthenticatedInterceptor;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import com.user.UserProto.UserId;
import com.util.FakeServiceModule;

@RunWith(JUnit4.class)
public class TaskServiceTest {

    private final UserId USER_ID = UserId.newBuilder().setId(11111).build();
    private final String COOKIE_VALUE = "TEST1233";

    protected Injector injector = Guice.createInjector(new FakeServiceModule());

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    
    private String serverName = InProcessServerBuilder.generateName();
    private InProcessServerBuilder serverBuilder = InProcessServerBuilder
          .forName(serverName).directExecutor();
    private InProcessChannelBuilder channelBuilder = InProcessChannelBuilder
          .forName(serverName).directExecutor();

    private Metadata metadata;

    @Before
    public void setup() {
        injector.injectMembers(this);

        //setup metadata
        String cookieValue = Constants.CUSTOM_SLOT_HEADER_COOKIE_KEY + "="+ COOKIE_VALUE;
        metadata = new Metadata();
        metadata.put(Constants.COOKIE_SLOT_METADATA_KEY, cookieValue);
        metadata.put(Constants.ACCESS_SLOT_METADATA_KEY, JWTUtil.generateNewAccessToken(USER_ID).getData());
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void shouldReturnOneRecurringGeneratedEntry() throws Exception {
       
        TaskServiceGrpc.TaskServiceBlockingStub blockingStub = createBlockingStub();
        ActionResponse response =
                blockingStub.someAction(ActionRequest.getDefaultInstance());

        assertEquals(response.getResultData(0), "result data 1");
    }


    // Authenticate Tests
    @Test
    public void shouldSetCustomAndUserIdHeader() throws Exception {

        String cookieValue = Constants.CUSTOM_SLOT_HEADER_COOKIE_KEY + "=TEST1233";
        metadata = new Metadata();
        metadata.put(Constants.COOKIE_SLOT_METADATA_KEY, cookieValue);
        metadata.put(Constants.ACCESS_SLOT_METADATA_KEY, JWTUtil.generateNewAccessToken(USER_ID).getData());

        TaskServiceGrpc.TaskServiceBlockingStub blockingStub = createBlockingStub();

         ActionResponse response =
                blockingStub.someAction(ActionRequest.getDefaultInstance());

        assertEquals(response.getResultData(0), "result data 1");
    }

    @Test
    public void shouldThrow_missingAccesstoken() throws Exception {

        String cookieValue = Constants.CUSTOM_SLOT_HEADER_COOKIE_KEY + "=TEST1233";
        metadata = new Metadata();
        metadata.put(Constants.COOKIE_SLOT_METADATA_KEY, cookieValue);

        TaskServiceGrpc.TaskServiceBlockingStub blockingStub = createBlockingStub();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, 
            () -> blockingStub.someAction(ActionRequest.getDefaultInstance()));

     assertEquals(exception.getStatus().getDescription(), "Missing access token");
    }

    @Test
    public void shouldThrow_invalidAccesstoken() throws Exception {

        String cookieValue = Constants.CUSTOM_SLOT_HEADER_COOKIE_KEY + "=TEST1233";
        metadata = new Metadata();
        metadata.put(Constants.COOKIE_SLOT_METADATA_KEY, cookieValue);
        String accessToken = "fdsf";
        metadata.put(Constants.ACCESS_SLOT_METADATA_KEY, accessToken);

        TaskServiceGrpc.TaskServiceBlockingStub blockingStub = createBlockingStub();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, 
            () -> blockingStub.someAction(ActionRequest.getDefaultInstance()));

     assertEquals(exception.getStatus().getDescription(), "Invalid access token");
    }

    private TaskServiceGrpc.TaskServiceBlockingStub createBlockingStub() throws Exception{
        // Add the service to the in-process server.
         grpcCleanup.register(
            serverBuilder.addService(injector.getInstance(TaskServiceImpl.class)).intercept(new AuthenticatedInterceptor()).build().start());
        ManagedChannel channel = grpcCleanup.register(
            channelBuilder.maxInboundMessageSize(1024).build());

        return MetadataUtils.attachHeaders(TaskServiceGrpc.newBlockingStub(channel),metadata);

    }


}