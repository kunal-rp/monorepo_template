workspace(name = "slot" ,
    # Map the @npm bazel workspace to the node_modules directory.
    # This lets Bazel use the same node_modules as other local tooling.
    managed_directories = {
        "@nodejs_modules": ["nodejs/node_modules"],
        "@frapp_modules": ["frontend/app/node_modules"],
        "@frapp_next_modules": ["frontend/next_app/node_modules"],
        })

# functions to get external libs
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
http_archive(
    name = "rules_java",
    url = "https://github.com/bazelbuild/rules_java/releases/download/4.0.0/rules_java-4.0.0.tar.gz",
    sha256 = "34b41ec683e67253043ab1a3d1e8b7c61e4e8edefbcad485381328c934d072fe",
)

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()
rules_java_toolchains()


# proto - grpc service ( should also get proto_rules dep )
http_archive(
    name = "rules_proto_grpc",
    sha256 = "8383116d4c505e93fd58369841814acc3f25bdb906887a2023980d8f49a0b95b",
    strip_prefix = "rules_proto_grpc-4.1.0",
    urls = ["https://github.com/rules-proto-grpc/rules_proto_grpc/archive/4.1.0.tar.gz"],
)

load("@rules_proto_grpc//:repositories.bzl", "rules_proto_grpc_toolchains", "rules_proto_grpc_repos")
rules_proto_grpc_toolchains()
rules_proto_grpc_repos()

load("@rules_proto_grpc//java:repositories.bzl", rules_proto_grpc_java_repos="java_repos")
rules_proto_grpc_java_repos()

load("@io_grpc_grpc_java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_ARTIFACTS", "IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS", "grpc_java_repositories")

grpc_java_repositories()

# maven_install
RULES_JVM_EXTERNAL_TAG = "4.0"
RULES_JVM_EXTERNAL_SHA = "31701ad93dbfe544d597dbe62c9a1fdd76d81d8a9150c2bf1ecf928ecdf97169"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")


maven_install(
    artifacts = IO_GRPC_GRPC_JAVA_ARTIFACTS,
    generate_compat_repositories = True,
    override_targets = IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS,
    repositories = [
        "https://repo.maven.apache.org/maven2/",
    ],
)

load("@maven//:compat.bzl", "compat_repositories")

compat_repositories()

#guice
#grpc testing
grpc_version = "1.27.0"
guice_version = "4.1.0"
maven_install(
    name = "maven2",
    artifacts = [
        "io.grpc:grpc-testing:%s" % grpc_version,
        "com.google.guava:guava:30.0-jre",
        'com.google.inject:guice:' + guice_version,
        'org.junit.jupiter:junit-jupiter-api:5.8.1',
        "com.google.api-client:google-api-client:1.32.2",
        "org.mongodb:mongodb-driver-sync:4.4.0",
        "org.mongodb:bson:4.4.0",
        "org.mongodb:mongo-java-driver:3.12.7",
        "org.slf4j:slf4j-simple:1.7.32",
    ],
    repositories = [
        "https://jcenter.bintray.com/",
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
)

## rules_docker
http_archive(
    name = "io_bazel_rules_docker",
    sha256 = "59536e6ae64359b716ba9c46c39183403b01eabfbd57578e84398b4829ca499a",
    strip_prefix = "rules_docker-0.22.0",
    urls = ["https://github.com/bazelbuild/rules_docker/releases/download/v0.22.0/rules_docker-v0.22.0.tar.gz"],
)
load(
    "@io_bazel_rules_docker//repositories:repositories.bzl",
    container_repositories = "repositories",
)
container_repositories()
load("@io_bazel_rules_docker//repositories:deps.bzl", container_deps = "deps")
container_deps()

# Load java_image rules to create java docker images to run grpc services
load(
    "@io_bazel_rules_docker//java:image.bzl",
    _java_image_repos = "repositories",
)
_java_image_repos()

# Load the macro that allows you to customize the docker toolchain configuration.
load("@io_bazel_rules_docker//toolchains/docker:toolchain.bzl",
    docker_toolchain_configure="toolchain_configure"
)

docker_toolchain_configure(
  name = "docker_config",
# abosolute path to credentials directory 
  client_config="${ROOT}/credentials",
  docker_flags = [
    "--tls",
    "--log-level=error",
  ],
)

#java jsonwebtoken

maven_install(
    name = "jjwt",
    artifacts = [
        "io.jsonwebtoken:jjwt:0.9.0",
        "javax.xml.bind:jaxb-api:2.3.1"
    ],
    repositories = [
        "https://jcenter.bintray.com/",
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
    ],
)

#nodejs & js rules - already installed from proto grpc 
load("@rules_proto_grpc//js:repositories.bzl", rules_proto_grpc_js_repos="js_repos")
rules_proto_grpc_js_repos()

#installing nodejs rules 
http_archive(
    name = "build_bazel_rules_nodejs",
    sha256 = "f0f76a06fd6c10e8fb9a6cb9389fa7d5816dbecd9b1685063f89fb20dc6822f3",
    urls = ["https://github.com/bazelbuild/rules_nodejs/releases/download/4.5.1/rules_nodejs-4.5.1.tar.gz"],
)

# The npm_install rule runs yarn anytime the package.json or package-lock.json file changes.
# It also extracts any Bazel rules distributed in an npm package.
load("@build_bazel_rules_nodejs//:index.bzl","yarn_install")
yarn_install(
    # Name this npm so that Bazel Label references look like @npm//package
    name = "nodejs_modules",
    package_json = "//nodejs:package.json",
    yarn_lock = "//nodejs:yarn.lock",
)

yarn_install(
    # Name this npm so that Bazel Label references look like @npm//package
    name = "npm",
    package_json = "//npm:package.json",
    yarn_lock = "//npm:yarn.lock",
)

yarn_install(
    name = "frapp_modules",
    package_json = "//frontend/app:package.json",
    yarn_lock = "//frontend/app:yarn.lock"
)

yarn_install(
    name = "frapp_next_modules",
    package_json = "//frontend/next_app:package.json",
    yarn_lock = "//frontend/next_app:yarn.lock"
)

# Load nodejs_image rules to create java docker images to run grpc services
load(
    "@io_bazel_rules_docker//nodejs:image.bzl",
    _nodejs_image_repos = "repositories",
)
_nodejs_image_repos()


# nginx docker base image
load("@io_bazel_rules_docker//container:pull.bzl", "container_pull")

container_pull(
    name = "nginx_base",
    registry = "index.docker.io",
    repository = "library/nginx",
    tag = "1.21.0-alpine",
)

#envoy base image
container_pull(
    name = "envoy_base",
    registry = "index.docker.io",
    repository = "envoyproxy/envoy",
    tag = "v1.18.3"
)

#node base image
container_pull(
    name = "node_base",
    registry = "index.docker.io",
    repository = "library/node",
    tag = "alpine3.14"
)

#mongodb base image
container_pull(
    name = "mongo_base",
    registry = "index.docker.io",
    repository = "library/mongo"
)

# k8s - push images directly to cluster 
http_archive(
    name = "io_bazel_rules_k8s",
    strip_prefix = "rules_k8s-0.6",
    urls = ["https://github.com/bazelbuild/rules_k8s/archive/v0.6.tar.gz"],
    sha256 = "51f0977294699cd547e139ceff2396c32588575588678d2054da167691a227ef",
)

load("@io_bazel_rules_k8s//k8s:k8s.bzl", "k8s_repositories")

k8s_repositories()

load("@io_bazel_rules_k8s//k8s:k8s_go_deps.bzl", k8s_go_deps = "deps")

k8s_go_deps()
