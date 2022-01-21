package(default_visibility = ["//visibility:public"])


java_library(
    name = "jwt_util",
    srcs = ["JWTUtil.java"],
    deps = [
        "@jjwt//:io_jsonwebtoken_jjwt",
        "@jjwt//:javax_xml_bind_jaxb_api",
        "//proto/user:user_java_proto",
    ]
)

java_library(
    name = "sso_validator",
    srcs = ["SSOValidator.java"],
    deps = [
        "@maven2//:com_google_api_client_google_api_client",
        "//proto/user:user_java_proto",
    ]
)

java_library(
    name = "main_sso_validator",
    srcs = ["MainSSOValidator.java"],
    deps = [
        ":sso_validator",
        "@maven2//:com_google_api_client_google_api_client",
        "@maven2//:com_google_http_client_google_http_client",
        "@maven2//:com_google_http_client_google_http_client_gson",
        "//proto/user:user_java_proto",
    ]
)

java_library(
    name = "fake_sso_validator",
    srcs = ["FakeSSOValidator.java"],
    deps = [
        ":sso_validator",
        "@maven2//:com_google_api_client_google_api_client",
        "@maven2//:com_google_inject_guice",
        "//proto/user:user_java_proto",
    ]
)