在Google Kubernetes Engine（GKE）上实现蓝绿部署（Blue-Green Deployment）是一个很好的实践，可以在不影响当前生产环境的情况下部署新版本，并在验证新版本无误后进行流量切换。以下是实现蓝绿部署的一些具体步骤和建议：

### 概述

蓝绿部署的核心思想是保持两组环境（蓝和绿），一个是当前正在运行的（蓝），另一个是新版本（绿）。通过一些流量管理操作，在验证绿环境正常后，切换全部流量到绿环境。

### 具体实现步骤

1. **部署蓝色环境（当前版本）**：
   - 蓝色环境是当前运行的版本。

2. **部署绿色环境（新版本）**：
   - 部署新版本到绿色环境，但不影响当前生产流量。

3. **流量管理**：
   - 使用Kubernetes的Service和Ingress管理流量。
   - 验证绿色环境的正常运行。

4. **切换流量**：
   - 将流量从蓝色环境切换到绿色环境。

5. **监控和回滚**：
   - 监控新版本，如有问题快速回滚到蓝色环境。

### 示例

假设你有一个应用`my-app`，蓝色版本是`v1`，绿色版本是`v2`。

#### 1. 准备环境和命名规范

你可以在Deployment和Service中使用标签来区分蓝色环境和绿色环境。

#### 2. 部署蓝色环境

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-blue
  labels:
    app: my-app
    version: blue
spec:
  replicas: 3
  template:
    metadata:
      labels:
        app: my-app
        version: blue
    spec:
      containers:
      - name: my-app
        image: gcr.io/my-project/my-app:v1
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: my-app-blue
  labels:
    app: my-app
    version: blue
spec:
  selector:
    app: my-app
    version: blue
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
```

#### 3. 部署绿色环境

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-green
  labels:
    app: my-app
    version: green
spec:
  replicas: 3
  template:
    metadata:
      labels:
        app: my-app
        version: green
    spec:
      containers:
      - name: my-app
        image: gcr.io/my-project/my-app:v2
        ports:
        - containerPort: 80
---
apiVersion: v1
kind: Service
metadata:
  name: my-app-green
  labels:
    app: my-app
    version: green
spec:
  selector:
    app: my-app
    version: green
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
```

#### 4. 使用Ingress进行流量管理

创建一个Ingress，用于流量分发。

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-app-ingress
  annotations:
    ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: my-app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: my-app-blue
            port:
              number: 80
```

#### 5. 流量切换策略

通过修改Ingress将流量从蓝色环境切换到绿色环境。

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: my-app-ingress
  annotations:
    ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: my-app.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: my-app-green
            port:
              number: 80
```

#### 6. 监控和回滚

验证流量切换后，监控应用的状态和性能指标。 

如有问题，可以快速回滚：

- 将Ingress重新指向`my-app-blue`。
- 删除绿色环境的Deployment和Service。

### 监控工具

使用GCP的Cloud Monitoring（Stackdriver）来监控你的服务运行情况：

```sh
gcloud container clusters create my-cluster \
    --enable-stackdriver-kubernetes
```

### 总结

蓝绿部署在GKE上的实现步骤：

1. 在当前版本上部署蓝色环境。
2. 部署一个并行的绿色环境。
3. 使用Ingress或Service进行流量分发管理。
4. 验证绿色环境，成功后切换流量。
5. 监控新版本运行情况，如有问题快速回滚。

