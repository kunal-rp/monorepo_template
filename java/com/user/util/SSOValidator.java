package com.user.util;

import java.util.Optional;
import com.user.UserProto.User;

public interface SSOValidator {

	public Optional<User> validateGoogleIdToken(String idToken);
}