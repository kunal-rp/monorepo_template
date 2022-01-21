package com.user.db;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
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
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.MongoException;
import org.bson.Document;
import org.bson.conversions.Bson;

public class MainUserDBHandler implements UserDBHandler {

	private final String COLLECTION_REFRESH = "refresh";
	private final String COLLECTION_USERS = "users";
	MongoClient mongoClient;

	public MainUserDBHandler(){
		mongoClient = MongoClients.create("mongodb://"+System.getenv("MONGODB_URI")+":80");

	}
 	
	@Override
	public ListenableFuture<DBFetchUserResponse> fetchUser(DBFetchUserRequest fetchUserRequest){

		System.out.println("fetchUser");
		System.out.println(fetchUserRequest);
		//TODO: add is-set checks for fetchable fields
		boolean isRefreshRequest = fetchUserRequest.getFieldList().contains(FetchableFields.USER_FIELD_REFRESH_TOKEN);		

		DBFetchUserResponse.Builder builder = DBFetchUserResponse.newBuilder();
		MongoCollection<Document> collection = getCollection(isRefreshRequest ? COLLECTION_REFRESH : COLLECTION_USERS);
		
		Document result;
		if(fetchUserRequest.hasUserId()){
			result = collection.find(eq("user_id", fetchUserRequest.getUserId().getId()))
                    .first();
		}else{
			result = collection.find(eq("email", fetchUserRequest.getEmail()))
                    .first();
		}

		System.out.println(result);

		if(result != null){
			if(isRefreshRequest){
				builder.setRefreshToken(UserRefreshToken.newBuilder().setData(result.get("token", String.class)).build());
			}else{
				builder.setUser(constructUser(result));
			}
		}
		
		return Futures.immediateFuture(builder.build());
	}

	@Override
	public ListenableFuture<UpdateRefreshTokenResponse> updateRefreshToken(UpdateRefreshTokenRequest updateRefreshTokenRequest){

		Document query = new Document().append("user_id", updateRefreshTokenRequest.getUserId().getId());
        Bson updates = Updates.combine(
                    Updates.set("token", updateRefreshTokenRequest.getNewRefreshToken().getData()));
        UpdateOptions options = new UpdateOptions().upsert(true);

         try {
           		getCollection(COLLECTION_REFRESH).updateOne(query, updates, options);
         		return Futures.immediateFuture(
					UpdateRefreshTokenResponse.newBuilder()
						.setNewRefreshToken(updateRefreshTokenRequest.getNewRefreshToken())
						.build());       
                
			} catch (MongoException me) {
                return Futures.immediateFuture(UpdateRefreshTokenResponse.getDefaultInstance());
        }
	}

	@Override
	public ListenableFuture<CreateUserResponse> createUser(CreateUserRequest createUserRequest){

		 try {

		 		MongoCollection<Document> collection = getCollection(COLLECTION_USERS);
		 		MongoCursor<Document> result = collection.find().sort(ascending("user_id")).limit(1).iterator();
		 		System.out.println("createUser");

		 		int createdUserId = (result.hasNext() ? result.next().get("user_id",Double.class).intValue() + 1 : 101);

		 		System.out.println(createdUserId);

                getCollection(COLLECTION_USERS).insertOne(
                	new Document()
                        .append("user_id", createdUserId)
                        .append("name", createUserRequest.getUser().getName())
                        .append("email", createUserRequest.getUser().getEmail())
                		.append("google_user_id", createUserRequest.getUser().getGoogleUserId()));
                return Futures.immediateFuture(CreateUserResponse.newBuilder().setCreatedUserId(UserId.newBuilder().setId(createdUserId).build()).build());
            } catch (Exception e) {
            	System.out.println(e);
            	return Futures.immediateFuture(CreateUserResponse.newBuilder().setError(CreateUserResponse.Error.CREATION_ERROR_UNKOWN).build());
            }

	}

	private User constructUser(Document result){
		return User.newBuilder()
			.setUserId(
				UserId.newBuilder()
				.setId(result.get("user_id",Integer.class))
				.build())
			.setName(result.get("name", String.class))
			.setEmail(result.get("email", String.class))
			.build();
	}

	private MongoCollection<Document> getCollection(String collection){
		MongoDatabase database = mongoClient.getDatabase(System.getenv("MONGODB_DB"));
        return database.getCollection(collection);
	}
	

}
