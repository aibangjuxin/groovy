- [Verify step](#verify-step)
    - [Step-by-Step Review:](#step-by-step-review)
    - [Debugging Gateway:](#debugging-gateway)
    - [Summary of Suggestions:](#summary-of-suggestions)
- [verify claude](#verify-claude)
- [Gateway install and configuration](#gateway-install-and-configuration)
  - [enable the GKE API](#enable-the-gke-api)
  - [create a gateway and supports https](#create-a-gateway-and-supports-https)
- [Ingress with Gateway](#ingress-with-gateway)


# Verify step 
### Step-by-Step Review:

1. **Enable GKE API:**
   ```bash
   gcloud services enable container.googleapis.com
   ```
   ✅ This is correct.

2. **Update the GKE cluster:**
   ```bash
   gcloud container cluster update CLUSTER_NAME --location LOCATION --gateway-api=standard
   ```
   ✅ Correct for enabling the Gateway API on your cluster.

3. **Verify the cluster has Gateway API enabled:**
   ```bash
   gcloud container cluster describe CLUSTER_NAME --location LOCATION --format='yaml(gatewayConfig.enabled)'
   ```
   ✅ This command will show if Gateway API is enabled on your cluster.

4. **Verify the Gateway Class:**
   ```bash
   kubectl get gatewayclasses
   kubectl api-resources | grep gatewayclasses
   kubectl api-resources | grep way
   ```
   ✅ Your commands look fine for checking the available gateway classes and API resources.

5. **Configure a proxy-only subnet:**
   - Listing and describing the subnet with `gcloud` commands look correct.
   - **Note**: Ensure the subnet is correctly configured for proxy-only use.

6. **Create a Gateway with HTTPS support:**
   - Creating and verifying the TLS secret is fine:
     ```bash
     kubectl create secret tls tls-secret --key=path/to/key.pem --cert=path/to/cert.pem --namespace=default
     ```
   - Your `Gateway` configuration YAML looks correct. It uses `gke-l7-rilb` and sets up an HTTPS listener:
     ```yaml
     listeners:
     - name: https
       protocol: HTTPS
       port: 443
       tls:
         mode: Terminate
         secretName: tls-secret
     ```

7. **Deploy a sample API:**
   ✅ The deployment and service configuration for `my-aibang-app` looks correct. You are using `ClusterIP` and setting the port to 443 with HTTPS.

8. **Create health check policies:**
   ✅ The `HTTPHealthCheckPolicy` configuration is correct, specifying HTTPS health checks on port `8443`.

9. **Create HTTPRoute:**
   - Your route configuration seems fine, but you might want to ensure the `parentRefs` section has the right `group` specified if required by your configuration:
     ```yaml
     parentRefs:
     - kind: Gateway
       name: internal-https
     ```

10. **Testing the Gateway:**
    - Testing with curl commands looks good. Ensure you replace `GATEWAY_IP_ADDRESS` with the correct IP obtained from the Gateway status.
    - **Fix**: The correct IP retrieval command should be:
      ```bash
      kubectl get gateways.networking.k8s.io internal-https --namespace=default -o jsonpath='{.status.addresses[0].value}'
      ```

### Debugging Gateway:
- The debugging steps for checking logs and backend services are well-structured. You can track health check logs and verify other components.

### Summary of Suggestions:
- Ensure the gateway name is consistent in all verification steps.
- Verify if the `parentRefs` in `HTTPRoute` need an additional group field.
- Double-check that the proxy-only subnet is properly configured.
- Correct the retrieval of the Gateway IP when running the `curl` commands.

These adjustments should improve the overall process and ensure successful configuration!


# verify claude 

1. 启用 GKE API 和更新集群:
   您的步骤是正确的。确保替换 CLUSTER_NAME 和 LOCATION 为您的实际集群名称和位置。

2. 验证集群已启用 Gateway:
   这些命令都是正确的，可以帮助您确认 Gateway API 已成功启用。

3. 配置代理子网:
   您列出了正确的命令来查看和描述子网。确保 "project-cinternal-vpc2" 是您的实际 VPC 名称。

4. 创建 Gateway 和支持 HTTPS:
   
   a. 创建 TLS 密钥:
   这一步骤正确。确保您有 .pem 文件的正确路径。

   b. 创建 Gateway:
   您的 Gateway 配置看起来正确。确保 `gke-l7-rilb` 类确实存在并已启用。

5. 部署示例 API:
   您的部署和服务配置基本正确，但有几点需要注意：
   - 确保镜像 `gcr.io/google-samples/hello-app:1.0` 支持 HTTPS 和 8443 端口。
   - 服务的 targetPort 应该与容器的端口匹配（8443）。

6. 创建健康检查策略:
   您的 HTTPHealthCheckPolicy 配置看起来正确。确保您的应用确实有 `/healthz` 端点。

7. 创建 HTTPRoute:
   您的 HTTPRoute 配置正确。它正确地引用了之前创建的 Gateway 和服务。

8. 测试 Gateway:
   您的测试命令是正确的。确保替换 GATEWAY_IP_ADDRESS 为实际的 Gateway IP。

9. 调试 Gateway:
   您列出的调试命令都很有用。确保替换项目 ID 和区域为您的实际值。

总体来说，您的步骤看起来是正确的，并且遵循了 Google Cloud 文档的建议。以下是一些额外的建议：

1. 确保所有的命名空间一致。您的例子中都使用了 "default" 命名空间，这很好。

2. 在创建 Gateway 和 HTTPRoute 之前，确保 GKE 集群已经完全更新并启用了 Gateway API。

3. 对于健康检查，确保您的应用确实在 8443 端口上提供了 HTTPS 服务，并有 `/healthz` 端点。

4. 在测试时，可能需要等待几分钟让所有资源完全创建和配置。

5. 如果遇到问题，使用 `kubectl describe` 命令查看详细的资源状态和事件。

6. 考虑使用 `kubectl get events --sort-by=.metadata.creationTimestamp` 来查看集群事件，这可能有助于诊断问题。

如果您在实施过程中遇到任何具体问题，请随时提供更多细节，我会很乐意帮助您进一步排查。



# Gateway install and configuration
- https://cloud.google.com/kubernetes-engine/docs/how-to/deploying-gateways
## enable the GKE API
```bash
gcloud services enable container.googleapis.com
```
- update the gke cluster
```bash
gcloud container cluster update CLUSTER_NAME --location LOCATION --gateway-api=standard
```
- verify the cluster enabled the gateway
```bash
gcloud container cluster describe CLUSTER_NAME --location LOCATION --format='yaml(gatewayConfig.enabled)'
```
- verify cluster enable gateway class 
```bash
kubectl get gatewayclasses
kubectl api-resources | grep gatewayclasses
kubectl api-resources | grep way
```
- `gke-l7-rilb` we using this gateway classs
- configure a proxy only subnet
- `gcloud compute networks subnets list --filter="network:project-cinternal-vpc2"`
- `gcloud compute networks subnets describe --region=us-central1 --subnet=subnet-proxy-only project-cinternal-vpc2`

## create a gateway and supports https 

1. create a tls secret
```bash
kubectl create secret tls tls-secret --key=path/to/key.pem --cert=path/to/cert.pem --namespace=default
```
2. verify the tls secret
```bash
kubectl get secret tls-secret --namespace=default
```
3. create a gateway and name it internal-https 
```bash
cat <<EOF | kubectl apply -f -
kind: Gateway
apiVersion: gateway.networking.k8s.io/v1beta1
metadata:
  name: internal-https
  namespace: default
spec:
  gatewayClassName: gke-l7-rilb
  listeners:
  - name: https
    protocol: HTTPS
    port: 443
    allowedRoutes:
      kinds:
      - kind: HTTPRoute
    tls:
      mode: Terminate
      secretName: tls-secret
EOF
```
4. verify the gateway 
```bash
kubectl get gateway internal-https --namespace=default
NAME          CLASS    ADDRESS      PROGRMMED   AGE
internal-https   gke-l7-rilb        192.168.100.1   True        10m
```
4.1 describe the gateway
```bash
kubectl describe gateway internal-https --namespace=default
```
4.2 check the gateway status and 
- gatewayclasses `gke-l7-rilb` is enabled 对应的是哪部应用负载均衡器
- listeners `https` is enabled 对应的是https协议的监听端口
- tls `tls-secret` is enabled 对应的是https协议的证书配置
5. deploy a sample api and Using https 
```bash
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-aibang-app
  labels:
    app: my-aibang-app
    spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-aibang-app
  template:
    metadata:
      labels:
        app: my-aibang-app
    spec:
      containers:
      - name: my-aibang-app
        image: gcr.io/google-samples/hello-app:1.0
        ports:
        - containerPort: 8443
---
apiVersion: v1
kind: Service
metadata:
  annotations:
    cloud.google.com/load-balancer-type: "Internal"
  name: my-aibang-app-service
  labels:
    app: my-aibang-app
  namespace: default
spec:
  type: ClusterIP
  ports:
  - port: 443
    protocol: TCP
    targetPort: 8443
    appProtocol: HTTPS
  selector:
    app: my-aibang-app
---
```
6. create a health check policies for the gateway
- 指定负载均衡器必须使用HTTPS协议的健康检查与后端Pod通信
```bash
cat <<EOF | kubectl apply -f -
apiVersion: networking.gke.io/v1
kind: HTTPHealthCheckPolicy
metadata:
  name: my-aibang-app-health-check
  namespace: default
spec:
  default:
    logConfig:
      enable: true
    config:
      type: HTTPS
      httpsHealthCheck:
        port: 8443
        requestPath: /healthz
    targetRef:
      group: ""
      kind: Service
      name: my-aibang-app-service
EOF
```
6.1 get svs status and endpoints
```bash
kubectl get svc my-aibang-app-service --namespace=default
NAME                   TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
my-aibang-app-service   ClusterIP   10.10.10.10   <none>        443/TCP    10m
```
```bash
kubectl get endpoints my-aibang-app-service --namespace=default
NAME                   ENDPOINTS           AGE
my-aibang-app-service   10.10.10.11:8443   10m
```
6.2 get HTTPHealthCheckPolicy status
```bash
kubectl get httphealthcheckpolicy my-aibang-app-health-check --namespace=default
NAME                     AGE
my-aibang-app-health-check   10m
```
6.3 describe the HTTPHealthCheckPolicy
```bash
kubectl describe httphealthcheckpolicy my-aibang-app-health-check --namespace=default
```


7. create a httproute for the gateway
```bash
cat <<EOF | kubectl apply -f -
apiVersion: gateway.networking.k8s.io/v1beta1
kind: HTTPRoute
metadata:
  name: my-aibang-app-route
  namespace: default
  labels:
    gateway: internal-https
spec:
  parentRefs:
  - kind: Gateway
    name: internal-https
  hostnames:
  - "www.aibang.com"
  rules:
  - backendRefs:
    - name: my-aibang-app-service
      port: 443
EOF
```
- Detail the httproute
    - the rules backendRefs.name is my-aibang-app-service, which is the service name we created before
    - the rules backendRefs.port is 443, which is the service port we created before
    - the hostnames is www.aibang.com, which is the domain name we want to access the service
    - the parentRefs.name is internal-https, which is the gateway we created before
7.1 get httproute status
- `kubectl get httproute my-aibang-app-route --namespace=default`
```bash
NAME      HOSTNAMES       AGE
my-aibang-app-route   ["www.aibang.com"]   10m
```
7.3 get httproute,gateway,svc status
- `kubectl get httproute,gateway,svc --namespace=default`
- get internla-https ip address
- `kubectl get gateways.networking.k8s.io internal-https --namespace=default -o jsonpath='{.status.addresses[0].value}'`


8. test the gateway
- `curl -k -H "Host: www.aibang.com" https://GATEWAY_IP_ADDRESS`
- `curl -k -H "Host: www.aibang.com" https://192.168.100.1`
- `curl -k -H "Host: www.aibang.com" https://192.168.100.1/healthz`
- `curl -k -H "Host: www.aibang.com" https://192.168.100.1/version/.well-known/health`


9. How to debug the gateway
- get HealthCheckPolicy logs
```bash 
gcloud logging read 'logName:healthchecks' --freshness=10m --project project-cinternal --format=json |jq '.[] | {
    targetIp: .jsonPayload.healthCheckProbeResult.targetIp,
    probeResultText: .jsonPayload.healthCheckProbeResult.probeResultText,
    detailedHealthState: .jsonPayload.healthCheckProbeResult.detailedHealthState,
    timestamp}'
```
- gcloud compute forwarding-rules list --filter="name=gke-internal-https-default-my-aibang-app-route" --project project-cinternal
- gcloud compute backend-services describe --region=us-central1 gke-internal-https-default-my-aibang-app-route --project project-cinternal
- gcloud compute url-maps list 
    - gcloud compute url-maps describe --region=us-central1 gke-internal-https-default-my-aibang-app-route-map --project project-cinternal
- gcloud compute target-http-proxies list --filter="name=gke-internal-https-default-my-aibang-app-route-proxy" --project project-cinternal
- gcloud compute health-checks list --filter="name=gke-internal-https-default-my-aibang-app-route-health-check" --project project-cinternal
    - gcloud compute health-checks describe --region=us-central1 gke-internal-https-default-my-aibang-app-route-health-check --project project-cinternal




# Ingress with Gateway


https://cloud.google.com/kubernetes-engine/docs/how-to/internal-load-balance-ingress
https://cloud.google.com/kubernetes-engine/docs/how-to/deploying-gateways

我的GKE环境里面现在按照这2个文档部署了对应的服务,但是我现在想要用Gateway方式来替换我的GKE ingress,我想了解,最大的区别是什么,

Key Differences between Ingress and Gateway in GKE
You're interested in understanding the major differences between using Gateway and Ingress in your GKE environment. While both are used for traffic management, Gateway offers a more modern and feature-rich approach. Here's a breakdown of the key distinctions:
* Abstraction Level: Ingress, a core Kubernetes resource, works at a higher level of abstraction. It relies on Ingress Controllers, which are often tied to specific load balancing solutions, to translate Ingress rules into concrete configurations. Gateway, being a newer API, provides a more granular and flexible way to manage traffic. It introduces new objects like GatewayClass, Gateway, HTTPRoute, etc. allowing for more control and customisation.
* Policy-Based Configuration: One of the most significant advantages of Gateway is its use of policies for configuration. You can attach policies to Gateways, Services, or ServiceImports to tailor traffic routing, security, and health checks without needing to recreate these resources. Ingress, on the other hand, often requires modifying the Ingress resource itself or relying on annotations specific to the Ingress Controller, making it less flexible and potentially requiring redeployments.
* Integration with Google Cloud Armor and IAP: Gateway allows seamless integration with Google Cloud Armor for enhanced backend security through GCPBackendPolicy. It also supports configuration of IAP for robust access control. These features often require more complex workarounds with Ingress.
* Granular Traffic Management: Gateway provides fine-grained control over traffic parameters like session affinity, connection draining timeouts, and access logging sampling rates, configured directly through policies. While some of these features might be available through Ingress annotations depending on the Ingress Controller, Gateway provides a standardised approach.
Important Considerations for Migration:
The sources primarily focus on Gateway's capabilities and configuration, and don't directly compare it with Ingress for migration. You should consider these points:
* Effort and Complexity: Migrating from Ingress to Gateway involves changes to your infrastructure configuration and potentially your application deployment workflows. Assess the complexity and resources required for this migration.
* Feature Parity: While Gateway offers a richer feature set, ensure it supports all functionalities provided by your existing Ingress setup or if suitable workarounds exist.
* Learning Curve: Familiarize yourself with Gateway's concepts, objects, and configuration patterns, as it differs from the Ingress model.
Transitioning to Gateway can bring enhanced flexibility and control over your traffic routing and security. However, carefully evaluating the migration path, considering the points above, and potentially consulting the Google Cloud documentation for specific Ingress-to-Gateway migration guidance will ensure a smooth transition.

