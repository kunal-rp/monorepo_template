package com.user.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import com.user.UserProto.User;
import com.google.inject.Singleton;

@Singleton
public class FakeSSOValidator implements SSOValidator {

	private Map<String, User> tokenIdMap = new HashMap<String, User>();

	@Override
	public Optional<User> validateGoogleIdToken(String idToken){
		if(tokenIdMap.containsKey(idToken)){
			return Optional.of(tokenIdMap.get(idToken));
		}
		return Optional.empty();
	}

	public void clear(){
		tokenIdMap = new HashMap<String, User>();
	}

	public void setUserToIdToken(String idToken, User user){
		tokenIdMap.put(idToken, user);
	}
}
