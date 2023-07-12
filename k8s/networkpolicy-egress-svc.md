- [summary](#summary)
- [flow](#flow)
- [Detail networkpolicy](#detail-networkpolicy)
  - [The service](#the-service)
  - [2 ingress for namespaceB](#2-ingress-for-namespaceb)
  - [3 namespaceA egress](#3-namespacea-egress)
    - [summary](#summary-1)
# summary
namespaceA == rt namespace

namespaceB == proxy namespace
# flow

namespaceA ==> namespace B
# Detail networkpolicy
GKE network policy 我配置允许一个namespaceA egress 到另一个namespacesB 的load balance 3128端口
要求namespaceA 内的源Pod必须有destinationKey: destinationValue 这个标签,它才能连接我的namespaceB 的kind: Service
我的默认规则已经是禁止了所有的Ingress和Egress
## The service
Squid Service
这个部分定义了一个名为 "proxy_name-service" 的 Service，在 namespaceB 中。

它使用了一个内部负载均衡器，并将端口 3128 的流量转发到匹配标签为 "app: proxy_name" 的 Pod。

这个规则用于在 namespaceB 中创建一个可从其他命名空间访问的 Squid 代理服务。

1 The service
```yaml
echo "kubectl apply squid service"
kubectl apply -f - <<EOF
apiVersion: v1
kind: Service
metadata:
  annotations:
    cloud.google.com/load-balancer-type: "Internal"
  labels:
    type: squid-services
    destinationKey: destinationValue
  name: proxy_name-service
  namespace: namespaceB
spec:
  ports:
    - port: 3128
      protocol: TCP
      targetPort: 3128
  selector:
    app: proxy_name
  type: LoadBalancer
EOF
```


## 2 ingress for namespaceB  
NamespaceB Ingress Network Policy

这个规则定义了一个名为 "allow-node-ingress-squid" 的网络策略，限制了进入 namespaceB 的流量。

它允许来自 CIDR 为 192.168.192.0/22 的 IP 地址块的流量访问端口 3128 和 443（TCP 协议）。

该策略只适用于具有标签 "destinationKey: destinationValue" 的 Pod。这个规则确保了只有满足条件的流量可以进入 namespaceB。

在我这个场景中主要是满足许Node之间的IP跳转访问 3128

```yaml
  kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-node-ingress-squid
  namespace: namespaceB 
spec:
  ingress:
  - from:
    - ipBlock:
        cidr: 192.168.192.0/22 # node ip range 允许Node之间的IP跳转访问 3128
    ports:
    - port: 3128
      protocol: TCP
    - port: 443
      protocol: TCP
  podSelector:
    matchLabels:
      destinationKey: destinationValue
  policyTypes:
  - Ingress
EOF
```


## 3 namespaceA egress 
namespaceA Egress Network Policy

这个规则定义了一个名为 "allow-rt-egress-proxy-microsoft" 的网络策略，用于限制 namespaceA 中的出口流量。

它允许具有标签 "destinationKey: destinationValue" 的 Pod 访问匹配标签为 "type: namespaceB" 的 namespaceB 中的任何 Pod，目标端口为 3128。

该策略将限制流量仅从具有特定标签的 Pod 流出，并且只允许访问特定的命名空间。
```yaml
echo "creating Pod->squid egress for DRN"
  kubectl apply -f - <<EOF
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-rt-egress-proxy-microsoft
  namespace: namespaceA
spec:
  egress:
  - to:
    - namespaceSelector: # 这个字段用于选择特定的命名空间作为目标
        matchLabels:
          type: namespaceB # 这个字段用于指定一个标签选择器，选择具有特定标签的命名空间。在这种情况下，选择具有标签 "type: namespaceB" 的命名空间作为目标。
      podSelector:
        matchLabels: # 这个字段用于指定一个标签选择器，选择具有特定标签的 Pod。在这种情况下，选择具有标签 "destinationKey: destinationValue" 的 Pod 作为目标。
          destinationKey: destinationValue # 访问namespaceB中标签为"destinationKey: destinationValue"的服务的3128端口
    ports:
      - port: 3128
  podSelector: # 这个字段用于选择应用该策略的源 Pod 
    matchLabels: # 这个字段用于指定一个标签选择器，选择具有特定标签的 Pod。在这种情况下，选择具有标签 "destinationKey: destinationValue" 的 Pod
      destinationKey: destinationValue # namespaceA中pod 具有 destinationKey: destinationValue 标签的 Pod 
  policyTypes:
    - Egress # 这个字段表示这是一个出口策略，即控制 Pod 发送流量的规则


上述 NetworkPolicy 中，namespaceSelector 和 podSelector 字段用于选择目标 Pod。
在您的配置中，namespaceSelector 选择了运行在 namespaceB 中的 Pod，

而 podSelector 选择了具有 destinationKey: destinationValue 标签的 Pod。
这意味着您的 Pod 必须同时满足这两个选择器才能被允许进行出站连接 这个是针对A namespace 而已的

```
### summary 

1. 您在namespaceB中创建了一个Squid代理的Service,类型是Internal Load Balancer,端口是3128。
2. 在namespaceB中创建了一个NetworkPolicy,允许来自192.168.192.0/22段的流量访问Squid服务的3128和443端口。这里是允许外部/我这里立即为内部Node跳转 访问Squid服务。
3. 在namespaceA中创建了一个NetworkPolicy,允许namespaceA中pod访问namespaceB中标签为"destinationKey: destinationValue"的服务的3128端口。这里是允许namespaceA访问namespaceB中的Squid服务。
总体来说,namespaceA可以通过3128端口访问namespaceB中的Squid服务,其他的访问默认都是被禁止的

对 GKE 网络策略的配置有几点建议:
1. Load Balancer 的 IP 在 GKE 中是动态分配的,所以如果要允许访问 Load Balancer,需要使用其 Service 名称,而不是 IP。
2. 不同 Namespace 之间默认是禁止访问的,所以需要显式配置规则允许跨 Namespace 访问。
3. 从一个 Namespace 访问另一个 Namespace 时,需要在目标 Namespace 配置 ingress 规则,在源 Namespace 配置 egress 规则。



根据您的要求，您希望在 namespaceA 中配置一个出口规则，允许通过 `namespaceSelector` 指定到 `namespaceB`。下面是一个符合您要求的 namespaceA 的出口规则的示例：

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: allow-egress-namespaceB
  namespace: namespaceA
spec:
  egress:
  - to:
    - namespaceSelector:
        matchLabels:
          name: namespaceB
    ports:
    - port: 3128
      protocol: TCP
  podSelector:
    matchLabels:
      destinationKey: destinationValue
  policyTypes:
  - Egress
```

解释如下：

- `metadata`：指定网络策略的名称为 "allow-egress-namespaceB"，所属命名空间为 `namespaceA`。

- `spec`：定义了网络策略的规范和规则。

  - `egress`：定义了出口规则。

    - `to`：指定出口流量的目标规则。

      - `namespaceSelector`：通过标签选择器来选择目标命名空间。

        - `matchLabels`：选择具有标签 "name: namespaceB" 的命名空间作为目标。

    - `ports`：定义允许的目标端口。

      - `port`：指定目标端口号为 3128。
      - `protocol`：指定协议为 TCP。

  - `podSelector`：通过标签选择器来选择应用该策略的源 Pod。

    - `matchLabels`：选择具有标签 "destinationKey: destinationValue" 的 Pod。

  - `policyTypes`：指定策略类型为 Egress，即出口规则。

这个规则将允许在 namespaceA 中具有标签 "destinationKey: destinationValue" 的源 Pod 连接到具有标签 "name: namespaceB" 的目标命名空间中的端口 3128。 
