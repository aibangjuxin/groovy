Kubernetes Event 监听:
Kubernetes Deployment 的 replicas 修改会触发 Kubernetes Events。
你可以部署一个程序（例如自定义 operator 或 sidecar 容器）监听这些事件。
该程序需要知道哪些 namespace/Deployment 是需要监控的。这可以通过配置或标签实现。
当检测到 replicas 数量的修改事件时，记录事件发生的时间和新的 replicas 值


通过 Kubernetes Event 监听实现 `replicas` 数量修改检测，可以构建一个高效的实时监控系统。这种方法利用 Kubernetes 内部事件流，能够及时捕获和处理 Deployment 副本数修改事件。


# Q 
我的用户如果是重新Deployment这个API,但其实不仅仅是一个Event的Replicas的调整了.这种监控看起来就有局限性?


# A 
你说得对，如果用户重新 `Deploy` API（例如，通过修改 Deployment 的其他配置，然后触发重建），那么仅仅监听 `replicas` 字段的修改事件确实存在局限性。因为在这种情况下，可能会触发多个事件，而不仅仅是 `replicas` 变化事件。

**问题分析**

1.  **多种事件:**  重新 `Deploy` 一个 API 可能会触发以下事件：
    *   `MODIFIED` 事件 (当 Deployment 配置修改时)
    *   `DELETED` 事件 (当旧的 Pod 被删除时)
    *   `ADDED` 事件 (当新的 Pod 被创建时)
    *   `MODIFIED` 事件 (当 Pod 的状态发生变化时)
    *   **不仅仅是 `replicas` 的变化:**  用户可能还会修改其他配置，如 `image`, `resources`, `environment variables`, `volumes` 等。

2.  **监听粒度:**  如果只关注 `replicas` 的修改，可能会忽略其他重要的配置变化。

3.  **监控目标:**  你的监控目标可能不仅仅是 `replicas`，而是任何可能影响 API 运行的配置变化。

**更全面的监控策略**

为了解决这个问题，我们需要采用更全面的监控策略：

1.  **监听 Deployment 的所有修改事件:**
    *   不只关注 `replicas`，而是关注 Deployment 的所有 `MODIFIED` 事件。
    *   从 `MODIFIED` 事件的 `object` 中获取整个 Deployment 的配置。
    *   比较前后两个版本 Deployment 的配置，找出所有发生变化的字段。

2.  **检查配置项:**
    *   当检测到配置变化时，你需要检查哪些配置项发生了变化。
    *   你可以定义一个需要监控的配置列表。 例如： `replicas`, `image`, `resources.limits`, `resources.requests`, `environment`, `volumes` 等。
    *   针对不同的配置项，执行不同的逻辑。例如：`replicas` 变化，更新Prometheus指标，`image` 变化，通知用户。
    *   你需要对比新旧两个 Deployment 配置，找出所有差异。

3.  **数据存储:**
     *   除了存储 `desired_replicas`，还要存储其他你关心的配置信息， 例如新的 `image`， 新的`resource` 等等。
     *   这样可以更全面的分析用户的行为，也方便后续的审计。
     *   可以在 BigQuery 每日任务中，存储Deployment的配置信息和事件的时间戳

4. **区分事件类型:**

    *  将事件类型加入到Prometheus指标，可以区分不同的事件，然后针对性的设置告警
    * `deployment_events_total{type="MODIFIED", namespace="...", deployment="...",field="replicas"}`
    * `deployment_events_total{type="MODIFIED", namespace="...", deployment="...",field="image"}`
    * `deployment_events_total{type="DELETED", namespace="...", deployment="..."}`
     
5.  **结合 Kubernetes API 获取 Deployment 状态**
    *  如果需要获取Pod最新的运行状态，可以结合 Kubernetes API 实时查询 Deployment 的运行状态， 例如， `available_replicas`,  `updated_replicas` 等。
    *  将这些信息同步到 Prometheus指标。

**代码示例 (修改后的 Python Event Listener)**

```python
from kubernetes import client, config, watch
from prometheus_client import Gauge, start_http_server, Counter
import time
import jsonpatch
import json


config.load_incluster_config()
v1 = client.AppsV1Api()

# Prometheus Metrics
deployment_events_total = Counter('deployment_events_total', 'Deployment events', ['type', 'namespace', 'deployment', 'field'])
deployment_replicas_desired = Gauge('deployment_replicas_desired', 'Desired replicas of deployment', ['namespace','deployment'])
deployment_replicas_actual = Gauge('deployment_replicas_actual', 'Actual replicas of deployment', ['namespace','deployment'])
deployment_replicas_max = Gauge('deployment_replicas_max', 'Maximum replicas of deployment', ['namespace','deployment'])

start_http_server(8000)


def get_max_replicas_from_firestore(namespace, deployment_name):
    # 实际的从 Firestore/BigQuery 获取最大replicas的逻辑
    return 8


def watch_deployments():
    w = watch.Watch()
    for event in w.stream(v1.list_deployment_for_all_namespaces):
        deployment = event['object']
        namespace = deployment.metadata.namespace
        deployment_name = deployment.metadata.name
        event_type = event['type']


        if event_type == 'MODIFIED':
            # 获取 Deployment 的当前版本
            old_deployment = v1.read_namespaced_deployment(name=deployment_name, namespace=namespace)
            
            #获取Deployment更新之后的版本
            new_deployment = event['object']
            
            # 获取 Deployment配置变化
            old_deployment_json = json.loads(json.dumps(old_deployment.to_dict()))
            new_deployment_json = json.loads(json.dumps(new_deployment.to_dict()))
            patch = jsonpatch.JsonPatch.from_diff(old_deployment_json, new_deployment_json)

            for change in patch:
               path = change.get("path", "unknown").split("/")
               field = path[1]
               deployment_events_total.labels(type=event_type, namespace=namespace, deployment=deployment_name,field=field).inc()
               print(f"Deployment {namespace}/{deployment_name} modified, field {field}")

            # 处理 replicas 的变化
            desired_replicas = new_deployment.spec.replicas
            if desired_replicas:
                max_replicas = get_max_replicas_from_firestore(namespace, deployment_name)
                deployment_replicas_desired.labels(namespace=namespace, deployment=deployment_name).set(desired_replicas)
                deployment_replicas_max.labels(namespace=namespace, deployment=deployment_name).set(max_replicas)
                
                actual_replicas = new_deployment.status.available_replicas
                deployment_replicas_actual.labels(namespace=namespace, deployment=deployment_name).set(actual_replicas if actual_replicas is not None else 0)

        elif event_type == 'DELETED':
             deployment_events_total.labels(type=event_type, namespace=namespace, deployment=deployment_name,field="deployment").inc()
             print(f"Deployment {namespace}/{deployment_name} deleted")
        
        elif event_type == 'ADDED':
            deployment_events_total.labels(type=event_type, namespace=namespace, deployment=deployment_name,field="deployment").inc()
            print(f"Deployment {namespace}/{deployment_name} added")


if __name__ == '__main__':
    watch_deployments()
```

**改进说明:**

1.  **完整配置比较：** 使用 `jsonpatch` 库来比较 Deployment 配置的差异。
2. **事件计数器:** 使用 `Counter` 来记录不同类型的事件.
3. **更灵活的监控：**  你可以根据你的需求，修改 `watch_deployments()` 函数，以支持更多的监控逻辑。

**总结**

*   监听 Deployment 的所有修改事件可以更全面地监控用户的配置变化。
*   比较前后两个版本的配置可以找出哪些字段发生了变化。
*   你可以将变化的字段作为Prometheus指标的标签，方便你进行更细粒度的监控和告警。
*  记录各种类型的事件，便于你后续的分析和审计。

通过这种方式，你可以更全面地监控用户对 API 的修改，并及时发现可能影响 API 运行的问题。希望这个解释能够解决你的疑虑！



好的，我们来详细阐述一下 Kubernetes Event 监听部分，以及如何实现它。

**Kubernetes Events 的概念**

Kubernetes Events 是 Kubernetes 集群中发生的各种事件的记录。它们提供了关于集群状态变化的可见性，例如 Pod 的创建、删除、更新、错误等等。对于 Deployment 而言，其 `replicas` 字段的修改也会产生一个事件。

**为什么使用 Kubernetes Events？**

*   **实时性:** Kubernetes Events 是近实时产生的，你可以立即感知到集群状态的变化。
*   **细粒度:** 你可以监听特定类型的事件，例如 Deployment 的 `replicas` 修改事件。
*   **可观测性:** Kubernetes Events 为你的集群操作提供了重要的可观测性数据。

**如何监听 Kubernetes Events？**

主要有两种方式：

1.  **使用 Kubernetes API 客户端库**
    *   你可以使用 Kubernetes 官方提供的 API 客户端库（如 Python, Go, Java 等）来连接到 Kubernetes API Server。
    *   客户端库提供了 `watch` 功能，允许你监听特定类型的事件，并以流式方式接收更新。

2.  **使用 Kubernetes Event 导出器**
    *   有些工具（如 `kube-eventer`）专门用来收集 Kubernetes Events，并将它们导出到各种后端系统（如 Prometheus, Elasticsearch 等）。
    *   你可以使用这些工具来简化 Event 监听过程。

**使用 Kubernetes API 客户端库监听事件的步骤**

1.  **设置 Kubernetes 客户端**

    *   在你的程序中引入 Kubernetes API 客户端库。
    *   使用 `config.load_incluster_config()` 或 `config.load_kube_config()` 方法加载 Kubernetes 集群的配置。

    ```python
    from kubernetes import client, config

    # 如果在集群内部运行
    config.load_incluster_config()

    # 如果在集群外部运行，使用 kubeconfig 文件
    # config.load_kube_config(config_file="~/.kube/config")

    v1 = client.AppsV1Api()
    ```

2.  **创建 Watch 对象**

    *   使用 `watch.Watch()` 创建一个 Watch 对象。
    *   你可以使用该对象来监听特定类型的 Kubernetes 资源。

    ```python
    from kubernetes import watch
    w = watch.Watch()
    ```

3.  **监听 Deployment 事件**

    *   使用 Watch 对象的 `stream` 方法，配合 `v1.list_deployment_for_all_namespaces` 方法，监听所有 namespace 中的 Deployment 事件。
    *   你还可以使用 `list_namespaced_deployment` 方法监听特定 namespace 中的 Deployment 事件。

    ```python
    for event in w.stream(v1.list_deployment_for_all_namespaces):
    ```

4.  **处理事件**

    *   在循环中，每个事件都将作为一个字典返回，包含 `type`（事件类型）和 `object` (事件对象，这里是 Deployment 对象)。
    *   你可以根据 `type` 字段判断事件类型（例如 `ADDED`, `MODIFIED`, `DELETED`），然后从 `object` 对象中获取 Deployment 的信息。
    *   对于 `MODIFIED` 事件，可以从 Deployment 对象中获取新的 `replicas` 值。

    ```python
    if event['type'] == 'MODIFIED':
        deployment = event['object']
        namespace = deployment.metadata.namespace
        deployment_name = deployment.metadata.name
        desired_replicas = deployment.spec.replicas
        # ... 获取其他信息并进行处理 ...
    ```

5.  **配置过滤和标签选择器**
    *   如果你只想监听特定的 namespace 或 Deployment，你可以使用 `list_namespaced_deployment` 方法，并设置 `namespace` 参数。
    *   如果你想监听特定标签的 Deployment，可以使用 `label_selector` 参数。
    *   使用标签选择器可以减少需要处理的事件数量，从而减少资源的消耗。
    ```python
    # 监听特定namespace的deployment
    #  for event in w.stream(v1.list_namespaced_deployment, namespace="my-namespace")
    # 监听特定label的deployment
    # for event in w.stream(v1.list_deployment_for_all_namespaces, label_selector="app=my-app")
    ```

6.  **获取最大值**

    *   当收到 Deployment 的修改事件时，需要从 Firestore/BigQuery 中获取该 Deployment 允许的最大 `replicas` 值。
    *   这个值的获取逻辑可以封装在一个函数中。

**代码示例 (Python)**

```python
from kubernetes import client, config, watch
from prometheus_client import Gauge, start_http_server
import time

# 在集群内部运行
config.load_incluster_config()

# 如果在集群外部运行，使用 kubeconfig 文件
# config.load_kube_config(config_file="~/.kube/config")


v1 = client.AppsV1Api()

# Prometheus metrics 定义
deployment_replicas_desired = Gauge('deployment_replicas_desired', 'Desired replicas of deployment', ['namespace','deployment'])
deployment_replicas_actual = Gauge('deployment_replicas_actual', 'Actual replicas of deployment', ['namespace','deployment'])
deployment_replicas_max = Gauge('deployment_replicas_max', 'Maximum replicas of deployment', ['namespace','deployment'])

start_http_server(8000)

def get_max_replicas_from_firestore(namespace, deployment_name):
    # 实际的从 Firestore/BigQuery 获取最大replicas的逻辑
    # 你可以使用 Firestore/BigQuery 的客户端库
    # 这里是模拟数据，请替换成你的实际逻辑
    return 8

def watch_deployments():
    w = watch.Watch()
    for event in w.stream(v1.list_deployment_for_all_namespaces):
        deployment = event['object']
        namespace = deployment.metadata.namespace
        deployment_name = deployment.metadata.name

        if event['type'] == 'MODIFIED':
            desired_replicas = deployment.spec.replicas
            max_replicas = get_max_replicas_from_firestore(namespace, deployment_name)
            deployment_replicas_desired.labels(namespace=namespace, deployment=deployment_name).set(desired_replicas)
            deployment_replicas_max.labels(namespace=namespace, deployment=deployment_name).set(max_replicas)

            deployment = v1.read_namespaced_deployment(name=deployment_name, namespace=namespace)
            actual_replicas = deployment.status.available_replicas
            deployment_replicas_actual.labels(namespace=namespace, deployment=deployment_name).set(actual_replicas if actual_replicas is not None else 0)
            
            print(f"Deployment {namespace}/{deployment_name} replicas updated: desired={desired_replicas}, actual={actual_replicas}, max={max_replicas}")

if __name__ == '__main__':
    watch_deployments()
```

**部署 Event Listener**

*   你可以将 Event Listener 打包成 Docker 镜像。
*   在 Kubernetes 中，可以使用 Deployment 或者 DaemonSet 来部署 Event Listener。
*   为了确保 Event Listener 可以访问 Kubernetes API，需要使用 Service Account 并赋予必要的权限。

**总结**

*   Kubernetes Events 提供了一种实时监控集群状态变化的方式。
*   使用 Kubernetes API 客户端库，你可以方便地监听并处理 Event。
*   通过过滤和标签选择器，可以减少不必要的事件处理。
*   你需要将从 Event 中获取的 `replicas` 值存储到 Prometheus 指标，以便后续的告警和监控。

希望这个更详细的解释能够帮助你更好地理解 Kubernetes Event 监听。 如果你还有其他问题，请随时提出。





以下是具体实现步骤和代码示例：

---

### **设计思路**
1. **监听 Kubernetes Events**
   - 使用 Kubernetes API 监听特定 namespace 下 Deployment 的 `replicas` 修改事件。
   - 过滤与目标 Deployment 相关的事件。
   
2. **事件处理**
   - 记录修改事件的时间、旧值和新值。
   - 可选择性地将这些事件存储到数据库（如 BigQuery）或触发通知。

3. **程序部署**
   - 以独立 Deployment 或 Sidecar 容器运行监听程序。
   - 支持通过标签或配置文件指定需要监控的 Deployments。

---

### **实现步骤**

#### 1. 使用 Kubernetes Python Client
使用 Kubernetes 官方 Python 客户端来监听事件。

```python
from kubernetes import client, config, watch

# 加载 Kubernetes 配置
config.load_kube_config()

# 监听特定 Namespace 下的事件
def watch_deployment_events(target_namespace, monitored_deployments):
    v1 = client.AppsV1Api()
    w = watch.Watch()
    
    print(f"Start watching deployments in namespace: {target_namespace}")
    for event in w.stream(v1.list_namespaced_deployment, namespace=target_namespace):
        deployment = event['object']
        event_type = event['type']
        metadata = deployment.metadata
        spec = deployment.spec
        
        # 检查是否是目标 Deployment
        if metadata.name in monitored_deployments:
            old_replicas = deployment.status.replicas or 0
            new_replicas = spec.replicas
            
            # 处理 replicas 修改事件
            if old_replicas != new_replicas:
                print(f"Event Type: {event_type}")
                print(f"Deployment: {metadata.name}")
                print(f"Old Replicas: {old_replicas}, New Replicas: {new_replicas}")
                print(f"Timestamp: {metadata.creation_timestamp}")
                
                # TODO: 存储到数据库或触发通知
                handle_event(metadata.name, old_replicas, new_replicas, event_type)

def handle_event(deployment_name, old_replicas, new_replicas, event_type):
    # 示例：简单打印，可扩展为写入 BigQuery 或触发通知
    print(f"[ALERT] Deployment {deployment_name} replicas changed from {old_replicas} to {new_replicas}. Event: {event_type}")

# 运行监听程序
if __name__ == "__main__":
    TARGET_NAMESPACE = "default"
    MONITORED_DEPLOYMENTS = ["example-deployment", "user-api"]
    watch_deployment_events(TARGET_NAMESPACE, MONITORED_DEPLOYMENTS)
```

---

#### 2. 配置监控目标
通过以下方式指定需要监控的 Deployment：
- **标签**：为 Deployment 添加特定标签，例如 `monitored=true`，并在程序中过滤。
- **配置文件**：将监控的 Deployment 名称存储在配置文件中，程序读取后加载。

---

#### 3. 事件存储与报警
您可以将事件数据存储在数据库中（例如 BigQuery），或者直接触发报警。

##### 示例：存储到 BigQuery
```python
from google.cloud import bigquery

def store_event_in_bigquery(deployment_name, old_replicas, new_replicas, event_type, timestamp):
    bq_client = bigquery.Client()
    table_id = "your_project_id.dataset.deployment_events"
    rows_to_insert = [
        {
            "deployment_name": deployment_name,
            "old_replicas": old_replicas,
            "new_replicas": new_replicas,
            "event_type": event_type,
            "timestamp": timestamp,
        }
    ]
    bq_client.insert_rows_json(table_id, rows_to_insert)
```

##### 示例：触发报警
使用 Google Cloud Pub/Sub 或其他通知系统发送报警。

---

### **程序部署**
1. **创建 Docker 镜像**
   将上述 Python 脚本打包成 Docker 镜像。

   ```dockerfile
   FROM python:3.9-slim
   RUN pip install kubernetes google-cloud-bigquery
   COPY watcher.py /app/watcher.py
   CMD ["python", "/app/watcher.py"]
   ```

2. **部署到 Kubernetes**
   创建一个 Deployment，在目标 Cluster 中运行该程序：

   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: replicas-watcher
     labels:
       app: replicas-watcher
   spec:
     replicas: 1
     selector:
       matchLabels:
         app: replicas-watcher
     template:
       metadata:
         labels:
           app: replicas-watcher
       spec:
         containers:
         - name: replicas-watcher
           image: your-docker-repo/replicas-watcher:latest
           env:
           - name: TARGET_NAMESPACE
             value: "default"
           - name: MONITORED_DEPLOYMENTS
             value: "example-deployment,user-api"
   ```

---

### **优点**
1. **实时性**：通过 Kubernetes Events 实现实时监听，无需等待定时任务。
2. **灵活性**：可以动态配置需要监控的 Deployment。
3. **扩展性**：可将监听到的事件与计费逻辑、报警系统集成。

---

如果需要更复杂的功能，可以考虑使用 Kubernetes Operator 框架（如 [Operator SDK](https://sdk.operatorframework.io/) 或 [kubebuilder](https://book.kubebuilder.io/)）。
