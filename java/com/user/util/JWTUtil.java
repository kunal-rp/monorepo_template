package com.user.util;

import java.util.Optional;
import java.util.Date;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.user.UserProto.UserId;
import com.user.UserProto.UserRefreshToken;
import com.user.UserProto.UserAccessToken;

public class JWTUtil{

	public final static String REFRESH_SIGNING_KEY = "refresh_slot_secret_value";
	public final static String ACCESS_SIGNING_KEY = "access_slot_secret_value";

	public static Optional<UserId> validateRefreshToken(UserRefreshToken jwtToken){
		return parseJWTForUserId(jwtToken.getData(), REFRESH_SIGNING_KEY);
	}

	public static Optional<UserId> validateAccessToken(UserAccessToken accessToken){
		return parseJWTForUserId(accessToken.getData(), ACCESS_SIGNING_KEY);
	}

	public static UserRefreshToken generateNewRefreshToken(UserId userId){
		return UserRefreshToken.newBuilder().setData(
				Jwts.builder()
	            .setSubject(String.valueOf(userId.getId()))
	            .setIssuedAt(new Date())
	            .signWith(SignatureAlgorithm.HS256, REFRESH_SIGNING_KEY)
	            .compact()
            ).build();
	}

	public static UserAccessToken generateNewAccessToken(UserId userId){	
		return UserAccessToken.newBuilder().setData(
				Jwts.builder()
	            .setSubject(String.valueOf(userId.getId()))
	            .setIssuedAt(new Date())
	            .signWith(SignatureAlgorithm.HS256, ACCESS_SIGNING_KEY)
	            .compact()
            ).build();
	}

	private static Optional<UserId> parseJWTForUserId(String token, String secret){
		JwtParser parser = Jwts.parser().setSigningKey(secret);
		Optional<UserId> userId = Optional.empty();

		try {
            Jws<Claims> claims = parser.parseClaimsJws(token.trim());
            userId = Optional.of(
				UserId.newBuilder().setId(
					Integer.parseInt(claims.getBody().getSubject())
				).build());
        } catch (Exception e) {}

        return userId;

	}
}