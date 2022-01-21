package(default_visibility = ["//visibility:public"])

load("@rules_proto//proto:defs.bzl", "proto_library")
load("@rules_proto_grpc//java:defs.bzl", "java_grpc_library")


proto_library(
    name = "task_service_proto",
    srcs = ["task_service.proto"],
)

java_grpc_library(
    name = "task_service_java_proto",
    protos = [
        ":task_service_proto",
    ],
)
