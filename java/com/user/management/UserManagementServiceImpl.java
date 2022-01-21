package com.user.management;

import java.util.Optional;
import java.util.concurrent.Executor;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import io.grpc.stub.StreamObserver;
import io.grpc.protobuf.StatusProto;
import com.google.rpc.Status;
import com.google.rpc.Code;
import com.user.UserDBProto.DBFetchUserRequest;
import com.user.UserDBProto.DBFetchUserResponse;
import com.user.db.UserDBHandler;
import com.user.UserProto.UserId;
import com.user.UserProto.User;
import com.user.UserProto.UserRefreshToken;
import com.user.util.JWTUtil;
import com.user.util.SSOValidator;
import com.user.UserDBProto.UpdateRefreshTokenRequest;
import com.user.UserDBProto.UpdateRefreshTokenResponse;
import com.user.UserDBProto.CreateUserRequest;
import com.user.UserDBProto.CreateUserResponse;
import com.user.management.UserManagementServiceProto.SignInRequest;
import com.user.management.UserManagementServiceProto.SignInResponse;
import com.user.management.UserManagementServiceProto.RegenerateRefreshTokenRequest;
import com.user.management.UserManagementServiceProto.RegenerateRefreshTokenResponse;


public class UserManagementServiceImpl extends UserManagementServiceGrpc.UserManagementServiceImplBase {

    private UserDBHandler userDBHandler;
    private SSOValidator ssoValidator;

    @Inject
    public UserManagementServiceImpl(
        UserDBHandler userDBHandler,
        SSOValidator ssoValidator){
        this.userDBHandler = userDBHandler;
        this.ssoValidator = ssoValidator;
    }

    @Override
    public void regenerateRefreshToken(RegenerateRefreshTokenRequest req, StreamObserver<RegenerateRefreshTokenResponse> responseObserver) {

        Executor executor = MoreExecutors.newDirectExecutorService();

        if(!req.hasExistingRefreshToken()){
            invalidJwt(responseObserver);
        }else {
            UserRefreshToken existingRefreshToken = req.getExistingRefreshToken();
            Optional<UserRefreshToken> updateRefreshToken = 
                internalRegenerateRefreshToken(
                    existingRefreshToken,
                    responseObserver, 
                    executor);
            if(updateRefreshToken.isPresent()){
                responseObserver.onNext(
                    RegenerateRefreshTokenResponse.newBuilder()
                        .setRefreshToken(
                            updateRefreshToken.get())
                        .setAccessToken(
                            JWTUtil.generateNewAccessToken(
                                JWTUtil.validateRefreshToken(existingRefreshToken).get()
                            ))
                        .build());
                responseObserver.onCompleted();
                }
        }
    }

    @Override 
    public void signIn(SignInRequest req, StreamObserver<SignInResponse> responseObserver) {

        System.out.println("sign in ");
        System.out.println(req);
        Executor executor = MoreExecutors.newDirectExecutorService();

        /* 
            1) validate id token passed 
            2) fetch user for email
                - if new, create new user
            3) gen refresh/access tokens 
        */

        Optional<User> userFromIdToken = ssoValidator.validateGoogleIdToken(req.getIdToken());
        System.out.println(userFromIdToken);
        if(userFromIdToken.isPresent()){

            try{
                ListenableFuture<DBFetchUserResponse> dbFetchUserFuture = 
                userDBHandler.fetchUser(
                    DBFetchUserRequest.newBuilder()
                        .setEmail(userFromIdToken.get().getEmail())
                        .addField(DBFetchUserRequest.FetchableFields.USER_FIELD_USER)
                        .build());

                PotentialUserCreationResult potentialUserCreationResult = createNewUserIfNeededWithRefreshToken(
                        dbFetchUserFuture, 
                        userFromIdToken.get(), 
                        executor).get();

                System.out.println("PotentialUserCreationResult");
                System.out.println(potentialUserCreationResult.getUserId());
                System.out.println(potentialUserCreationResult.getUserRefreshTokenFuture().get());

                responseObserver.onNext(
                    SignInResponse.newBuilder()
                        .setRefreshToken(
                                potentialUserCreationResult.getUserRefreshTokenFuture().get())
                            .setAccessToken(
                                JWTUtil.generateNewAccessToken(
                                    potentialUserCreationResult.getUserId()
                                ))
                            .build());
                responseObserver.onCompleted();

            }catch(Exception e){
            Status status = Status.newBuilder()
                    .setCode(Code.UNKNOWN.getNumber())
                    .setMessage("something bad happened - pending")
                    .build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));

        }

        }else{
            // TODO: invalid id token throw
        }

    }

    private Optional<UserRefreshToken> internalRegenerateRefreshToken(
        UserRefreshToken existingRefreshToken,
        StreamObserver streamObserver,
        Executor executor ){

        /*
            1) validate refresh token 
            2) retrieve user id from token 
            3) fetch stored token and compare tokens 
            4) generate new token, store in db and return 
            */ 
            Optional<UserId> userId = JWTUtil.validateRefreshToken(existingRefreshToken);

            if(!userId.isPresent()){
                invalidJwt(streamObserver);
            }else{
                try{
                    Optional<ListenableFuture<UpdateRefreshTokenResponse>> updateRefreshTokenFuture = 
                        verifyRefreshTokenAndUpdate(
                            existingRefreshToken, 
                            userId.get(), 
                            executor);

                    if(!updateRefreshTokenFuture.isPresent()){
                        invalidJwt(streamObserver);
                    }else{
                       return Optional.of(updateRefreshTokenFuture.get().get().getNewRefreshToken());
                    }
                } catch(Exception e){
                    Status status = Status.newBuilder()
                        .setCode(Code.UNKNOWN.getNumber())
                        .setMessage("something bad happened - pending")
                        .build();
                    streamObserver.onError(StatusProto.toStatusRuntimeException(status));
                }
            }

        return Optional.empty();
    }

    private Optional<ListenableFuture<UpdateRefreshTokenResponse>> verifyRefreshTokenAndUpdate(
            UserRefreshToken existingRefreshToken,
            UserId userId,
            Executor executor) throws Exception{

        ListenableFuture<DBFetchUserResponse> dbFetchUserFuture = userDBHandler.fetchUser(
            DBFetchUserRequest.newBuilder()
                .setUserId(userId)
                .addField(DBFetchUserRequest.FetchableFields.USER_FIELD_REFRESH_TOKEN)
                .build());

         return validateAndSetNewRefreshToken(
                dbFetchUserFuture,
                existingRefreshToken,
                userId, 
                executor).get();

    }

     private ListenableFuture<Optional<ListenableFuture<UpdateRefreshTokenResponse>>> validateAndSetNewRefreshToken(
            ListenableFuture<DBFetchUserResponse> dbFetchUserFuture,
            UserRefreshToken existingRefreshToken,
            UserId userId,
            Executor executor){
        AsyncFunction<DBFetchUserResponse, Optional<ListenableFuture<UpdateRefreshTokenResponse>>> setNewTokenFunction =
          new AsyncFunction<DBFetchUserResponse, Optional<ListenableFuture<UpdateRefreshTokenResponse>>>() {
            public ListenableFuture<Optional<ListenableFuture<UpdateRefreshTokenResponse>>> apply(DBFetchUserResponse fetchUserResponse) {
                 Optional<ListenableFuture<UpdateRefreshTokenResponse>> response = Optional.empty();
                 if(fetchUserResponse.getRefreshToken().equals(existingRefreshToken)){
                        response = Optional.of(updateRefreshTokenFuture(userId));
                    }
                return Futures.immediateFuture(response);
                }
          };
        return  Futures.transformAsync(dbFetchUserFuture, setNewTokenFunction, executor);
    }

    final class PotentialUserCreationResult{
        ListenableFuture<UserRefreshToken> userRefreshTokenFuture;
        UserId userId;

        public PotentialUserCreationResult(ListenableFuture<UserRefreshToken> userRefreshTokenFuture,UserId userId ){
            this.userRefreshTokenFuture = userRefreshTokenFuture;
            this.userId = userId;
        }

        public ListenableFuture<UserRefreshToken> getUserRefreshTokenFuture(){
            return userRefreshTokenFuture;
        }

        public UserId getUserId(){
            return userId;
        }

    }

    private ListenableFuture<PotentialUserCreationResult> createNewUserIfNeededWithRefreshToken(
            ListenableFuture<DBFetchUserResponse> dbFetchUserFuture,
            User userFromIdToken, 
            Executor executor){

        System.out.println("createNewUserIfNeededWithRefreshToken");

        AsyncFunction<UpdateRefreshTokenResponse,UserRefreshToken> handleUpdateRefreshTokenFunction =
          new AsyncFunction<UpdateRefreshTokenResponse, UserRefreshToken>() {
            public ListenableFuture<UserRefreshToken> apply(UpdateRefreshTokenResponse updateRefreshTokenResponse) {
                return Futures.immediateFuture(updateRefreshTokenResponse.getNewRefreshToken());
                }
          };

        AsyncFunction<CreateUserResponse,PotentialUserCreationResult> handleCreateUserFunction =
          new AsyncFunction<CreateUserResponse, PotentialUserCreationResult>() {
            public ListenableFuture<PotentialUserCreationResult> apply(CreateUserResponse createUserResponse) {
                System.out.println("handleCreateUserFunction");
                System.out.println(createUserResponse);
                // TODO: handle errors in creation
                return Futures.immediateFuture(new PotentialUserCreationResult(
                    Futures.transformAsync(
                        updateRefreshTokenFuture(createUserResponse.getCreatedUserId()), handleUpdateRefreshTokenFunction, executor),
                    createUserResponse.getCreatedUserId()));
                }
          };

        AsyncFunction<DBFetchUserResponse,PotentialUserCreationResult> handleFetchUserResponseFunction =
          new AsyncFunction<DBFetchUserResponse, PotentialUserCreationResult>() {
            public ListenableFuture<PotentialUserCreationResult> apply(DBFetchUserResponse fetchUserResponse) {
                System.out.println("fetchUserResponse");
                System.out.println(fetchUserResponse);
                if(fetchUserResponse.hasUser()){
                    // update refresh token for existing user
                    return Futures.immediateFuture(
                        new PotentialUserCreationResult(
                            Futures.transformAsync(
                                updateRefreshTokenFuture(fetchUserResponse.getUser().getUserId()), handleUpdateRefreshTokenFunction, executor), 
                            fetchUserResponse.getUser().getUserId()));
                }else{
                    return Futures.transformAsync(
                        createUser(userFromIdToken), handleCreateUserFunction, executor);
                }
            }
        };
        return Futures.transformAsync(dbFetchUserFuture, handleFetchUserResponseFunction, executor);
    }

    private ListenableFuture<UpdateRefreshTokenResponse> updateRefreshTokenFuture(UserId userId){
        return userDBHandler.updateRefreshToken(
                    UpdateRefreshTokenRequest.newBuilder()
                        .setUserId(userId)
                        .setNewRefreshToken(JWTUtil.generateNewRefreshToken(userId))
                        .build());

    }

    private ListenableFuture<CreateUserResponse> createUser(User user){
        return userDBHandler.createUser(
                    CreateUserRequest.newBuilder()
                        .setUser(user)
                        .build());

    }

    private void invalidJwt(StreamObserver<RegenerateRefreshTokenResponse> responseObserver){
        Status status = Status.newBuilder()
                    .setCode(Code.INVALID_ARGUMENT.getNumber())
                    .setMessage("Invalid refresh token")
                    .build();

        responseObserver.onError(StatusProto.toStatusRuntimeException(status));
    }



}