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

选择是使用HTTP探针还是TCP探针通常取决于你的应用程序的性质和需求。下面是一些建议：

1. **HTTP探针：**
   - **优势：** 更高级别的健康检查，允许你检查应用程序的具体HTTP端点，验证应用程序是否能够正确响应HTTP请求。
   - **适用场景：** 适用于Web应用程序、API服务等能够响应HTTP请求的场景。
   - **注意事项：** 请确保你的应用程序提供了健康检查的HTTP端点，而且该端点设计良好，能够反映应用程序的实际健康状态。

2. **TCP探针：**
   - **优势：** 更通用，适用于任何TCP连接。不需要应用程序提供特定的健康检查端点。
   - **适用场景：** 适用于无法使用HTTP进行健康检查的场景，例如数据库连接或其他基于TCP的服务。
   - **注意事项：** TCP探针通常只检查TCP连接是否建立，而不了解应用程序的具体健康状态。

3. **综合使用：**
   - **优势：** 在某些情况下，可以同时使用HTTP和TCP探针，以获得更全面的健康检查。
   - **适用场景：** 对于复杂的应用程序，可能需要同时检查TCP连接和特定HTTP端点，以确保全面的健康状态。
   - **注意事项：** 确保不要过度使用探针，以避免对应用程序性能造成负担。

综合考虑应用程序的性质、架构和运行环境，选择最适合你需求的探针类型。

**Explanation:**
- **Liveness Probe (livenessProbe):** Used to check if the container is alive, performs an HTTP GET request to the health check page /.well_health, with an initial delay of 3 seconds and checks every 5 seconds.
- **Readiness Probe (readinessProbe):** Used to check if the container is ready to receive traffic, performs an HTTP GET request to the health check page /.well_health, with an initial delay of 5 seconds and checks every 5 seconds.
- **Startup Probe (startupProbe):** Used to check if the container has completed startup, performs an HTTP GET request to the health check page /.well_health, with an initial delay of 10 seconds and checks every 5 seconds.
- The health check page is located at /.well_health, on port 8443.

Choosing between HTTP and TCP probes typically depends on the nature and requirements of your application. Here are some recommendations:

1. **HTTP Probe:**
   - *Advantages:* Higher-level health checks, allowing you to inspect specific HTTP endpoints, validating if the application responds correctly to HTTP requests.
   - *Use Cases:* Suitable for web applications, API services, and scenarios where the application can respond to HTTP requests.
   - *Considerations:* Ensure your application provides a well-designed health check HTTP endpoint reflecting the actual health status of the application.

2. **TCP Probe:**
   - *Advantages:* More general, applicable to any TCP connection. No need for the application to provide specific health check endpoints.
   - *Use Cases:* Suitable for scenarios where HTTP health checks are not feasible, such as database connections or other TCP-based services.
   - *Considerations:* TCP probes typically only verify if the TCP connection is established and do not understand the specific health status of the application.

3. **Combined Usage:**
   - *Advantages:* In some cases, using both HTTP and TCP probes simultaneously can provide a more comprehensive health check.
   - *Use Cases:* For complex applications, it may be necessary to check both TCP connections and specific HTTP endpoints to ensure a thorough health status.
   - *Considerations:* Ensure not to overuse probes to avoid placing a burden on application performance.

# squid
```yaml
containers:
- name: squid-proxy-container
  image: your-squid-image:tag
  ports:
  - containerPort: 3128
  livenessProbe:
    tcpSocket:
      port: 3128
    initialDelaySeconds: 10
    periodSeconds: 5
    failureThreshold: 3
  readinessProbe:
    tcpSocket:
      port: 3128
    initialDelaySeconds: 10
    periodSeconds: 5
    failureThreshold: 3
  startupProbe:
    tcpSocket:
      port: 3128
    initialDelaySeconds: 10
    periodSeconds: 5
    failureThreshold: 30
``` 
这配置是一个 Kubernetes Deployment 的容器部分，其中包含了存活探针（livenessProbe）、就绪探针（readinessProbe）和启动探针（startupProbe）的设置，主要针对名为 `squid-proxy-container` 的容器。

1. **基本容器配置：**
   - `name: squid-proxy-container`: 定义容器的名称。
   - `image: your-squid-image:tag`: 指定容器所使用的镜像，其中 `your-squid-image:tag` 应该被替换为实际的Squid代理镜像及其标签。
   - `ports`: 配置容器的端口映射。
     - `containerPort: 3128`: 定义容器监听的端口号，这里是Squid代理的默认端口。

2. **存活探针 (`livenessProbe`):**
   - 通过TCP套接字检测容器是否存活。
   - `port: 3128`: 指定TCP连接的目标端口。
   - `initialDelaySeconds: 10`: 容器启动后等待10秒开始第一次检测。
   - `periodSeconds: 5`: 每5秒执行一次存活探针。
   - `failureThreshold: 3`: 如果在连续3次检测中都失败，则认为容器不再存活。

3. **就绪探针 (`readinessProbe`):**
   - 通过TCP套接字检测容器是否准备好接受流量。
   - `port: 3128`: 指定TCP连接的目标端口。
   - `initialDelaySeconds: 10`: 容器启动后等待10秒开始第一次检测。
   - `periodSeconds: 5`: 每5秒执行一次就绪探针。
   - `failureThreshold: 3`: 如果在连续3次检测中都失败，则认为容器不准备好接受流量。

4. **启动探针 (`startupProbe`):**
   - 通过TCP套接字检测容器是否已经启动完成。
   - `port: 3128`: 指定TCP连接的目标端口。
   - `initialDelaySeconds: 10`: 容器启动后等待10秒开始第一次检测。
   - `periodSeconds: 5`: 每5秒执行一次启动探针。
   - `failureThreshold: 30`: 如果在连续30次检测中都失败，则认为容器启动失败。

这些探针用于确保Squid代理容器在运行、准备好接受流量以及成功启动后能够正常工作。适当调整这些参数以满足你的具体需求和应用场景。

确保你的Squid代理Pod在kube-dns启动之后再启动，可以通过以下几种方式来解决依赖启动顺序的问题：

1. **使用启动延迟：**
   - 在Squid代理容器的`startupProbe`或`livenessProbe`中增加一定的初始延迟（`initialDelaySeconds`），以确保kube-dns有足够的时间启动。例如，将`initialDelaySeconds`设置为较大的值，比如60秒，以便确保kube-dns有充分时间启动。

2. **使用依赖关系控制器：**
   - 如果你使用的是Kubernetes 1.9或更高版本，可以使用Init Containers 或 `initContainers` 字段，以确保 kube-dns 在Squid代理容器之前启动。Init Containers 是在主容器启动之前运行的短暂容器，可以用于执行一些初始化任务，例如等待依赖服务启动。
   
3. **使用等待机制：**
   - 在你的启动脚本中，添加等待kube-dns服务启动的机制。这可以通过在脚本中使用`nc`（netcat）或`curl`等工具，定期检查kube-dns服务是否可用，直到它准备好为止。

下面是一个简单的示例，演示了如何在启动脚本中使用等待机制：

```bash
#!/bin/sh

# Wait for kube-dns to be ready on port 53
echo "Waiting for kube-dns to be ready..."
while ! nc -z kube-dns-service 53; do
  sleep 5
done

# Continue with Squid proxy startup
exec squid
```

请根据你的具体环境和需求选择适合的方法。使用上述方法之一，可以确保在Squid代理启动时kube-dns已经准备就绪。

# dns

Kubernetes中的Pod在kube-dns没有启动的情况下，仍然可以启动，但可能会面临一些网络和服务发现方面的问题，特别是对于依赖kube-dns提供的 DNS 解析服务的应用。

以下是可能的影响：

1. **无法解析服务名称：** kube-dns负责为Pod提供服务名称到IP地址的解析服务。如果kube-dns没有启动，Pod将无法通过服务名称解析到相应的IP地址。这可能会影响应用程序与其他服务的通信。

2. **服务发现问题：** Kubernetes中的服务发现通常通过DNS完成。如果kube-dns不可用，其他Pod可能无法通过服务名称找到你的Pod。

3. **无法使用Service资源：** 如果你的应用程序依赖Kubernetes Service资源，且kube-dns没有启动，可能会导致服务发现和网络通信的问题。

为了确保你的应用程序能够正常工作，特别是在启动阶段，建议在容器启动之前确保kube-dns服务已经正常运行。你可以使用上述提到的方法之一，例如在启动脚本中添加等待kube-dns就绪的机制，以保证依赖服务的可用性。

在Kubernetes中，Pod的主机名通常是基于Pod名称的，默认情况下是Pod的metadata.name。Pod名称与主机名之间存在一定的关系，但并不依赖kube-dns。主机名与DNS解析相关，可以通过`/etc/hosts`文件来进行解析。

如果kube-dns没有启动，Pod仍然可以通过`/etc/hosts`文件进行基本的主机名解析。Kubernetes在每个Pod中都注入了一个本地的DNS解析器，该解析器会使用`/etc/hosts`文件中的信息，以及一些其他的本地解析策略，来解析主机名。

但请注意，这种解析方式可能会有限制，不同于kube-dns提供的完整的DNS解析服务。特别是，对于服务发现和跨命名空间的解析，kube-dns等DNS解析服务更为强大和灵活。

总的来说，Pod在kube-dns未启动的情况下，仍然能够获取到一定程度的主机名解析，但功能可能受到限制，特别是在涉及到Kubernetes服务发现和跨命名空间的情况下。
## squid likeness tcp 

在Kubernetes（K8S）中，可以使用存活（liveness）、就绪（readiness）和启动（startup）探针来检查Squid服务的启动状态。以下是一个示例配置，使用TCP探针检查3128端口的状态：

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: squid
  labels:
    app: squid
spec:
  containers:
  - name: squid
    image: your-squid-image
    ports:
    - containerPort: 3128
    livenessProbe:
      tcpSocket:
        port: 3128
      initialDelaySeconds: 15
      periodSeconds: 20
    readinessProbe:
      tcpSocket:
        port: 3128
      initialDelaySeconds: 15
      periodSeconds: 20
    startupProbe:
      tcpSocket:
        port: 3128
      initialDelaySeconds: 5
      periodSeconds: 10
      failureThreshold: 5
```

在这个示例中，`livenessProbe`和`readinessProbe`使用TCP探针检查3128端口的状态。存活探针在容器启动后的15秒开始工作，每20秒检查一次。就绪探针的配置与存活探针相同。启动探针在容器启动后的5秒开始工作，每10秒检查一次，允许失败5次。如果启动探针失败，Kubelet将杀死容器，而容器依其重启策略进行重启[1][4]。

请根据实际服务和环境调整探针的配置，例如探测频率、失败阈值等。

Sources
[1] 配置存活、就绪和启动探针 - Kubernetes https://kubernetes.io/zh-cn/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[2] Configure Liveness, Readiness and Startup Probes - Kubernetes https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[3] 连接到外部HTTPS 代理 - Istio https://istio.io/v1.1/zh/docs/examples/advanced-gateways/http-proxy/
[4] k8s怎么配置Pod的liveness和readiness与startup探针- 技术颜良 - 博客园 https://www.cnblogs.com/cheyunhua/p/15378350.html
[5] Liveness, Readiness, and Startup Probes | Kube by Example https://kubebyexample.com/learning-paths/application-development-kubernetes/lesson-4-customize-deployments-application-2
[6] Kubernetes-存活探针(liveness probe)（十六） - Andya_net - 博客园 https://www.cnblogs.com/Andya/p/17384773.html
[7] 配置Pod 的liveness 和readiness 探针- kubernetes中文手册 - FreeMesh - 云原生 https://doc.cncf.vip/kubernetes-handbook/yong-hu-zhi-nan/resource-configuration/configure-liveness-readiness-probes
[8] A Practical Guide to Kubernetes Startup Probe - Airplane.dev https://www.airplane.dev/blog/kubernetes-startup-probe
[9] k8s 存活探针LivenessProbe - 【The_crossing] - 博客园 https://www.cnblogs.com/liujunjun/p/14383075.html
[10] K8s Liveness/Readiness/Startup 探针机制原创 - CSDN博客 https://blog.csdn.net/IT_ZRS/article/details/128446388
[11] Configure Kubernetes Readiness and Liveness Probes - Tutorial - DEV Community https://dev.to/pavanbelagatti/configure-kubernetes-readiness-and-liveness-probes-tutorial-478p
[12] k8s中容器日志文件日志如何标准输出打印 https://www.niewx.cn/local-search.xml
[13] What Are Startup, Liveness, and Readiness in Kubernetes Probes - Loft Labs https://loft.sh/blog/kubernetes-probes-startup-liveness-readiness/
[14] 配置linux防止端口扫描-合作伙伴中心-认证鉴权:AK/SK认证-华为云 https://www.huaweicloud.com/guide/list-17508201-A-392
[15] A Guide to Understanding your Kubernetes Liveness Probes Best Practices - Fairwinds https://www.fairwinds.com/blog/a-guide-to-understanding-kubernetes-liveness-probes-best-practices

By Perplexity at https://www.perplexity.ai/search/71891259-2c7e-4096-8489-e56e11d7b46d


您可以使用以下方法验证GKE deployment 中 liveness、就绪和启动探针的配置是否生效：

1. **Liveness Probe验证：**
   - 使用`kubectl describe pod <pod名称>`查看有关Pod的详细信息，确认 liveness 探针的配置是否正确。
   - 观察 liveness 探针的重试次数和间隔，确保与您的预期相符。
   - 模拟容器内的故障条件，例如通过发送错误请求，验证 liveness 探针是否按预期触发 Pod 的重启。

2. **就绪 Probe验证：**
   - 同样使用`kubectl describe pod <pod名称>`检查就绪探针的配置。
   - 确保就绪探针的成功阈值和失败阈值符合您的期望。
   - 使用`kubectl get pods`检查 Pod 的就绪状态，确保就绪状态反映了就绪探针的状态。

3. **启动 Probe验证：**
   - 启动探针通常是容器启动后的初始化检查。确认您的应用程序启动时是否满足启动探针的条件。
   - 查看 Pod 的事件，以确保启动探针没有引发 Pod 启动失败的问题。

4. **测试 Case：**
   - 创建测试容器，模拟正常运行条件，确保 liveness 和就绪探针返回成功。
   - 模拟不正常的运行条件，例如资源不足或服务不可用，确保 liveness 和就绪探针返回失败。
   - 使用 GKE 的自动修复功能观察是否根据 liveness 探针的状态进行 Pod 的自动重启。

请注意，在进行这些测试时，确保您的测试环境与生产环境相似，以获得准确的结果。如果您有进一步的细节或特定的配置，我可以提供更具体的帮助。

要模拟或触发 GKE deployment 中 liveness、就绪和启动探针的状态，您可以使用以下方法：

1. **手动触发 Liveness Probe 失败：**
   - 可以通过发送错误请求或模拟应用程序内部错误，使得 liveness 探针返回失败。例如，如果 liveness 探针是一个 HTTP 接口，您可以使用 `curl` 发送一个返回非 2xx 状态码的请求。

2. **手动触发就绪状态的变化：**
   - 改变应用程序的状态，使其不符合就绪探针的期望条件。例如，如果就绪探针要求某个服务可用，您可以模拟该服务不可用的情况，观察就绪状态是否相应地变化。

3. **手动触发启动探针的条件：**
   - 确保您的启动探针的条件是应用程序启动后的一些初始化检查。您可以手动触发这些条件，例如在容器中模拟某些资源的准备就绪。

4. **使用 `kubectl exec` 进入容器进行手动测试：**
   - 使用 `kubectl exec` 命令进入运行中的容器，手动运行一些命令，以模拟探针的条件。例如，您可以模拟某个服务不可用或检查应用程序的启动状态。

5. **通过修改应用程序代码引发状态变化：**
   - 在应用程序代码中引入一些模拟用的标志或配置，以在需要时触发状态变化。这可以包括在测试环境中引入特定的测试标志。

请确保在进行这些测试时，您了解应用程序的行为，以避免对生产环境产生意外的影响。同时，可以通过监视 Pod 的事件和日志来检查这些状态的变化。

对于运行 Squid 且使用 TCP 的 Pod，您可以使用以下测试方法来验证 liveness、就绪和启动探针的配置：

1. **Liveness Probe 测试：**
   - 配置 Squid Pod 的 liveness 探针，通常是通过检查 Squid 服务是否在运行。您可以使用 `nc`（netcat）等工具测试 Squid 服务的运行状态，以确保 liveness 探针按预期工作。

     ```yaml
     livenessProbe:
       exec:
         command:
         - sh
         - -c
         - nc -zv localhost 3128
       initialDelaySeconds: 5
       periodSeconds: 5
     ```

   - 使用 `kubectl describe pod <pod名称>` 查看 liveness 探针的执行状态，确认是否在预定的时间间隔内成功执行。

2. **就绪 Probe 测试：**
   - Squid 就绪探针通常需要检查 Squid 是否已完成初始化，可以通过检查 Squid 服务端口是否打开来实现。

     ```yaml
     readinessProbe:
       exec:
         command:
         - sh
         - -c
         - nc -zv localhost 3128
       initialDelaySeconds: 10
       periodSeconds: 5
     ```

   - 使用 `kubectl describe pod <pod名称>` 确认就绪探针是否按预期工作，且 Pod 在初始化完成后变为就绪状态。

3. **启动 Probe 测试：**
   - 启动探针可能需要检查 Squid 服务的初始化状态，确保 Squid 在启动后能够正常运行。

     ```yaml
     startupProbe:
       exec:
         command:
         - sh
         - -c
         - nc -zv localhost 3128
       failureThreshold: 30
       periodSeconds: 5
     ```

   - 使用 `kubectl describe pod <pod名称>` 确认启动探针是否按预期工作，且在 Squid 服务初始化完成后，Pod 成功变为运行状态。

4. **通过修改 Squid 配置引发状态变化：**
   - 在测试环境中修改 Squid 配置，引入一些模拟用的变化，以触发探针的条件变化。例如，可以模拟 Squid 配置错误或特定场景的网络问题。

请根据您的具体场景和 Squid 的配置调整上述示例。确保在测试时密切关注探针的执行日志和 Pod 的事件，以确保它们按预期工作。

假设我的GKE deployment 启用了liveness那么我如何验证我的存活探针 就绪探针或者启动探针配置生效 有没有好的测试case

要验证GKE deployment的存活探针（liveness probe）、就绪探针（readiness probe）和启动探针（startup probe）是否配置生效，可以通过以下测试案例进行验证：

1. **存活探针（Liveness Probe）测试**：存活探针用于检测容器是否在运行。如果存活探针失败，Kubernetes会杀死容器，并根据其重启策略来决定是否创建新的容器。你可以通过故意让存活探针失败来测试其是否生效。例如，如果你的存活探针是一个HTTP GET请求，你可以尝试关闭应用的HTTP服务或者让其返回非200-399的HTTP状态码，然后观察Kubernetes是否会重新启动容器[1]。

2. **就绪探针（Readiness Probe）测试**：就绪探针用于检测容器是否已经准备好接受请求。如果就绪探针失败，Kubernetes会停止将请求转发到该容器，直到探针再次成功。你可以通过故意让就绪探针失败来测试其是否生效。例如，如果你的就绪探针是一个HTTP GET请求，你可以尝试让应用返回非200-399的HTTP状态码，然后观察Kubernetes是否会停止将请求转发到该容器[1]。

3. **启动探针（Startup Probe）测试**：启动探针用于检测应用程序在启动过程中是否运行正常。如果启动探针失败，Kubernetes会杀死容器，并根据其重启策略来决定是否创建新的容器。你可以通过故意让启动探针失败来测试其是否生效。例如，如果你的启动探针是一个HTTP GET请求，你可以在应用启动时让其返回非200-399的HTTP状态码，然后观察Kubernetes是否会重新启动容器[3]。

在进行上述测试时，你可以使用`kubectl describe pod <pod-name>`命令来查看Pod的事件和状态，这可以帮助你确定探针是否按预期工作[1][4]。

Sources
[1] 配置存活、就绪和启动探针 - Kubernetes https://kubernetes.io/zh-cn/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[2] 适用于应用负载均衡器的GKE Ingress - Google Cloud https://cloud.google.com/kubernetes-engine/docs/concepts/ingress?hl=zh-cn
[3] GKE1.18中未使用启动探针” - 火山引擎 https://www.volcengine.com/theme/6941051-G-7-1
[4] 【七】Kubernetes 探针介绍- 存活、就绪探针案例测试- 神奇二进制 - 博客园 https://www.cnblogs.com/l-hh/p/14848256.html
[5] Kubernetes生产最佳实践-应用开发原创 - CSDN博客 https://blog.csdn.net/weixin_42526859/article/details/120066154
[6] 在GKE非httpDocker构建中无法使活跃探针正常工作-火山引擎 https://www.volcengine.com/theme/5346567-Z-7-1
[7] 一文带你检查Kubernetes应用是否为最佳实践 - 稀土掘金 https://juejin.cn/post/6844904024911642637
[8] How to gracefully deploy applications on Kubernetes - Han Hongfa's personal blog https://www.hanhf.com/?p=38
[9] 存活探针（Liveness Probe） - 华为云 https://support.huaweicloud.com/basics-cce/kubernetes_0010.html
[10] GKE 最佳实践：设计和构建高可用性集群 - Google Cloud https://www.infoq.cn/zones/google/cloud/article/kIa3JMG2sJuGOKNLtytl
[11] 【云原生| 从零开始学Kubernetes】十三、k8s的容器探测以及启动探测 - 阿里云开发者社区 https://developer.aliyun.com/article/1047055
[12] 原生Kubernetes监控功能详解-Part2_文化& 方法 - InfoQ https://www.infoq.cn/article/oax5qaeczlk4ryaa7hfg
[13] Kubernetes学习（一）之认识Kubernetes - 一只病猫 https://dinghuang.github.io/2019/01/12/Kubernetes%E5%AD%A6%E4%B9%A0%EF%BC%88%E4%B8%80%EF%BC%89%E4%B9%8B%E8%AE%A4%E8%AF%86Kubernetes/
[14] 容器组设置 - KubeSphere https://kubesphere.io/zh/docs/v3.3/project-user-guide/application-workloads/container-image-settings/
[15] [云原生] Kubernetes（k8s）健康检查详解与实战演示（就绪性探针和存活性探针） - 博客园 https://www.cnblogs.com/liugp/p/16630873.html
[16] 就绪探针（Readiness Probe） - 华为云 https://support.huaweicloud.com/basics-cce/kubernetes_0026.html
[17] GKE 最佳实践：设计和构建高可用性集群 - InfoQ https://www.infoq.cn/article/kia3jmg2sjugoknltytl
[18] Kubernetes生产环境最佳实践 - 云原生知识星球 https://lyyao09.github.io/2020/11/12/k8s/Kubernetes-Production-Best-Practices/
[19] pod健康检查之容器的存活探针、就绪探针、启动探针原创 - CSDN博客 https://blog.csdn.net/MssGuo/article/details/123037625
[20] k8s架构及服务详解- 姚红 - 博客园 https://www.cnblogs.com/yaohong/p/11361784.html

By Perplexity at https://www.perplexity.ai/search/bdac9fcc-f313-42d6-a1fd-07cb7f614f5c

对于TCP场景，你可以使用TCP存活探针（Liveness Probe）、TCP就绪探针（Readiness Probe）和TCP启动探针（Startup Probe）进行测试。以下是一些测试建议：

1. **TCP存活探针测试**：TCP存活探针尝试与容器指定端口建立TCP连接，如果连接成功建立，说明容器是健康的[4]。你可以尝试关闭应用的TCP服务或者让其无法建立连接，然后观察Kubernetes是否会重新启动容器。

2. **TCP就绪探针测试**：TCP就绪探针尝试与容器建立TCP连接，如果能建立连接说明已经就绪[2]。你可以尝试让应用无法建立连接，然后观察Kubernetes是否会停止将请求转发到该容器。

3. **TCP启动探针测试**：TCP启动探针的测试方法与TCP存活探针和TCP就绪探针类似，主要是在应用启动时进行测试。你可以尝试让应用在启动时无法建立连接，然后观察Kubernetes是否会重新启动容器。

在进行测试时，你可以通过查看Kubernetes的事件或者容器的日志来验证探针是否生效。例如，如果探针失败，你应该能在Kubernetes的事件中看到相应的错误信息，或者在容器的日志中看到探针请求失败的记录[1]。

Sources
[1] 配置存活、就绪和启动探针 - Kubernetes https://kubernetes.io/zh-cn/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/
[2] 就绪探针（Readiness Probe） - 华为云 https://support.huaweicloud.com/basics-cce/kubernetes_0026.html
[3] 4.6.3 设置TCP 探针 https://kubernetes.renkeju.com/chapter_4/4.6.3.Setting_up_the_TCP_probe.html
[4] 存活探针（Liveness Probe） - 华为云 https://support.huaweicloud.com/basics-cce/kubernetes_0010.html
[5] 存活探针(Liveness)、就绪探针(Readiness)、启动探针(Startup)、容器钩子 - Akiraka https://www.akiraka.net/kubernetes/855.html
[6] Kubernetes 三大探针及探针方式原创 - CSDN博客 https://blog.csdn.net/LiukuoDocker7/article/details/125518141
[7] Kubernetes-存活探针(liveness probe) - Andya_net - 博客园 https://www.cnblogs.com/Andya/p/12434547.html
[8] [云原生] Kubernetes（k8s）健康检查详解与实战演示（就绪性探针和存活性探针） - 博客园 https://www.cnblogs.com/liugp/p/16630873.html
[9] Kubernetes探针原理详解- 史振兴 - 博客园 https://www.cnblogs.com/szx666/p/16109838.html
[10] Kubernetes 就绪探针（Readiness Probe） - 人艰不拆_zmc - 博客园 https://www.cnblogs.com/zhangmingcheng/p/15654956.html
[11] 配置Pod 的liveness 和readiness 探针· Kubernetes 中文指南 - Jimmy Song https://jimmysong.io/kubernetes-handbook/guide/configure-liveness-readiness-probes.html
[12] 探针· 个人笔记 - 前言 http://www.ytool.cloud/k8s/pod/probe.html
[13] Kubernetes-存活探针(liveness probe)（十六） 原创 - CSDN博客 https://blog.csdn.net/Andya_net/article/details/104715512
[14] Istio 服务的健康检查 https://preliminary.istio.io/latest/zh/docs/ops/configuration/mesh/app-health-check/
[15] Kubernetes--Pod存活性探测(设置TCP探针) - 稀土掘金 https://juejin.cn/post/7158619154587582495
[16] Kubernetes-存活探针(liveness probe)（十六） - 51CTO博客 https://blog.51cto.com/u_16102572/6254777

By Perplexity at https://www.perplexity.ai/search/bdac9fcc-f313-42d6-a1fd-07cb7f614f5c


