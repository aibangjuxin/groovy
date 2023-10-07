# reference
https://cloud.google.com/armor/docs/security-policy-overview
The following diagram illustrates the location of global external Application Load Balancers, classic Application Load Balancers, the Google network, and Google data centers.
https://cloud.google.com/static/armor/images/ip-bl-wl-1.svg
The following diagram illustrates the location of global external Application Load Balancers, classic Application Load Balancers, the Google network, and Google data centers.


# need prepare 
我想要创建一个GCP工程里面的security policy 也就是cloud armor 。而且这个是基于HTTPS的backend services . 对于我的这个创建要求我需要了解创建的顺序都有哪些？
- 比如instance group
- target-https-proxy
  - URL Map
  - ssl-certificates
- forwarding-rules
- backend-services
- Health check 
## Detail introduction
要创建基于HTTPS的后端服务的安全策略（Cloud Armor）并配置相关资源，你需要按照以下顺序进行创建：

1. 创建实例组（Instance Group）：这是你的后端服务的集合，可以是虚拟机实例组、容器实例组或网络终点组（NEG）。

2. 创建URL映射（URL Map）：URL映射将传入的请求映射到后端服务。你可以定义路径匹配规则、代理规则和重定向规则等。

3. 创建目标HTTPS代理（Target HTTPS Proxy）：目标HTTPS代理终止传入的HTTPS请求，并将其转发到后端服务。

4. 配置SSL证书（SSL Certificates）：如果你的HTTPS代理需要使用SSL/TLS加密，你需要为该代理配置相应的SSL证书。

5. 创建转发规则（Forwarding Rules）：转发规则定义了传入请求的入口点，并指定了目标HTTPS代理。

6. 创建后端服务（Backend Services）：在后端服务中，你可以指定实例组、负载均衡策略、会话保持等配置。

7. 创建健康检查（Health Check）：健康检查用于验证后端服务的可用性和状态。你可以选择HTTP或HTTPS协议进行健康检查。

创建这些资源的顺序可以确保它们之间的依赖关系得到满足。请注意，具体的创建步骤可能因为你的特定要求和GCP控制台的更新而有所变化。在创建这些资源之前，你应该熟悉GCP文档中相关服务的详细配置和选项，以确保你的安全策略能够按预期工作。

当创建GCP工程中的Cloud Armor安全策略时，以下是每个步骤的详细创建命令和参数，以及对每个参数的解释：

1. 创建实例组（Instance Group）：
```
gcloud compute instance-groups managed create INSTANCE_GROUP_NAME \
    --size=SIZE \
    --template=INSTANCE_TEMPLATE
```
- `INSTANCE_GROUP_NAME`：实例组的名称。
- `SIZE`：实例组的大小，即包含的实例数量。
- `INSTANCE_TEMPLATE`：实例组所使用的实例模板。

2. 创建URL映射（URL Map）：
```
gcloud compute url-maps create URL_MAP_NAME \
    --default-service=BACKEND_SERVICE
```
- `URL_MAP_NAME`：URL映射的名称。
- `BACKEND_SERVICE`：默认的后端服务，即转发请求的目标。

3. 创建目标HTTPS代理（Target HTTPS Proxy）：
```
gcloud compute target-https-proxies create TARGET_HTTPS_PROXY_NAME \
    --url-map=URL_MAP_NAME \
    --ssl-certificates=SSL_CERTIFICATE
```
- `TARGET_HTTPS_PROXY_NAME`：目标HTTPS代理的名称。
- `URL_MAP_NAME`：目标HTTPS代理绑定的URL映射。
- `SSL_CERTIFICATE`：用于SSL/TLS加密的SSL证书。

4. 配置SSL证书（SSL Certificates）：
```
gcloud compute ssl-certificates create SSL_CERTIFICATE_NAME \
    --certificate=CERTIFICATE_FILE \
    --private-key=PRIVATE_KEY_FILE
```
- `SSL_CERTIFICATE_NAME`：SSL证书的名称。
- `CERTIFICATE_FILE`：SSL证书文件的路径。
- `PRIVATE_KEY_FILE`：私钥文件的路径。

5. 创建转发规则（Forwarding Rules）：
```
gcloud compute forwarding-rules create FORWARDING_RULE_NAME \
    --target-https-proxy=TARGET_HTTPS_PROXY_NAME \
    --ports=PORT_RANGE
```
- `FORWARDING_RULE_NAME`：转发规则的名称。
- `TARGET_HTTPS_PROXY_NAME`：转发规则绑定的目标HTTPS代理。
- `PORT_RANGE`：转发规则监听的端口范围。

6. 创建后端服务（Backend Services）：
```
gcloud compute backend-services create BACKEND_SERVICE_NAME \
    --protocol=PROTOCOL \
    --health-checks=HEALTH_CHECK_NAME \
    --global
gcloud compute backend-services create BACKEND_SERVICE_NAME \
    --global \
    --protocol HTTPS \
    --port 443 \
    --health-checks HEALTH_CHECK_NAME \
    --load-balancing-scheme INTERNAL_SELF_MANAGED \
    --timeout=60s \
    --connection-draining-timeout 60s

以下是上述命令中各参数的解释和示例：

- `BACKEND_SERVICE_NAME`：您要创建的后端服务的名称。

- `--global`：指定后端服务是全局的，这意味着它可以在多个区域使用。

- `--protocol HTTPS`：指定协议为 HTTPS。

- `--port 443`：指定端口号为 443，这是 HTTPS 默认的端口。

- `--health-checks HEALTH_CHECK_NAME`：指定与后端服务关联的健康检查的名称。您需要事先创建一个健康检查并将其名称替换为 `HEALTH_CHECK_NAME`。

- `--load-balancing-scheme INTERNAL_SELF_MANAGED`：指定负载均衡方案为 INTERNAL_SELF_MANAGED。这适用于内部负载均衡。

- `--timeout=60s`：指定超时时间为 60 秒。

- `--connection-draining-timeout 60s`：指定连接排放超时时间为 60 秒。

请替换 `BACKEND_SERVICE_NAME` 和 `HEALTH_CHECK_NAME` 为您的实际名称。执行此命令后，您将创建一个名为 `BACKEND_SERVICE_NAME` 的 HTTPS 后端服务，该服务将根据配置的健康检查与负载均衡器一起使用。
```
- `BACKEND_SERVICE_NAME`：后端服务的名称。
- `PROTOCOL`：后端服务使用的协议，例如HTTP或HTTPS。
- `HEALTH_CHECK_NAME`：用于健康检查的健康检查器的名称。

7. 创建健康检查（Health Check）：
```
gcloud compute health-checks create HEALTH_CHECK_NAME \
    --protocol=PROTOCOL \
    --port=PORT \
    --request-path=REQUEST_PATH
```
- `HEALTH_CHECK_NAME`：健康检查的名称。
- `PROTOCOL`：健康检查使用的协议，例如HTTP或HTTPS。
- `PORT`：健康检查监听的端口。
- `REQUEST_PATH`：用于健康检查的请求路径。


