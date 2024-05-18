
# summary

- prompt 
我想写这样一个Shell 脚本
我想要查看我运行环境中的所有的svc IP 信息,去查询我对应的DNS记录里面是否有这个IP,如果有,则输出到一个文件中,如果没有,则输出到另一个文件中,并且输出这个IP所在的namespace 和svc name.因为这个IP所对应的是svc就是要删除的
1 导出我工程下面的一个cloud dns zone 的所有记录到一个文件
2 获取所有的namespace 和 service下的Load balance类型是cluster ip 的svc 信息 . 信息包括namespace svc name 和 svc ip 并且输出到一个文件
3 获取所有的namespace 和 service下的Load balance类型是Loadbalancer 的svc 信息 . 信息包括namespace svc name 和 svc ip 和loadbalancer ip 并且输出到一个文件
4 我需要拿2和3步骤中,所有获取到的这些IP地址 去步骤1 中导出的文件中查找是否有相同的记录,如果有,则输出到一个文件中.有个信息这些IP是要要保留的
5 我需要拿2和3步骤中,所有获取到的这些IP地址 去步骤1 中导出的文件中查找是否有相同的记录,如果没有,则需要打印我这个IP所在的namespace 和 svc name.这部分IP是要删除的

                        
                    
                     
 
        

#!/bin/bash

# 1. Export all records from a Cloud DNS zone to a file
gcloud dns record-sets export records.yaml --zone=YOUR_ZONE_NAME

# 2. Get all cluster IP services and their IPs
kubectl get svc -A -l type=ClusterIP -o jsonpath='{range .items[*]}{.metadata.namespace}{"\t"}{.metadata.name}{"\t"}{.spec.clusterIP}{"\n"}{end}' > cluster_ips.txt

# 3. Get all LoadBalancer services and their IPs
kubectl get svc -A -l type=LoadBalancer -o jsonpath='{range .items[*]}{.metadata.namespace}{"\t"}{.metadata.name}{"\t"}{.spec.clusterIP}{"\t"}{.status.loadBalancer.ingress[0].ip}{"\n"}{end}' > loadbalancer_ips.txt

# 4. Find matching IPs in DNS records
grep -F -f cluster_ips.txt records.yaml > matching_ips.txt
grep -F -f loadbalancer_ips.txt records.yaml >> matching_ips.txt

# 5. Identify IPs to delete
comm -23 cluster_ips.txt matching_ips.txt > ips_to_delete_cluster_ip.txt
comm -23 loadbalancer_ips.txt matching_ips.txt > ips_to_delete_loadbalancer.txt

# Print IPs to delete with corresponding namespace and service name
while read line; do
  namespace=$(echo $line | awk '{print $1}')
  svc_name=$(echo $line | awk '{print $2}')
  ip=$(echo $line | awk '{print $3}')
  echo "Namespace: $namespace, Service Name: $svc_name, IP: $ip"
done < ips_to_delete_cluster_ip.txt

while read line; do
  namespace=$(echo $line | awk '{print $1}')
  svc_name=$(echo $line | awk '{print $2}')
  ip=$(echo $line | awk '{print $3}')
  loadbalancer_ip=$(echo $line | awk '{print $4}')
  echo "Namespace: $namespace, Service Name: $svc_name, Cluster IP: $ip, Load Balancer IP: $loadbalancer_ip"
done < ips_to_delete_loadbalancer.txt



好的,我可以帮助您编写一个Shell脚本来实现上述需求。以下是脚本的大致思路和代码:

```bash
#!/bin/bash

# 1. 导出 Cloud DNS Zone 的所有记录到文件
gcloud dns record-sets export my-zone.com --zone=my-zone --project=my-project > dns_records.txt

# 2. 获取所有 Cluster IP 类型的 Service 信息并输出到文件
kubectl get svc --all-namespaces -o=custom-columns=NAMESPACE:.metadata.namespace,NAME:.metadata.name,CLUSTER-IP:.spec.clusterIP | grep -v CLUSTER-IP > cluster_ips.txt

# 3. 获取所有 LoadBalancer 类型的 Service 信息并输出到文件
kubectl get svc --all-namespaces -o=custom-columns=NAMESPACE:.metadata.namespace,NAME:.metadata.name,CLUSTER-IP:.spec.clusterIP,LOAD-BALANCER-IP:.status.loadBalancer.ingress[0].ip | grep -v LOAD-BALANCER-IP > load_balancer_ips.txt

# 4. 查找需要保留的 IP 地址并输出到文件
grep -Ff <(awk '{print $3}' cluster_ips.txt load_balancer_ips.txt) dns_records.txt > keep_ips.txt

# 5. 查找需要删除的 IP 地址并输出到文件
awk 'FNR==NR{a[$3];next} !($3 in a)' <(awk '{print $3}' cluster_ips.txt load_balancer_ips.txt) dns_records.txt > delete_ips.txt
awk 'NR==FNR{a[$3]=$1" "$2;next} ($3 in a){print a[$3],$3}' cluster_ips.txt load_balancer_ips.txt delete_ips.txt > delete_svc_ips.txt
```

脚本解释:

1. 使用 `gcloud` 命令导出 Cloud DNS Zone 的所有记录到 `dns_records.txt` 文件。
2. 使用 `kubectl get svc` 命令获取所有 Cluster IP 类型的 Service 信息，并使用 `custom-columns` 输出 Namespace、Name 和 Cluster IP，最后将结果输出到 `cluster_ips.txt` 文件。
3. 使用 `kubectl get svc` 命令获取所有 LoadBalancer 类型的 Service 信息，并使用 `custom-columns` 输出 Namespace、Name、Cluster IP 和 LoadBalancer IP，最后将结果输出到 `load_balancer_ips.txt` 文件。
4. 使用 `grep` 和 `awk` 命令从 `cluster_ips.txt` 和 `load_balancer_ips.txt` 文件中提取所有 IP 地址，并在 `dns_records.txt` 文件中查找这些 IP 地址。如果找到，则将这些 IP 地址输出到 `keep_ips.txt` 文件。
5. 使用 `awk` 命令从 `cluster_ips.txt` 和 `load_balancer_ips.txt` 文件中提取所有 IP 地址，并在 `dns_records.txt` 文件中查找这些 IP 地址。如果没有找到，则将这些 IP 地址及其对应的 Namespace 和 Service Name 输出到 `delete_svc_ips.txt` 文件。

注意:

- 请将 `my-zone.com`、`my-zone` 和 `my-project` 替换为您实际的 Cloud DNS Zone 和 GCP 项目信息。
- 确保您已安装并配置了 `gcloud` 和 `kubectl` 命令行工具。
- 如果需要更多自定义或调整，可以根据实际需求修改相应的命令和参数。

# ChatGPT 实现

明白了，我们需要将脚本进一步完善，加入从response policy中导出记录并进行匹配的部分，并且要确保LoadBalancer类型的服务同时检查ClusterIP和LoadBalancer IP。

下面是更新后的脚本，包含以下内容：
1. 导出DNS区域记录。
2. 获取Kubernetes服务信息，包括ClusterIP和LoadBalancer IP。
3. 导出response policy记录。
4. 比较ClusterIP和LoadBalancer IP，分别输出匹配和不匹配的结果。

假设你已经配置好了`kubectl`和`gcloud`命令行工具，并且可以访问你的Kubernetes集群和Google Cloud DNS。

```sh
#!/bin/bash

# 设置变量
PROJECT_ID="your-google-cloud-project-id"
DNS_ZONE="your-dns-zone-name"
RESPONSE_POLICY_NAME="your-response-policy-name"
DNS_FILE="dns_records.txt"
RESPONSE_POLICY_FILE="response_policy_records.txt"
SVC_CLUSTER_IP_FILE="svc_cluster_ips.txt"
SVC_LOADBALANCER_IP_FILE="svc_loadbalancer_ips.txt"
MATCHED_IPS_FILE="matched_ips.txt"
UNMATCHED_IPS_FILE="unmatched_ips.txt"

# 导出DNS区域记录到文件
gcloud dns record-sets list --zone=$DNS_ZONE --project=$PROJECT_ID > $DNS_FILE

# 导出response policy记录到文件
gcloud dns response-policies rules list --response-policy=$RESPONSE_POLICY_NAME --project=$PROJECT_ID > $RESPONSE_POLICY_FILE

# 获取所有namespace下的ClusterIP类型的svc信息
kubectl get svc --all-namespaces -o json | jq -r '
.items[] |
select(.spec.type=="ClusterIP") |
"\(.metadata.namespace) \(.metadata.name) \(.spec.clusterIP)"' > $SVC_CLUSTER_IP_FILE

# 获取所有namespace下的LoadBalancer类型的svc信息
kubectl get svc --all-namespaces -o json | jq -r '
.items[] |
select(.spec.type=="LoadBalancer") |
"\(.metadata.namespace) \(.metadata.name) \(.spec.clusterIP) \(.status.loadBalancer.ingress[0].ip)"' > $SVC_LOADBALANCER_IP_FILE

# 初始化匹配和不匹配的文件
echo "" > $MATCHED_IPS_FILE
echo "" > $UNMATCHED_IPS_FILE

# 检查ClusterIP类型的svc IP是否在DNS记录和response policy中
while read -r namespace svc_name svc_ip; do
  if grep -q "$svc_ip" $DNS_FILE || grep -q "$svc_ip" $RESPONSE_POLICY_FILE; then
    echo "$namespace $svc_name $svc_ip" >> $MATCHED_IPS_FILE
  else
    echo "$namespace $svc_name $svc_ip" >> $UNMATCHED_IPS_FILE
  fi
done < $SVC_CLUSTER_IP_FILE

# 检查LoadBalancer类型的svc IP是否在DNS记录和response policy中
while read -r namespace svc_name cluster_ip loadbalancer_ip; do
  if grep -q "$cluster_ip" $DNS_FILE || grep -q "$cluster_ip" $RESPONSE_POLICY_FILE || \
     grep -q "$loadbalancer_ip" $DNS_FILE || grep -q "$loadbalancer_ip" $RESPONSE_POLICY_FILE; then
    echo "$namespace $svc_name $cluster_ip $loadbalancer_ip" >> $MATCHED_IPS_FILE
  else
    echo "$namespace $svc_name $cluster_ip $loadbalancer_ip" >> $UNMATCHED_IPS_FILE
  fi
done < $SVC_LOADBALANCER_IP_FILE

echo "匹配的IP地址已保存到 $MATCHED_IPS_FILE"
echo "不匹配的IP地址已保存到 $UNMATCHED_IPS_FILE"
```

### 更新脚本说明：

1. **变量设置**：设置项目ID、DNS区域名称、response policy名称和各种文件的名称。
2. **导出DNS区域记录和response policy记录**：分别使用`gcloud dns record-sets list`和`gcloud dns response-policies rules list`命令导出记录，并保存到文件中。
3. **获取ClusterIP类型的服务信息**：使用`kubectl get svc`命令获取所有命名空间下的ClusterIP服务信息，并使用`jq`处理JSON数据，提取命名空间、服务名称和服务IP，保存到`svc_cluster_ips.txt`文件中。
4. **获取LoadBalancer类型的服务信息**：同样使用`kubectl get svc`命令获取所有命名空间下的LoadBalancer服务信息，并提取命名空间、服务名称、ClusterIP和LoadBalancer IP，保存到`svc_loadbalancer_ips.txt`文件中。
5. **初始化匹配和不匹配的文件**：清空`matched_ips.txt`和`unmatched_ips.txt`文件，以确保结果不会混淆。
6. **检查ClusterIP的IP地址**：读取`svc_cluster_ips.txt`文件中的每一行，检查IP是否在DNS记录或response policy记录中，并根据结果将信息写入相应的文件。
7. **检查LoadBalancer的IP地址**：读取`svc_loadbalancer_ips.txt`文件中的每一行，检查ClusterIP和LoadBalancer IP是否在DNS记录或response policy记录中，并将信息写入相应的文件。

通过这个脚本，你可以对比DNS记录和response policy记录，确定哪些服务IP需要保留，哪些需要删除。确保根据实际环境修改变量值和检查命令，以适应你的具体需求。


下面是一个Shell脚本，用于完成你描述的任务。该脚本将导出DNS区域记录，获取Kubernetes服务信息，并将根据IP地址在DNS记录中查找匹配项，分别输出匹配和不匹配的结果。

假设你已经配置好了`kubectl`和`gcloud`命令行工具，并且可以访问你的Kubernetes集群和Google Cloud DNS。

```sh
#!/bin/bash

# 设置变量
PROJECT_ID="your-google-cloud-project-id"
DNS_ZONE="your-dns-zone-name"
DNS_FILE="dns_records.txt"
SVC_CLUSTER_IP_FILE="svc_cluster_ips.txt"
SVC_LOADBALANCER_IP_FILE="svc_loadbalancer_ips.txt"
MATCHED_IPS_FILE="matched_ips.txt"
UNMATCHED_IPS_FILE="unmatched_ips.txt"

# 导出DNS区域记录到文件
gcloud dns record-sets list --zone=$DNS_ZONE --project=$PROJECT_ID > $DNS_FILE

# 获取所有namespace下的ClusterIP类型的svc信息
kubectl get svc --all-namespaces -o json | jq -r '
.items[] |
select(.spec.type=="ClusterIP") |
"\(.metadata.namespace) \(.metadata.name) \(.spec.clusterIP)"' > $SVC_CLUSTER_IP_FILE

# 获取所有namespace下的LoadBalancer类型的svc信息
kubectl get svc --all-namespaces -o json | jq -r '
.items[] |
select(.spec.type=="LoadBalancer") |
"\(.metadata.namespace) \(.metadata.name) \(.status.loadBalancer.ingress[0].ip)"' > $SVC_LOADBALANCER_IP_FILE

# 初始化匹配和不匹配的文件
echo "" > $MATCHED_IPS_FILE
echo "" > $UNMATCHED_IPS_FILE

# 检查ClusterIP类型的svc IP是否在DNS记录中
while read -r namespace svc_name svc_ip; do
  if grep -q "$svc_ip" $DNS_FILE; then
    echo "$namespace $svc_name $svc_ip" >> $MATCHED_IPS_FILE
  else
    echo "$namespace $svc_name $svc_ip" >> $UNMATCHED_IPS_FILE
  fi
done < $SVC_CLUSTER_IP_FILE

# 检查LoadBalancer类型的svc IP是否在DNS记录中
while read -r namespace svc_name loadbalancer_ip; do
  if grep -q "$loadbalancer_ip" $DNS_FILE; then
    echo "$namespace $svc_name $loadbalancer_ip" >> $MATCHED_IPS_FILE
  else
    echo "$namespace $svc_name $loadbalancer_ip" >> $UNMATCHED_IPS_FILE
  fi
done < $SVC_LOADBALANCER_IP_FILE

echo "匹配的IP地址已保存到 $MATCHED_IPS_FILE"
echo "不匹配的IP地址已保存到 $UNMATCHED_IPS_FILE"
```

### 脚本说明：

1. **变量设置**：设置项目ID、DNS区域名称和各种文件的名称。
2. **导出DNS区域记录**：使用`gcloud dns record-sets list`命令导出DNS记录，并保存到`dns_records.txt`文件中。
3. **获取ClusterIP类型的服务信息**：使用`kubectl get svc`命令获取所有命名空间下的ClusterIP服务信息，并使用`jq`处理JSON数据，提取命名空间、服务名称和服务IP，保存到`svc_cluster_ips.txt`文件中。
4. **获取LoadBalancer类型的服务信息**：同样使用`kubectl get svc`命令获取所有命名空间下的LoadBalancer服务信息，并提取命名空间、服务名称和LoadBalancer IP，保存到`svc_loadbalancer_ips.txt`文件中。
5. **初始化匹配和不匹配的文件**：清空`matched_ips.txt`和`unmatched_ips.txt`文件，以确保结果不会混淆。
6. **检查ClusterIP的IP地址**：读取`svc_cluster_ips.txt`文件中的每一行，检查IP是否在DNS记录中，并根据结果将信息写入相应的文件。
7. **检查LoadBalancer的IP地址**：类似地，读取`svc_loadbalancer_ips.txt`文件中的每一行，检查IP是否在DNS记录中，并将信息写入相应的文件。

这个脚本可以帮助你实现对Kubernetes服务IP地址和DNS记录的检查，并将结果分类到匹配和不匹配的文件中。确保根据实际环境修改变量值和检查命令，以适应你的具体需求。

# Gemini 

