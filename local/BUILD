package(default_visibility = ["//visibility:public"])

load("@io_bazel_rules_docker//container:container.bzl", "container_image")


genrule(
  name = "init_files",
  srcs =  [
    ":init.js",
  ],
  cmd = " && ".join([
    "mkdir docker-entrypoint-initdb.d",
    "mv local/init.js docker-entrypoint-initdb.d",
    "mv ./docker-entrypoint-initdb.d $@",
  ]),
  outs = [
    "docker-entrypoint-initdb.d",
  ],
)

container_image(
    name = "db_local_image",
    base = "@mongo_base//image",
    files = [
      ":init_files"
    ],
)