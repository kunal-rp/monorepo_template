load("//:java/test/java_testing_generator.bzl", "java_unit_test")

package(default_visibility = ["//visibility:public"])

java_unit_test(
    name = "task_tests",
    pkg="com.task",
    srcs = glob(["*.java"]),
    deps = [
    	"@io_grpc_grpc_java//api",
        "@io_grpc_grpc_java//core:inprocess",
        "@io_grpc_grpc_java//stub",
        "@maven2//:io_grpc_grpc_testing",
        "@maven2//:com_google_inject_guice",
        "@maven2//:org_junit_jupiter_junit_jupiter_api",
        "//proto/task:task_service_java_proto",
        "//proto/user:user_java_proto",
        "//java/com/task:task_service_impl",
        "//java/com/task:authenticated_interceptor",
        "//java/com/task:constants",
        "//java/com/util:fake_service_module",
        "//java/com/user/util:jwt_util",
    ],

)
