package(default_visibility = ["//visibility:public"])

load("@rules_proto_grpc//js:defs.bzl", "js_grpc_node_library","js_proto_library")
load("@io_bazel_rules_docker//nodejs:image.bzl", "nodejs_image")

js_grpc_node_library(
    name = "task_service_proto",
    protos = [
        "//proto/task:task_service_proto",
        "//proto/user:user_proto",
    ],
    deps_repo = "@nodejs_modules"
)

js_grpc_node_library(
    name = "user_management_service_proto",
    protos = [
        "//proto/user:user_management_service_proto",
        "//proto/user:user_proto"
    ],
    deps_repo = "@nodejs_modules"
)

js_proto_library(
    name = "user_js_proto",
    protos = ["//proto/user:user_proto"],
    deps_repo = "@nodejs_modules"
)


nodejs_image(
    name = "nodejs_gateway_image",
     entry_point = "gateway.js",
     data = [
      "@nodejs_modules//express",
      "@nodejs_modules//cookie-parser",
      "@nodejs_modules//@grpc/grpc-js",
      "@nodejs_modules//body-parser",
      ":task_service_proto",
      ":user_management_service_proto",
      ":user_js_proto",
    ],
)
