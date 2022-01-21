load("@rules_java//java:defs.bzl", "java_library")

package(default_visibility = ["//visibility:public"])

# The java_binary rule, like many others, has multiple outputs. We'll
# see that in action shortly.

java_library(
    name = "setup-util",
    srcs = ["SetupUtil.java"],
    deps = [
        "@maven//:com_google_guava_guava",
    ],
)

java_library(
    name = "service_module",
    srcs = ["ServiceModule.java"],
    deps = [
    	"@maven2//:com_google_inject_guice",
        "@maven2//:com_google_guava_guava",
        "//java/com/user/db:user_db_handler",
        "//java/com/user/db:main_user_db_handler",
        "//java/com/user/util:sso_validator",
        "//java/com/user/util:main_sso_validator",
    ],
)

java_library(
    name = "fake_service_module",
    srcs = ["FakeServiceModule.java"],
    deps = [
        "@maven2//:com_google_inject_guice",
        "@maven2//:com_google_guava_guava",
        "//java/com/user/db:user_db_handler",
        "//java/com/user/db:fake_user_db_handler",
        "//java/com/user/util:sso_validator",
        "//java/com/user/util:fake_sso_validator",
    ],
)
