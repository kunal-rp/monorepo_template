package com.task;

import io.grpc.Status;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import java.util.Arrays;
import com.user.UserProto.UserId;
import com.user.UserProto.UserAccessToken;
import com.user.util.JWTUtil;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * A interceptor to authenticate protected paths.
 */
public class AuthenticatedInterceptor implements ServerInterceptor {

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call,
      final Metadata requestHeaders,
      ServerCallHandler<ReqT, RespT> next) {

          Context ctx = Context.current();

          // parse cookie for custom value 
      		String cookieValue = requestHeaders.get(Constants.COOKIE_SLOT_METADATA_KEY);
          if(cookieValue == null){
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid cookie"));
          }
          Optional<String> customHeaderValue = 
            Arrays.asList(cookieValue.split(";"))
              .stream()
              .filter(cookie -> cookie.split("=")[0].trim().equals(Constants.CUSTOM_SLOT_HEADER_COOKIE_KEY)).findAny();

          if(customHeaderValue.isPresent()){            
              ctx = ctx.withValue(Constants.CUSTOM_HEADER_CTX_KEY, customHeaderValue.get().split("=")[1].trim());
          }

          System.out.println("intercept");
          System.out.println(requestHeaders.toString());

          //parse metadata for auth header
          String accessTokenData = requestHeaders.get(Constants.ACCESS_SLOT_METADATA_KEY);

          if(accessTokenData == null){
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Missing access token"));
          }

          Optional<UserId> userId = JWTUtil.validateAccessToken(UserAccessToken.newBuilder().setData(accessTokenData).build());
          if(!userId.isPresent()){
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid access token"));
          }

          ctx = ctx.withValue(Constants.USER_ID_CTX_KEY, userId.get());
          return Contexts.interceptCall(ctx, call, requestHeaders, next);
  }

}