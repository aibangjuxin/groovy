K8S中使用Ingress进行流量管理的架构流程如下:

1. 开发人员编写Ingress资源清单,定义路由规则,并将其部署到K8S集群中。

2. K8S集群中的Ingress Controller部署会监听Ingress资源的变化。一旦发现新增的Ingress资源,Ingress Controller就会根据其定义的规则配置负载均衡器(如Nginx)。

3. 用户访问K8S集群的入口(如NodePort、LoadBalancer),请求会被转发到Ingress Controller部署的负载均衡器上。

4. 负载均衡器根据Ingress规则对请求进行转发,并最终转发到指定的服务上。

5. 服务根据请求中指定的路径和规则,将请求转发到后端的Pod。

6. Pod接收到请求后,访问本地容器并返回结果。

7. 响应会依次返回给负载均衡器、入口和最终用户。

流程图如下:
``` mermaid 
graph LR

ingress(Ingress资源)-->ingress-controller(Ingress Controller)
ingress-controller --> nginx(负载均衡器Nginx)
nginx --> service1(Service 1)
nginx --> service2(Service 2)
service1 --> pod1((Pod 1))
service2 --> pod2((Pod 2))

user --> 入口
入口 --> nginx
nginx --> pod1((Pod 1)) & pod2((Pod 2))
pod1 & pod2 -->response 

``` 

综上,Ingress Controller通过监听Ingress资源的变化,然后动态配置负载均衡器,实现外部请求到达K8S内部服务的流量管理。Ingress和Ingress Controller之间通过K8S API进行协调。