要在GKE中部署一个API，并能够指定响应延迟（如通过URL中的`timeout`参数），可以使用Java或Python实现。以下是两种语言的简单实现示例。

##n Jav Deployment和Service来部署应用并暴露API。

apiVersion: apps/v1
kind: Deployment
metadata:
  name: timeout-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: timeout-app
  template:
    metadata:
      labels:
        app: timeout-app
    spec:
      containers:
      - name: timeout-app
        image: gcr.io/your-project-id/your-app
        ports:
        - containerPort: 8080

---
apiVersion: v1
kind: Service
metadata:
  name: timeout-app-service
spec:
  selector:
    app: timeout-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer

这里的targetPort需要与容器内应用监听的端口一致。通过设置type: LoadBalancer，Kubernetes会为服务创建一个外部IP，供客户端访问。

使用Spring Boot框架可以快速创建REST API。以下是一个示例：

**1. Maven 依赖**

首先，在`pom.xml`中添加Spring Boot的依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

**2. API 代码**

创建一个简单的控制器来处理请求：

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeoutController {

    @GetMapping("/api/timeout")
    public String handleRequest(@RequestParam(defaultValue = "0") int timeout) throws InterruptedException {
        // Convert timeout to milliseconds
        int delay = timeout * 1000;
        // Sleep for the specified time
        Thread.sleep(delay);
        return "Request completed after " + timeout + " seconds delay";
    }
}
```

这个控制器定义了一个`/api/timeout`端点，接收`timeout`参数，并在响应前暂停指定的时间。

**3. 启动应用**

创建一个主类来启动Spring Boot应用：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeoutApplication.class, args);
    }
}
```

### Python 实现

使用Flask框架，可以快速实现一个类似的API。

**1. 安装Flask**

首先，安装Flask：

```bash
pip install Flask
```

**2. API 代码**

```python
from flask import Flask, request
import time

app = Flask(__name__)

@app.route('/api/timeout')
def timeout():
    # Get the timeout parameter from the query string, default to 0
    timeout = int(request.args.get('timeout', 0))
    # Sleep for the specified time
    time.sleep(timeout)
    return f"Request completed after {timeout} seconds delay", 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

这个Flask应用定义了一个`/api/timeout`端点，接收`timeout`参数，并在响应前暂停指定的时间。

### 部署到GKE

无论使用Java还是Python实现，都可以将应用打包成Docker镜像，然后推送到容器注册表（如Google Container Registry）。接下来，可以通过Kubernetes的Deployment和Service资源在GKE中部署这个应用。这里简要概述一下步骤：

1. **构建Docker镜像**：

    对于Java项目，可以使用以下`Dockerfile`：

    ```dockerfile
    FROM openjdk:11-jre-slim
    COPY target/your-app.jar /app.jar
    ENTRYPOINT ["java", "-jar", "/app.jar"]
    ```

    对于Python项目，可以使用以下`Dockerfile`：

    ```dockerfile
    FROM python:3.9-slim
    WORKDIR /app
    COPY . /app
    RUN pip install -r requirements.txt
    CMD ["python", "app.py"]
    ```

2. **推送到容器注册表**：

    ```bash
    docker build -t gcr.io/your-project-id/your-app .
    docker push gcr.io/your-project-id/your-app
    ```

3. **在GKE中部署**：

    创建一个Kubernetes Deployment和Service来部署应用并暴露API。

    ```yaml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: timeout-app
    spec:
      replicas: 1
      selector:
        matchLabels:
          app: timeout-app
      template:
        metadata:
          labels:
            app: timeout-app
        spec:
          containers:
          - name: timeout-app
            image: gcr.io/your-project-id/your-app
            ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: timeout-app-service
spec:
  selector:
    app: timeout-app
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
```
这里的targetPort需要与容器内应用监听的端口一致。通过设置type: LoadBalancer，Kubernetes会为服务创建一个外部IP，供客户端访问。
