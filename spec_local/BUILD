load("@io_bazel_rules_k8s//k8s:object.bzl", "k8s_object")
load("@io_bazel_rules_k8s//k8s:objects.bzl", "k8s_objects")

k8s_object(
  name = "deployment_gateway",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_gateway.yaml",
  substitutions = {
    "{SUB_BASE_URL}": "$(DOMAIN)",
  },
  images = {
      "YOUR_REGISTRY_NAME/slot_gateway": "//nodejs:nodejs_gateway_image"
  } 
)

k8s_object(
  name = "deployment_task",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_task.yaml",
  images = {
      "YOUR_REGISTRY_NAME/slot_task": "//java/com/task:task_java_image"
  },
)

k8s_object(
  name = "deployment_user_management",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_user_management.yaml",
  substitutions = {
    "{GOOGLE_CLIENT_ID}": "$(GOOGLE_CLIENT_ID)",
    "{MONGODB_URI}": "$(MONGODB_URI)",
    "{MONGODB_DB}": "$(MONGODB_DB)",
  },
  images = {
      "YOUR_REGISTRY_NAME/slot_user_management": "//java/com/user/management:user_management_java_image"
  },
)

k8s_object(
  name = "deployment_frapp",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_frapp.yaml",
  substitutions = {
    "{SUB_BASE_URL}": "$(DOMAIN)",
  },
  images = {
      "YOUR_REGISTRY_NAME/slot_frapp": "//frontend/app:prod_image"
  },
)

k8s_object(
  name = "deployment_next_frapp",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_next_frapp.yaml",
  substitutions = {
    "{SUB_BASE_URL}": "$(CLUSTER_INGRESS_IP)",
    "{NEXTAUTH_URL}" : "$(DOMAIN)",
    "{GOOGLE_CLIENT_ID}": "$(GOOGLE_CLIENT_ID)",
    "{GOOGLE_CLIENT_SECRET}": "$(GOOGLE_CLIENT_SECRET)",
  },
  images = {
      "YOUR_REGISTRY_NAME/slot_next_frapp": "//frontend/next_app:prod_image"
  },
)

k8s_object(
  name = "deployment_envoy",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_envoy.yaml",
  images = {
      "YOUR_REGISTRY_NAME/slot_envoy": "//frontend/envoy:envoy_image"
  },
)

k8s_object(
  name = "deployment_mongodb",
  kind = "deployment",
  kubeconfig = ":config",
  cluster = "minikube",
  template = ":deployment_mongodb.yaml",
  images = {
      "YOUR_REGISTRY_NAME/slot_mongodb": "//local:db_local_image"
  },
)

k8s_object(
  name = "service_gateway",
  template = ":service_gateway.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "service_task",
  template = ":service_task.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "service_user_management",
  template = ":service_user_management.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)


k8s_object(
  name = "service_frapp",
  template = ":service_frapp.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "service_next_frapp",
  template = ":service_next_frapp.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "service_envoy",
  template = ":service_envoy.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "service_mongodb",
  template = ":service_mongodb.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "service"
)

k8s_object(
  name = "ingress",
  template = ":ingress.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "ingress"
)

k8s_object(
  name = "ns_slot",
  template = ":ns_slot.yaml",
  kubeconfig = ":config",
  cluster = "minikube",
  kind = "namespace"
)

k8s_objects(
    name = "non_nodejs_deployment",
    objects = [
      ":deployment_task",
      ":deployment_envoy",
      "deployment_mongodb",
      ":deployment_frapp",
      ":deployment_next_frapp",
    ]
)

k8s_objects(
    name = "nodejs_deployment",
    objects = [
      ":deployment_gateway",
    ]
)

k8s_objects(
    name = "services",
    objects = [
      ":service_gateway",
      ":service_task",
      ":service_user_management",
      ":service_frapp",
      ":service_next_frapp",
      ":service_envoy",
      ":service_mongodb"
    ]
)

k8s_objects(
    name = "k8s_1",
    objects = [
        ":ns_slot",
        ":non_nodejs_deployment",
        ":services",
        ":ingress"
    ]
)

k8s_objects(
    name = "k8s_2",
    objects = [
        ":deployment_user_management",
    ]
)