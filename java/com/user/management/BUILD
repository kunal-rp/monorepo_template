load("@rules_java//java:defs.bzl", "java_library")
load("@io_bazel_rules_docker//java:image.bzl", "java_image")

package(default_visibility = ["//visibility:public"])


java_library(
    name = "user_management_service_impl",
    srcs = ["UserManagementServiceImpl.java"],
    deps = [
        "@maven2//:com_google_inject_guice",
        "@io_grpc_grpc_java//api",
        "@com_google_api_grpc_proto_google_common_protos//:com_google_api_grpc_proto_google_common_protos",
        "//proto/user:user_management_service_java_proto",
        "//java/com/user/db:user_db_handler",
        "//proto/user:user_db_java_proto",
        "//java/com/user/util:jwt_util",
        "//java/com/user/util:sso_validator"
    ],
)


java_image(
    name = "user_management_java_image",
     srcs = ["UserManagementService.java"],
     deps = [
        "@maven2//:com_google_inject_guice",
        "@io_grpc_grpc_java//api",
        ":user_management_service_impl",
        "//java/com/util:service_module",
        "//java/com/util:setup-util",
     ],
    main_class = "com.user.management.UserManagementService",
)