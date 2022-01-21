package com.user.db;

import java.util.Optional;
import java.util.List;
import com.google.common.util.concurrent.ListenableFuture;
import com.user.UserDBProto.CreateUserRequest;
import com.user.UserDBProto.CreateUserResponse;
import com.user.UserDBProto.DBFetchUserRequest;
import com.user.UserDBProto.DBFetchUserResponse;
import com.user.UserDBProto.UpdateRefreshTokenRequest;
import com.user.UserDBProto.UpdateRefreshTokenResponse;

/* 
	DB Util for all user related operations 
*/ 
public interface UserDBHandler {

	//fetch 
	public ListenableFuture<DBFetchUserResponse> fetchUser(DBFetchUserRequest fetchUserRequest);

	//update
	public ListenableFuture<UpdateRefreshTokenResponse> updateRefreshToken(UpdateRefreshTokenRequest updateRefreshTokenRequest);
	public ListenableFuture<CreateUserResponse> createUser(CreateUserRequest createUserRequest);
}
