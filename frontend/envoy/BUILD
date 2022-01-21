package(default_visibility = ["//visibility:public"])

load("@io_bazel_rules_docker//container:container.bzl", "container_image")

container_image(
    name = "envoy_image",
    base = "@envoy_base//image",
    files = [
      "envoy.yaml",
    ],
    cmd = " && ".join([
    "envoy -c ./envoy.yaml;"
  ]),
)