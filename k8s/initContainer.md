# initContainers
##  About dir 
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  namespace: lex-ext-kdp
  name: app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: app
  template:
    metadata:
      labels:
        app: app
    spec:
      initContainers:
      - name: init-appd-dir
        image: python:latest
        command: ["sh", "-c", "mkdir -p /opt/appdynamics && touch /opt/appdynamics/a.txt && echo abc > /opt/appdynamics/a.txt"]
        volumeMounts:
        - name: appd-volume
          mountPath: /opt # 定义了 initContainer，它挂载了名为 appd-volume 的 Volume 到 /opt 目录
      containers:
      - name: app
        image: nginx
        volumeMounts:
        - name: appd-volume
          mountPath: /opt # 定义了主应用容器，它挂载了名为 appd-volume 的 Volume 到 /opt 目录
      volumes:
      - name: appd-volume
        emptyDir: {}

```

- eg:
```bash
initContainers:
       command: ["sh", "-c", "mkdir -p /opt/appdynamics && touch /opt/appdynamics/a.txt && echo abc > /opt/appdynamics/a.txt"]

Containers
        - name: appd-volume
          mountPath: /opt
```
- Result
```bash
在initContainers中，我们使用mkdir -p /opt/appdynamics && touch /opt/appdynamics/a.txt && echo abc > /opt/appdynamics/a.txt创建了一个名为a.txt的文件，
并将其写入了abc内容到a.txt 

需要注意的是后面 Containers的mountPath这里

我mountPath is /opt

root@app-74d7fb884-7p9rv:/opt/appdynamics# pwd
/opt/appdynamics
root@app-74d7fb884-7p9rv:/opt/appdynamics# cat a.txt
abc
```
- ask 
```
假如我有这样一个需求
我想构建一个deployment其包含2个部分一个是
initContainers一个是containers
我想要在initContainers中创建一个目录,然后这个目录里面在构建的时候将我的代码逻辑放在里面比如里面是一个start.sh 和一个 delete.sh
我在deploy的时候首先执行这个start.sh
然后在containers中将这个目录挂载到容器中. 普通的这个Containers是一个用户的API,等待用户的API启动之后
我要在容器中执行delete.sh
如果这样的逻辑是否可行? 如果可以行 请给我一个具体的deployment.yaml
```

是的，你的逻辑是可行的。你可以使用 Kubernetes 的 `initContainers` 来执行一些初始化操作，然后将生成的文件或目录挂载到主容器中。以下是一个简单的 `Deployment` YAML 文件，展示了如何实现这一逻辑：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      initContainers:
      - name: init-scripts
        image: busybox
        command:
        - sh
        - -c
        - |
          mkdir -p /app-scripts
          echo "echo Start Script Executed!" > /app-scripts/start.sh
          echo "echo Delete Script Executed!" > /app-scripts/delete.sh
        volumeMounts:
        - name: scripts-volume
          mountPath: /app-scripts
      containers:
      - name: user-api
        image: your-user-api-image:latest
        volumeMounts:
        - name: scripts-volume
          mountPath: /app-scripts
      volumes:
      - name: scripts-volume
        emptyDir: {}
```

这个 YAML 文件创建了一个 `Deployment`，其中包含了一个 `initContainers`，它使用 `busybox` 镜像执行一些初始化命令，生成了 `/app-scripts` 目录，并在其中创建了 `start.sh` 和 `delete.sh`。接着，这个目录被挂载到主容器中。

主容器使用了你的用户 API 镜像，并挂载了 `/app-scripts` 目录。你可以在用户 API 中通过 `/app-scripts/start.sh` 来执行启动逻辑，在用户 API 启动之后再执行 `/app-scripts/delete.sh`。

注意：这里使用了 `emptyDir` 类型的 Volume，这意味着它是临时的，Pod 重启后会丢失。如果需要更持久的存储，你可能需要使用 Persistent Volume 和 Persistent Volume Claim。此外，请确保你的用户 API 镜像中具备执行这两个脚本的执行权限。

你可以根据实际需求调整镜像和脚本的内容。


如果你想在 `Deployment` 中调用执行脚本 `/app-scripts/delete.sh`，你可以通过在 `Deployment` 的 `spec` 中的 `containers` 下的 `command` 字段来指定需要在容器启动时执行的命令。下面是一个示例：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-deployment
spec:
  replicas: 1
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      initContainers:
      - name: init-scripts
        image: busybox
        command:
        - sh
        - -c
        - |
          mkdir -p /app-scripts
          echo "echo Start Script Executed!" > /app-scripts/start.sh
          echo "echo Delete Script Executed!" > /app-scripts/delete.sh
        volumeMounts:
        - name: scripts-volume
          mountPath: /app-scripts
      containers:
      - name: user-api
        image: your-user-api-image:latest
        command:
        - sh
        - -c
        - |
          /app-scripts/start.sh
        volumeMounts:
        - name: scripts-volume
          mountPath: /app-scripts
      volumes:
      - name: scripts-volume
        emptyDir: {}
```

在上述示例中，`command` 字段被添加到了 `containers` 下的 `user-api` 容器中。这里将 `/app-scripts/start.sh` 添加到了容器的启动命令中。你可以按照类似的方式将 `/app-scripts/delete.sh` 加入到需要执行的命令中。

请注意，你的用户 API 镜像需要包含能够执行这两个脚本的执行权限。如果你遇到权限问题，可能需要在 Dockerfile 中为这两个脚本添加执行权限。

这只是一个简单的示例，你可以根据实际需求调整脚本和命令
