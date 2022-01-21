package(default_visibility = ["//visibility:public"])

load("@frapp_modules//webpack:index.bzl", "webpack")
load("@frapp_modules//http_server:index.bzl", "http_server")
load("@io_bazel_rules_docker//nodejs:image.bzl", "nodejs_image")
load("@build_bazel_rules_nodejs//:index.bzl", "nodejs_binary")
load("@io_bazel_rules_docker//container:container.bzl", "container_image")
load("@rules_proto_grpc//js:defs.bzl", "js_grpc_web_library")

NPM_PACKAGES = [
    "@frapp_modules//:node_modules",
]

js_grpc_web_library(
  name = "task_grpc_js",
  protos = [
    "//proto/task:task_service_proto",
    "//proto/user:user_proto"
  ],
  deps_repo = "@frapp_modules",
)

filegroup(
    name = "srcs",
    srcs = glob(["src/**"], exclude = [ "src/genProto/**" ] ) + glob(["assets/**"])
)


nodejs_binary(
    name = "webpack_cli",
    data = NPM_PACKAGES,
    entry_point = "@frapp_modules//:node_modules/webpack-cli/bin/cli.js",
)

genrule(
  name = "prod_build",
  tools = [":webpack_cli"],
  srcs = NPM_PACKAGES + [
    ":srcs",
    "src/index.html",
    "webpack.prod.js",
    ".babelrc",
    ":task_grpc_js"
  ],
  cmd = " && ".join([
    "cp -a bazel-out/darwin-fastbuild/bin/frontend/app/task_grpc_js_pb/ frontend/app/src/",
    #"ls -R frontend | grep ':$$' | sed -e 's/:$$//' -e 's/[^-][^\/]*\//--/g' -e 's/^/   /' -e 's/-/|/'",
    "$(execpath webpack_cli) --mode=production --config=$(location webpack.prod.js)",
    "mv frontend/app/dist $@",
  ]),
  outs = [
    "dist",
  ],
)

genrule(
  name = "dev_build",
  tools = [":webpack_cli"],
  srcs = NPM_PACKAGES + [
    ":srcs",
    "src/index.html",
    "webpack.dev.js",
    ".babelrc",
    ":task_grpc_js",
  ],
  cmd = " && ".join([
    "cp -a bazel-out/darwin-fastbuild/bin/frontend/app/task_grpc_js_pb/ frontend/app/src/",
    "$(execpath webpack_cli) --mode=development --config=$(location webpack.dev.js)",
    "mv frontend/app/devDist $@",
  ]),
  outs = [
    "devDist",
  ],
)

http_server(
    name = "prod_server",
    data = [
        ":prod_build"
    ],
    # args = ["frontend/app/dist/"],
)

http_server(
    name = "dev_server",
    data = [
        ":dev_build"
    ],
    args = ["frontend/app/devDist"],
)


container_image(
    name = "dev_image",
    base = "@nginx_base//image",
    files = [
      ":dev_build",
      "nginxDev.conf",
      "entry.sh",
    ],
    cmd = "".join([
      "chmod +x ./entry.sh;",
      "./entry.sh;",
      "nginx -c /nginxDev.conf;"
    ]),
)

container_image(
    name = "prod_image",
    base = "@nginx_base//image",
    files = [
      ":prod_build",
      "nginx.conf",
      "entry.sh",
    ],
    cmd = "".join([
      "chmod +x ./entry.sh;",
      "./entry.sh;",
      "nginx -c /nginx.conf;"
    ]),
)
