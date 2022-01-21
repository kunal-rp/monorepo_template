package(default_visibility = ["//visibility:public"])

load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_java//java:defs.bzl", "java_proto_library")
load("@rules_proto_grpc//java:defs.bzl", "java_grpc_library")


proto_library(
    name = "user_proto",
    srcs = ["user.proto"],
)

java_proto_library(
    name = "user_java_proto",
    deps = [":user_proto"],
)

proto_library(
    name = "user_db_proto",
    srcs = ["db.proto"],
    deps = [":user_proto"]
)

java_proto_library(
    name = "user_db_java_proto",
    deps = [":user_db_proto"],
)


proto_library(
    name = "user_management_service_proto",
    srcs = ["user_management_service.proto"],
    deps = [":user_proto"]
)

java_grpc_library(
    name = "user_management_service_java_proto",
    protos = [
        ":user_management_service_proto",
        ":user_proto"
    ],
)