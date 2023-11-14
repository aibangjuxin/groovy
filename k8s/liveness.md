Kubernetes的存活探针（Liveness Probe）是一种用于检测容器是否还在运行的机制。如果存活探针检测到容器不健康，例如应用死锁（应用在运行，但是无法继续执行后面的步骤）情况，Kubelet将会重启容器，以提高应用的可用性，即使其中存在缺陷[1]。这是一种很好的健康检查机制，可以感知到容器崩溃，然后能够重启这个容器[2]。

Kubernetes支持三种类型的探针：存活探针（Liveness Probe）、就绪探针（Readiness Probe）和启动探针（Startup Probe）。每类探针都支持三种探测方法：HTTP GET、TCP Socket和Exec[2]。例如，HTTP GET方法是向容器发送HTTP GET请求，如果Probe收到2xx或3xx，说明容器是健康的[2]。

在配置存活探针时，你需要仔细配置，确保它能真正标示出不可恢复的应用故障，例如死锁。错误的存活探针可能会导致级联故障，这会导致在高负载下容器重启；例如由于应用无法扩展，导致客户端请求失败；以及由于某些 Pod 失败而导致剩余 Pod 的工作负载增加[1]。

例如，以下是一个配置存活探针的例子，其中运行一个基于 registry.k8s.io/busybox 镜像的容器。在这个配置文件中，可以看到 Pod 中只有一个 Container。periodSeconds 字段指定了 kubelet 应该每 5 秒执行一次存活探测。initialDelaySeconds 字段告诉 kubelet 在执行第一次探测前应该等待 5 秒。kubelet 在容器内执行命令 cat /tmp/healthy，如果命令执行成功，将返回 0，kubelet 就会认为该容器是活着的并且很健康。如果返回非 0 值，kubelet 就会杀掉这个容器并重启它[1]。

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    test: liveness
  name: liveness-exec
spec:
  containers:
  - name: liveness
    image: registry.k8s.io/busybox
    args:
    - /bin/sh
    - -c
    - touch /tmp/healthy; sleep 30; rm -f /tmp/healthy; sleep 600
    livenessProbe:
      exec:
        command:
        - cat
        - /tmp/healthy
      initialDelaySeconds: 5
      periodSeconds: 5
```
[1]

Sources
[1] 配置存活、就绪和启动探针 - Kubernetes https://kubernetes.io/zh-cn/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[2] 存活探针（Liveness Probe） - 华为云 https://support.huaweicloud.com/basics-cce/kubernetes_0010.html
[3] 配置Pod 的liveness 和readiness 探针· Kubernetes 中文指南 - Jimmy Song https://jimmysong.io/kubernetes-handbook/guide/configure-liveness-readiness-probes.html
[4] Kubernetes探针原理详解- 史振兴 - 博客园 https://www.cnblogs.com/szx666/p/16109838.html
[5] pod健康检查之容器的存活探针、就绪探针、启动探针原创 - CSDN博客 https://blog.csdn.net/MssGuo/article/details/123037625
[6] kubernetes的存活探针和就绪探针 - 51CTO博客 https://blog.51cto.com/lizexiong/5470207
[7] K8s之就绪探针和存活探针 - 阿里云开发者社区 https://developer.aliyun.com/article/1095979
[8] Kubernetes中几种探针的最佳实践 - 睿象云 https://aiops.com/news/post/13700.html

By Perplexity at https://www.perplexity.ai/search/5f25ac45-03f3-4c3b-9afb-999d12c6a190


以下是修改后的YAML配置，增加了三种类型的探针（存活探针、就绪探针、启动探针）：

```yaml
kind: Deployment
metadata:
  namespace: teamsnamespace
  name: sprint-lex-hap-memroy-cpu
  labels:
    194f867a2: enabled
    app: nginx
    type: pa
spec:
  replicas: 2
  selector:
    matchLabels:
      194f867a2: enabled
      team: lex
  template:
    metadata:
      labels:
        194f867a2: enabled
        app: nginx
        type: pa
        team: lex
    spec:
      serviceAccountName: lex-teamsnamespace-sa
      automountServiceAccountToken: false
      imagePullSecrets:
      - name: sprint
      containers:
      - image: eu.gcr.io/company/lex-api-podsdata-collector:1.0.1
        env:
        - name: https_proxy
          value: "192.168.192.51:443"
        imagePullPolicy: Always
        name: docker-container-hello-node
        resources:
          requests:
            memory: "900Mi"
            cpu: "200m"
            ephemeral-storage: "2Gi"
        # 增加存活探针
        livenessProbe:
          httpGet:
            path: "/.well_health"
            port: 8443
          initialDelaySeconds: 3
          periodSeconds: 5
        # 增加就绪探针
        readinessProbe:
          httpGet:
            path: "/.well_health"
            port: 8443
          initialDelaySeconds: 5
          periodSeconds: 5
        # 增加启动探针
        startupProbe:
          httpGet:
            path: "/.well_health"
            port: 8443
          initialDelaySeconds: 10
          periodSeconds: 5
```

解释：
- 存活探针 (`livenessProbe`): 用于检测容器是否存活，通过HTTP GET请求检查健康检查页面`.well_health`，初始延迟3秒，每5秒检查一次。
- 就绪探针 (`readinessProbe`): 用于检测容器是否准备好接收流量，通过HTTP GET请求检查健康检查页面`.well_health`，初始延迟5秒，每5秒检查一次。
- 启动探针 (`startupProbe`): 用于检测容器是否已经启动完成，通过HTTP GET请求检查健康检查页面`.well_health`，初始延迟10秒，每5秒检查一次。
- 健康检查页面路径为 `/.well_health`，端口为 `8443`。

