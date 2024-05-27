## 使用 Kubernetes 自定义资源 (CRD) 进行版本管理的详细指南

Kubernetes 自定义资源 (CRD) 提供了一种强大的机制来扩展 Kubernetes API，使其能够定义和管理特定于您的应用程序或需求的自定义资源类型。 在版本管理的背景下，CRD 可以发挥重要作用，帮助您有效地跟踪、管理和删除 API 版本。

### 定义 CRD

第一步是定义一个 CRD 来表示您的 API 版本。 这涉及创建 YAML 文件，其中包含有关自定义资源的详细信息，例如名称、架构和字段。 以下是一个示例 CRD，用于表示 API 版本：

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: apiversions.example.com
spec:
  group: example.com
  versions:
    - name: v1alpha1
      served: true
      storage: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            majorVersion:
              type: integer
            minorVersion:
              type: integer
            deploymentName:
              type: string
            serviceName:
              type: string
            dnsRecordName:
              type: string
```

在这个示例中，CRD 定义了一个名为 `APIVersion` 的自定义资源类型，该类型包含以下字段：

* `majorVersion`: API 的主版本号
* `minorVersion`: API 的次版本号
* `deploymentName`: 与 API 版本关联的部署名称
* `serviceName`: 与 API 版本关联的服务名称
* `dnsRecordName`: 与 API 版本关联的 DNS 记录名称

### 版本控制器

接下来，您需要开发一个控制器来监视 CRD 中的更改并相应地执行操作。 控制器将负责创建、更新和删除 API 版本的相应资源，例如部署、服务和 DNS 记录。

以下是控制器工作流程概述：

1. 控制器启动并连接到 Kubernetes API 服务器。
2. 控制器监视 CRD 中的更改。
3. 当创建新的 CRD 时，控制器会解析其配置并提取相关信息，例如 API 版本、部署名称、服务名称和 DNS 记录名称。
4. 控制器使用这些信息来创建相应的资源，例如部署、服务和 DNS 记录。
5. 控制器还将定期检查现有的 CRD 并根据定义的保留策略识别要删除的旧版本。
6. 对于要删除的每个版本，控制器将删除关联的资源，例如部署、服务和 DNS 记录。

### 实施示例

以下是一个示例代码片段，演示了如何使用 `kubebuilder` 工具创建 CRD 和控制器：

```go
package controller

import (
    "github.com/go-logr/logr"
    "k8s.io/apimachinery/pkg/runtime"
    "k8s.io/client-go/dynamic"
    "sigs.k8s.io/controller-runtime/handler"
    "sigs.k8s.io/controller-runtime/reconciler"
)

type Reconciler struct {
    client dynamic.Interface
    log   logr.Logger
}

func (r *Reconciler) Reconcile(request reconcile.Request) (reconcile.Result, error) {
    // 解析 CRD 并提取相关信息
    apiVersion, err := getAPIVersionFromRequest(request)
    if err != nil {
        return reconcile.Result{}, err
    }

    // 根据 CRD 信息创建相应的资源
    err = createResources(r.client, apiVersion)
    if err != nil {
        return reconcile.Result{}, err
    }

    // 检查旧版本并根据保留策略删除
    err = deleteOldVersions(r.client)
    if err != nil {
        return reconcile.Result{}, err
    }

    return reconcile.Result{}, nil
}

func (r *Reconciler) SetupWithManager(mgr reconcile.Manager) error {
    return mgr.GetFieldIndexer().IndexField(apiVersion{}, "metadata.name", handler.ClusterScoped)
}
```

此示例代码片段演示了如何使用 `kubebuilder` 工具创建基本的 CRD 和控制器。 您可以扩展此代码以实现更复杂的逻辑，例如支持多命名空间、自定义保留策略和错误处理。

### 优势

使用 CRD 进行版本管理具有以下优势：

* **灵活性和可扩展性:** CRD 允许您定义和管理特定于您的应用程序或需求的自定义资源类型。 这提供了极大的灵活性和可扩展性，使您可以根据需要定制版本管理方案。
* **集中管理:** 您可以将所有与 API 版本相关的信息存储在一个 CRD 中，包括版本号、部署名称、服务名称和 DNS 记录


# crd chatgpt
利用Kubernetes的自定义资源定义（CRD）来管理API版本和关联资源的版本控制是一种强大的方式，可以通过自动化管理来提高效率和稳定性。下面是详细的步骤和示例，帮助你实现这一目标。

### 1. 定义CRD

首先，定义一个CRD来表示API版本，包括主版本、次版本、部署名称、服务名称和DNS记录名称字段。

```yaml
apiVersion: apiextensions.k8s.io/v1
kind: CustomResourceDefinition
metadata:
  name: apiversions.yourdomain.com
spec:
  group: yourdomain.com
  versions:
    - name: v1
      served: true
      storage: true
      schema:
        openAPIV3Schema:
          type: object
          properties:
            spec:
              type: object
              properties:
                majorVersion:
                  type: integer
                minorVersion:
                  type: integer
                deploymentName:
                  type: string
                serviceName:
                  type: string
                dnsRecordName:
                  type: string
  scope: Namespaced
  names:
    plural: apiversions
    singular: apiversion
    kind: ApiVersion
    shortNames:
    - av
```

### 2. 创建Controller

接下来，创建一个控制器来监视CRD中的更改，并执行必要的操作。

#### Controller代码示例（Python版，基于Kubernetes的Python客户端和controller-runtime）

1. **安装依赖**：
   - `pip install kubernetes`
   - `pip install kopf`

2. **控制器代码**：

```python
import kopf
import kubernetes
import logging

kubernetes.config.load_kube_config()

@kopf.on.create('apiversions.yourdomain.com', 'v1')
def create_fn(spec, name, namespace, **kwargs):
    major_version = spec.get('majorVersion')
    minor_version = spec.get('minorVersion')
    deployment_name = spec.get('deploymentName')
    service_name = spec.get('serviceName')
    dns_record_name = spec.get('dnsRecordName')

    # 部署新的版本
    create_deployment(namespace, deployment_name)
    create_service(namespace, service_name)
    update_dns_record(dns_record_name)

    # 触发旧版本删除逻辑
    delete_old_versions(namespace, major_version)

def create_deployment(namespace, deployment_name):
    # 创建Deployment的代码
    pass

def create_service(namespace, service_name):
    # 创建Service的代码
    pass

def update_dns_record(dns_record_name):
    # 更新DNS记录的代码
    pass

def delete_old_versions(namespace, major_version):
    # 查找旧版本并删除的代码
    api = kubernetes.client.CustomObjectsApi()
    versions = api.list_namespaced_custom_object(
        group="yourdomain.com",
        version="v1",
        namespace=namespace,
        plural="apiversions"
    )
    for version in versions['items']:
        if version['spec']['majorVersion'] == major_version and version['metadata']['name'] != name:
            old_deployment = version['spec']['deploymentName']
            old_service = version['spec']['serviceName']
            old_dns_record = version['spec']['dnsRecordName']
            delete_deployment(namespace, old_deployment)
            delete_service(namespace, old_service)
            delete_dns_record(old_dns_record)

def delete_deployment(namespace, deployment_name):
    # 删除Deployment的代码
    pass

def delete_service(namespace, service_name):
    # 删除Service的代码
    pass

def delete_dns_record(dns_record_name):
    # 删除DNS记录的代码
    pass

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    kopf.run()
```

### 3. 部署Controller

1. **构建Docker镜像**：
   创建一个Dockerfile来构建你的控制器。

   ```dockerfile
   FROM python:3.8-slim

   WORKDIR /app
   COPY . /app

   RUN pip install --no-cache-dir -r requirements.txt

   ENTRYPOINT ["kopf", "run", "/app/controller.py"]
   ```

2. **部署Controller**：
   创建一个Deployment来运行你的控制器。

   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: apiversion-controller
     namespace: your-namespace
   spec:
     replicas: 1
     selector:
       matchLabels:
         app: apiversion-controller
     template:
       metadata:
         labels:
           app: apiversion-controller
       spec:
         containers:
           - name: controller
             image: your-docker-repo/apiversion-controller:latest
             imagePullPolicy: Always
             env:
               - name: KUBERNETES_SERVICE_HOST
                 value: "kubernetes.default.svc"
               - name: KUBERNETES_SERVICE_PORT
                 value: "443"
   ```

### 4. 部署CRD和Controller

1. **应用CRD**：
   ```bash
   kubectl apply -f crd.yaml
   ```

2. **部署Controller**：
   ```bash
   kubectl apply -f controller-deployment.yaml
   ```

### 5. 使用CRD管理API版本

创建一个新的API版本CRD实例：

```yaml
apiVersion: yourdomain.com/v1
kind: ApiVersion
metadata:
  name: apiversion-1-0
  namespace: your-namespace
spec:
  majorVersion: 1
  minorVersion: 0
  deploymentName: abj-aibang-is-onboarding-na-accouts-1-0-0-deployment
  serviceName: abj-aibang-is-onboarding-na-accouts-1-0-0-service
  dnsRecordName: abj-aibang-is-onboarding-na-accouts-1-0-0-internal.aliyun.aibang.cn.local
```

通过这种方式，使用Kubernetes的CRD和自定义控制器，你可以实现一个自动化的API版本管理系统，确保在部署新版本时自动删除旧版本，并管理相关的资源。
