package com.user.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import io.grpc.StatusRuntimeException;
import io.grpc.ManagedChannel;
import io.grpc.testing.GrpcCleanupRule;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.inprocess.InProcessChannelBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Guice;
import com.google.common.collect.Iterables;
import com.user.management.UserManagementServiceGrpc;
import com.user.UserProto.UserId;
import com.user.UserProto.UserAccessToken;
import com.user.UserProto.User;
import com.user.UserProto.UserRefreshToken;
import com.user.management.UserManagementServiceProto.SignInRequest;
import com.user.management.UserManagementServiceProto.SignInResponse;
import com.user.management.UserManagementServiceProto.RegenerateRefreshTokenRequest;
import com.user.management.UserManagementServiceProto.RegenerateRefreshTokenResponse;
import com.user.util.FakeSSOValidator;
import com.user.db.FakeUserDBHandler;
import com.util.FakeServiceModule;
import com.user.util.JWTUtil;
import com.user.management.UserManagementServiceImpl;

/**
 * User Management Service Test
 */
@RunWith(JUnit4.class)
public class UserManagementServiceTest {

    private final String SAMPLE_SSO_ID_TOKEN = "sample-id-token";
    private final UserId USER_ID = UserId.newBuilder().setId(11111).build();
    private final String SAMPLE_USER_EMAIL = "smaple@user.com";
    private final User SAMPLE_USER = 
        User.newBuilder()
            .setUserId(USER_ID)
            .setName("sample user")
            .setEmail(SAMPLE_USER_EMAIL)
            .build();
    private final User SAMPLE_USER_FROM_SSO = 
        User.newBuilder()
            .setName("sample user sso")
            .setEmail(SAMPLE_USER_EMAIL)
            .build();

    private Injector injector = Guice.createInjector(new FakeServiceModule());
    private String serverName = InProcessServerBuilder.generateName();
    private InProcessServerBuilder serverBuilder = InProcessServerBuilder
          .forName(serverName).directExecutor();
    private InProcessChannelBuilder channelBuilder = InProcessChannelBuilder
          .forName(serverName).directExecutor();

    @Inject private FakeUserDBHandler fakeUserDBHandler;
    @Inject private FakeSSOValidator fakeSSOValidator;

    @Rule
    public GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    
    @Before
    public void setup() {
        injector.injectMembers(this);
    }

    @After
    public void cleanUp() {
        fakeUserDBHandler.clear();
    }

    @Test
    public void shouldValidateExistingRefresh_generateAndReturnNewRefreshToken() throws Exception {
        
        UserRefreshToken existingRefreshToken = JWTUtil.generateNewRefreshToken(USER_ID);

        fakeUserDBHandler.setRefreshTokenForUserId(USER_ID, existingRefreshToken);

        UserManagementServiceGrpc.UserManagementServiceBlockingStub blockingStub = createBlockingStub();
        RegenerateRefreshTokenResponse response =
                blockingStub.regenerateRefreshToken(
                    RegenerateRefreshTokenRequest.newBuilder()
                        .setExistingRefreshToken(existingRefreshToken)
                    .build());
        // new on file refresh token should be same as returned 
        assertEquals(fakeUserDBHandler.getRefreshTokenForUserId(USER_ID), response.getRefreshToken());
        assertNotSame(response.getRefreshToken(), existingRefreshToken);
        validateAccessTokenToId(response.getAccessToken(), USER_ID.getId());
    }

    @Test
    public void shouldThrow_invalidRefreshToken_unparsable() throws Exception {
        
        UserRefreshToken existingRefreshToken = JWTUtil.generateNewRefreshToken(USER_ID);

        fakeUserDBHandler.setRefreshTokenForUserId(USER_ID, existingRefreshToken);

        UserManagementServiceGrpc.UserManagementServiceBlockingStub blockingStub = createBlockingStub();
        
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, 
            () -> blockingStub.regenerateRefreshToken(
                    RegenerateRefreshTokenRequest.newBuilder()
                        .setExistingRefreshToken(UserRefreshToken.newBuilder().setData("test").build())
                    .build()));
        assertEquals(exception.getStatus().getDescription(), "Invalid refresh token");

    }

    @Test
    public void shouldThrow_invalidRefreshToken_notOnFile() throws Exception {
        
        UserRefreshToken existingRefreshToken = JWTUtil.generateNewRefreshToken(USER_ID);
        //wait a second s.t. issuedAt date is different per token
        TimeUnit.SECONDS.sleep(1);
        UserRefreshToken anotherRefreshToken = JWTUtil.generateNewRefreshToken(USER_ID);

        fakeUserDBHandler.setRefreshTokenForUserId(USER_ID, existingRefreshToken);

        UserManagementServiceGrpc.UserManagementServiceBlockingStub blockingStub = createBlockingStub();
        
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, 
            () -> blockingStub.regenerateRefreshToken(
                    RegenerateRefreshTokenRequest.newBuilder()
                        .setExistingRefreshToken(anotherRefreshToken)
                    .build()));
        assertEquals(exception.getStatus().getDescription(), "Invalid refresh token");

    }

    // Sign in
    @Test
    public void shouldLogin_validGoogleIdToken_existingUser() throws Exception {
        fakeUserDBHandler.setEmailToUser(SAMPLE_USER_EMAIL, SAMPLE_USER);
        fakeSSOValidator.setUserToIdToken(SAMPLE_SSO_ID_TOKEN, SAMPLE_USER_FROM_SSO);

        UserManagementServiceGrpc.UserManagementServiceBlockingStub blockingStub = createBlockingStub();
        SignInResponse response =
                blockingStub.signIn(
                    SignInRequest.newBuilder()
                        .setIdToken(SAMPLE_SSO_ID_TOKEN)
                    .build());
        // new on file refresh token should be same as returned 
        assertEquals(fakeUserDBHandler.getRefreshTokenForUserId(USER_ID), response.getRefreshToken());
        validateAccessTokenToId(response.getAccessToken(), USER_ID.getId());
    }

    @Test 
    public void shouldLogin_validGoogleIdToken_newuser() throws Exception {
        fakeSSOValidator.setUserToIdToken(SAMPLE_SSO_ID_TOKEN, SAMPLE_USER_FROM_SSO);
        fakeUserDBHandler.setCreatedUserId(1099);

        UserManagementServiceGrpc.UserManagementServiceBlockingStub blockingStub = createBlockingStub();
        SignInResponse response =
                blockingStub.signIn(
                    SignInRequest.newBuilder()
                        .setIdToken(SAMPLE_SSO_ID_TOKEN)
                    .build());
        // new on file refresh token should be same as returned 
        assertEquals(fakeUserDBHandler.getRefreshTokenForUserId(UserId.newBuilder().setId(1099).build()), response.getRefreshToken());
        validateAccessTokenToId(response.getAccessToken(), 1099);
        assertEquals(fakeUserDBHandler.getCreatedUser().get(), SAMPLE_USER_FROM_SSO.toBuilder().setUserId(UserId.newBuilder().setId(1099).build()).build());
    }

    private void validateAccessTokenToId(UserAccessToken accessToken, int userId){
        assertEquals(JWTUtil.validateAccessToken(accessToken).get().getId(), userId);
    }

    private UserManagementServiceGrpc.UserManagementServiceBlockingStub createBlockingStub() throws Exception{
        // Add the service to the in-process server.
         grpcCleanup.register(
            serverBuilder.addService(injector.getInstance(UserManagementServiceImpl.class)).build().start());
        ManagedChannel channel = grpcCleanup.register(
            channelBuilder.maxInboundMessageSize(1024).build());

        return UserManagementServiceGrpc.newBlockingStub(channel);

    }
}
