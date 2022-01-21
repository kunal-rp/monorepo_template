package com.user.db;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.user.UserDBProto.CreateUserRequest;
import com.user.UserDBProto.CreateUserResponse;
import com.user.UserDBProto.DBFetchUserRequest;
import com.user.UserDBProto.DBFetchUserRequest.FetchableFields;
import com.user.UserDBProto.DBFetchUserResponse;
import com.user.UserProto.UserRefreshToken;
import com.user.UserProto.UserId;
import com.user.UserProto.User;
import com.user.UserDBProto.UpdateRefreshTokenRequest;
import com.user.UserDBProto.UpdateRefreshTokenResponse;
import com.google.inject.Singleton;

@Singleton
public class FakeUserDBHandler implements UserDBHandler {

	private int DEFAULT_CREATED_USER_ID = 999;

	private Map<UserId, UserRefreshToken> refreshTokens = new HashMap<UserId, UserRefreshToken>();
	private Map<String, User> emailToUser = new HashMap<String, User>();
	private Optional<User> createdUser = Optional.empty();
	private int createdUserId = DEFAULT_CREATED_USER_ID;

	@Override
	public ListenableFuture<DBFetchUserResponse> fetchUser(DBFetchUserRequest fetchUserRequest){

		DBFetchUserResponse.Builder response  = DBFetchUserResponse.newBuilder();

		if(refreshTokens.containsKey(fetchUserRequest.getUserId()) && fetchUserRequest.getFieldList().contains(FetchableFields.USER_FIELD_REFRESH_TOKEN)){
			response.setRefreshToken(refreshTokens.get(fetchUserRequest.getUserId()));			
		}else if(emailToUser.containsKey(fetchUserRequest.getEmail()) && fetchUserRequest.getFieldList().contains(FetchableFields.USER_FIELD_USER)){
			response.setUser(emailToUser.get(fetchUserRequest.getEmail()));
		}

		return Futures.immediateFuture(response.build());
	}

	@Override
	public ListenableFuture<UpdateRefreshTokenResponse> updateRefreshToken(UpdateRefreshTokenRequest updateRefreshTokenRequest){
		System.out.println("fake updateRefreshToken");
		System.out.println(updateRefreshTokenRequest);
		refreshTokens.put(updateRefreshTokenRequest.getUserId(), updateRefreshTokenRequest.getNewRefreshToken());

		return Futures.immediateFuture(
			UpdateRefreshTokenResponse.newBuilder()
				.setNewRefreshToken(updateRefreshTokenRequest.getNewRefreshToken())
				.build());

	}

	@Override
	public ListenableFuture<CreateUserResponse> createUser(CreateUserRequest createUserRequest){
		UserId newUserId = UserId.newBuilder()
					.setId(createdUserId).build();
		createdUser = 
			Optional.of(createUserRequest.getUser().toBuilder()
				.setUserId(newUserId).build());
		return Futures.immediateFuture(CreateUserResponse.newBuilder().setCreatedUserId(newUserId).build());
	}
	

	public void clear(){
		refreshTokens = new HashMap<UserId, UserRefreshToken>();
		emailToUser = new HashMap<String, User>();
		createdUser = Optional.empty();
		createdUserId = DEFAULT_CREATED_USER_ID;
	}

	public void setEmailToUser(String email, User user){
		emailToUser.put(email, user);
	}

	public void setRefreshTokenForUserId(UserId userId, UserRefreshToken refreshToken){
		refreshTokens.put(userId, refreshToken);
	}

	public UserRefreshToken getRefreshTokenForUserId(UserId userId){
		return refreshTokens.get(userId);
	}

	public Optional<User> getCreatedUser(){
		return createdUser;
	}

	public void setCreatedUserId(int createdUserId){
		this.createdUserId = createdUserId;
	}
}
