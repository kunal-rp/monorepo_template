load("@rules_java//java:defs.bzl", "java_library")
load("@io_bazel_rules_docker//java:image.bzl", "java_image")

package(default_visibility = ["//visibility:public"])


java_library(
    name = "constants",
    srcs = ["Constants.java"],
    deps = [
        "@io_grpc_grpc_java//context",
        "@io_grpc_grpc_java//api",
        "@com_google_api_grpc_proto_google_common_protos//:com_google_api_grpc_proto_google_common_protos",
        "//proto/user:user_java_proto",
    ],
)

java_library(
    name = "authenticated_interceptor",
    srcs = ["AuthenticatedInterceptor.java"],
    deps = [
        ":constants",
        "@io_grpc_grpc_java//api",
        "@io_grpc_grpc_java//context",
        "@com_google_api_grpc_proto_google_common_protos//:com_google_api_grpc_proto_google_common_protos",
        "@com_google_protobuf_javalite//:protobuf_javalite",
        "//java/com/user/util:jwt_util",
        "//proto/user:user_java_proto",
    ],
)

java_library(
    name = "task_service_impl",
    srcs = ["TaskServiceImpl.java"],
    deps = [
        "@maven2//:com_google_inject_guice",
        "@io_grpc_grpc_java//api",
        "@com_google_api_grpc_proto_google_common_protos//:com_google_api_grpc_proto_google_common_protos",
        ":constants",
        "//proto/task:task_service_java_proto",
    ],
)


java_image(
    name = "task_java_image",
     srcs = ["TaskService.java"],
     deps = [
        "@maven2//:com_google_inject_guice",
        "@io_grpc_grpc_java//api",
        ":task_service_impl",
        ":authenticated_interceptor",
        "//java/com/util:service_module",
        "//java/com/util:setup-util",
     ],
    main_class = "com.task.TaskService",
)