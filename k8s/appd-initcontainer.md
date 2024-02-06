# appd deployment 
- Reference
  - https://docs.appdynamics.com/appd/20.x/en/application-monitoring/install-app-server-agents/java-agent/install-the-java-agent/install-the-java-agent-in-containers
- deployment 
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: appd-deployment
  namespace: lex-ext-kdp
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      volumes:
        - name: appd-agent-repo
          emptyDir: {}
      initContainers:
        - name: init-container
          image: docker.io/appdynamics/java-agent:20.8.0
          command: ["sh", "-c", "cp -r /opt/appdynamics/. /opt/temp"]
          volumeMounts:
            - mountPath: /opt/temp
              name: appd-agent-repo
      containers:
        - name: demo-container
          image: nginx:latest
          volumeMounts:
            - mountPath: "/opt/appdynamics"
              name: appd-agent-repo
          resources:
            limits:
              cpu: "750m"
              memory: 200Mi
            requests:
              cpu: "500m"
              memory: 150Mi

```
- explain

逐行解释这个 Deployment 的配置，并重点解释 Init Container、Volumes 和 VolumeMounts 部分。

1. `apiVersion: apps/v1`:
   - 这定义了使用的 Kubernetes API 的版本。

2. `kind: Deployment`:
   - 这指定了要创建的 Kubernetes 资源类型，即 Deployment。Deployment 管理着 Pod 的副本数，并确保它们在集群中按照指定的方式运行。

3. `metadata`:
   - 这包含了资源的元数据，包括名称和命名空间。在这个例子中，Deployment 的名称是 "appd-deployment"，它所属的命名空间是 "lex-ext-kdp"。

4. `spec`:
   - 这是 Deployment 的规范部分，定义了 Deployment 的期望状态。

5. `replicas: 3`:
   - 这指定了要创建的 Pod 的副本数目。在这个例子中，Deployment 将确保有三个副本的 Pod 在集群中运行。

6. `selector`:
   - 这指定了用于选择要管理的 Pod 的标签选择器。在这个例子中，Deployment 将管理具有标签 `app: my-app` 的 Pod。

7. `template`:
   - 这定义了要创建的 Pod 的模板。

8. `metadata`:
   - 这里包含了 Pod 模板的元数据，包括标签。在这个例子中，Pod 的标签是 `app: my-app`。

9. `spec`:
   - 这是 Pod 的规范部分，定义了 Pod 的规格。

10. `volumes`:
    - 这定义了 Pod 中的卷。在这个例子中，它定义了一个名为 "appd-agent-repo" 的卷，并且指定它是一个空目录卷（`emptyDir: {}`）。这意味着在 Pod 的生命周期内，所有容器都可以在该卷中读写数据，但在 Pod 删除后，该卷中的数据将被删除。

11. `initContainers`:
    - 这定义了初始化容器。初始化容器是在主容器启动之前运行的一组容器，用于执行特定的任务。在这个例子中，有一个名为 "init-container" 的初始化容器。
    - `name: init-container`：给初始化容器指定了一个名称，以便在日志中识别和跟踪它。
    - `image: docker.io/appdynamics/java-agent:20.8.0`：指定了初始化容器所使用的镜像，该镜像用于运行 AppDynamics Java Agent。
    - `command: ["sh", "-c", "cp -r /opt/appdynamics/. /opt/temp"]`：定义了初始化容器要执行的命令。这个命令的作用是将 `/opt/appdynamics/` 目录下的所有内容复制到 `/opt/temp/` 目录中。
    - `volumeMounts`：指定了初始化容器要挂载的卷。在这个例子中，它将名为 "appd-agent-repo" 的卷挂载到容器的 `/opt/temp` 目录中，以便在初始化容器内部访问该卷中的文件或数据。

12. `containers`:
    - 这定义了主容器。主容器是真正运行应用程序的容器。在这个例子中，有一个名为 "demo-container" 的容器，它使用了 `nginx:latest` 镜像。
    - `volumeMounts`：指定了主容器要挂载的卷。在这个例子中，它将名为 "appd-agent-repo" 的卷挂载到容器的 `/opt/appdynamics` 目录中，以便在主容器内部访问该卷中的文件或数据。
    - `resources`：定义了容器的资源限制和请求。在这个例子中，容器被限制使用最多 750m 的 CPU 和 200Mi 的内存，并且至少请求 500m 的 CPU 和 150Mi 的内存。

综上所述，这个 Deployment 的配置是用于在 Kubernetes 集群中部署一个具有初始化容器和主容器的应用程序，并使用空目录卷来在它们之间共享数据。




这部分定义了 Deployment 中的初始化容器（initContainers）和卷（volumes）。

1. `volumes`:
   - 这里定义了 Pod 中的卷。在这个例子中，定义了一个名为 "appd-agent-repo" 的卷，并且指定它是一个空目录卷（emptyDir）。空目录卷会在 Pod 创建时创建，并且对于同一 Pod 中的所有容器都是可见的。这意味着在该 Pod 中的任何容器都可以读写该卷，并且卷中的数据在 Pod 生命周期内是持久的，但在 Pod 删除后将被删除。

2. `initContainers`:
   - 这里定义了 Pod 中的初始化容器。初始化容器是在主容器启动之前运行的一组容器，用于执行一些初始化任务，例如加载配置文件、下载文件等。在这个例子中，有一个名为 "init-container" 的初始化容器。
   - `name: init-container`：给初始化容器指定了一个名称，以便在日志中识别和跟踪它。
   - `image: docker.io/appdynamics/java-agent:20.8.0`：指定了初始化容器所使用的镜像，该镜像用于运行 AppDynamics Java Agent。
   - `command: ["sh", "-c", "cp -r /opt/appdynamics/. /opt/temp"]`：定义了初始化容器要执行的命令。这个命令的作用是将 `/opt/appdynamics/` 目录下的所有内容复制到 `/opt/temp/` 目录中。这样做的目的可能是为了在主容器启动之前准备一些必要的文件或配置。
   - `volumeMounts`：指定了初始化容器要挂载的卷。在这个例子中，它将名为 "appd-agent-repo" 的卷挂载到容器的 `/opt/temp` 目录中，以便在初始化容器内部访问该卷中的文件或数据。

综上所述，这部分的含义是定义了 Deployment 中的初始化容器和卷。初始化容器在 Pod 启动之前执行特定的任务，而卷提供了用于在容器之间共享数据的机制。


- Login my pod 
```bash
kubectl exec -it appd-deployment-67dc5c659d-b55sf -n lex-ext-kdp -- /bin/bash
Defaulted container "demo-container" out of: demo-container, init-container (init)
root@appd-deployment-67dc5c659d-b55sf:/# cd /opt/temp
bash: cd: /opt/temp: No such file or directory
root@appd-deployment-67dc5c659d-b55sf:/# ls /opt/appdynamics/
LICENSE  conf  javaagent.jar  javaagent.jar.asc  multi-release	readme.txt  readme.txt.asc  ver20.8.0.30686
```


# volumes with volumeMounts
在 Kubernetes 的 Deployment 中，`volumes` 和 `volumeMounts` 是用来处理存储和数据挂载的两个相关但不同的概念。

1. `volumes`:
   - `volumes` 定义了 Pod 中的存储卷。这些存储卷可以是持久存储卷，也可以是临时存储卷。它们提供了一种将数据存储到 Pod 中的机制，可以被 Pod 中的多个容器共享。
   - `volumes` 可以定义在 Deployment 的模板中，也可以在 Pod 模板中定义。通常，它们会在 Pod 模板中定义，以便被 Pod 中的所有容器共享。
   - 一个 Pod 可以有多个卷，每个卷可以被一个或多个容器挂载使用。

2. `volumeMounts`:
   - `volumeMounts` 定义了容器如何将卷挂载到其文件系统中。它指定了卷挂载的路径和卷的名称。
   - 每个容器可以有多个 `volumeMounts` 条目，以便挂载多个卷到容器中。
   - `volumeMounts` 的声明通常在容器的配置部分中，用于告诉 Kubernetes 将哪些卷挂载到容器中。

总的来说，`volumes` 定义了 Pod 中的存储卷，而 `volumeMounts` 定义了容器如何使用这些卷。`volumes` 是 Pod 级别的概念，而 `volumeMounts` 是容器级别的概念。通过这两个概念的结合使用，可以实现在 Kubernetes 中灵活管理存储和数据挂载的需求。
