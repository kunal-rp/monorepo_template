load("@rules_java//java:defs.bzl","java_library")

package(default_visibility = ["//visibility:public"])

java_library(
    name = "user_db_handler",
    srcs = ["UserDBHandler.java"],
    deps = [
        "@maven2//:com_google_guava_guava",
        "//proto/user:user_java_proto",
        "//proto/user:user_db_java_proto",
    ],
)

java_library(
    name = "fake_user_db_handler",
    srcs = ["FakeUserDBHandler.java"],
    deps = [
        "@maven2//:com_google_guava_guava",
        "@maven2//:com_google_inject_guice",
        ":user_db_handler",
        "//proto/user:user_java_proto",
        "//proto/user:user_db_java_proto",
    ],
)

java_library(
    name = "main_user_db_handler",
    srcs = ["MainUserDBHandler.java"],
    deps = [
        "@maven2//:com_google_guava_guava",
        "@maven2//:com_google_inject_guice",
        "@maven2//:org_mongodb_mongodb_driver_sync",
        "@maven2//:org_mongodb_bson",
        "@maven2//:org_mongodb_mongo_java_driver",
        "@maven2//:org_slf4j_slf4j_simple",
        ":user_db_handler",
        "//proto/user:user_java_proto",
        "//proto/user:user_db_java_proto",
        "//java/com/user/util:jwt_util"
    ],
)