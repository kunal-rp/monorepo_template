package com.user.util;

import java.util.Collections;
import java.util.Optional;
import com.user.UserProto.User;
import com.user.UserProto.UserId;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class MainSSOValidator implements SSOValidator {

	@Override
	public Optional<User> validateGoogleIdToken(String idToken){

		try{
			GoogleIdTokenVerifier verifier = 
				new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
			    .setAudience(Collections.singletonList(System.getenv("GOOGLE_CLIENT_ID")))
			    .build();
			GoogleIdToken googleIdToken = verifier.verify(idToken);

			if (idToken != null) {
				Payload payload = googleIdToken.getPayload();

				System.out.println(payload);

			  return Optional.of(User.newBuilder()
			  		.setGoogleUserId((String) payload.getSubject())
			  		.setName((String) payload.get("name"))
			  		.setEmail(payload.getEmail())
			  		.setProfileUrl((String) payload.get("picture"))
			  		.setLocale((String) payload.get("locale"))
			  		.build());
			}
		}
		catch(Exception e){
		}
		return Optional.empty();
	}
}