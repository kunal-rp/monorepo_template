package com.task;

import io.grpc.Metadata;
import io.grpc.Context;
import com.user.UserProto.UserId;

public class Constants {

      // Metadata
	public static final Metadata.Key<String> COOKIE_SLOT_METADATA_KEY =
      Metadata.Key.of("cookie", Metadata.ASCII_STRING_MARSHALLER);

      public static final Metadata.Key<String> ACCESS_SLOT_METADATA_KEY =
      Metadata.Key.of("slot-a-tkn", Metadata.ASCII_STRING_MARSHALLER);

      public static final String CUSTOM_SLOT_HEADER_COOKIE_KEY = "custom_slot_header";

      // Context
	public static final Context.Key<String> CUSTOM_HEADER_CTX_KEY = Context.key("custom_slot");
      public static final Context.Key<UserId> USER_ID_CTX_KEY = Context.key("slot_user_id");
}

