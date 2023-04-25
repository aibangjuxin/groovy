 
要获取 GKE Pod 占用的 GKE 节点磁盘大小，可以使用以下步骤：

1. 确认 GKE 集群的名称和区域/区域，以及要查询的 Pod 的名称。

2. 运行以下命令，获取 Pod 所在的节点名称：

   ```
   kubectl get pod <pod-name> -o=jsonpath='{.spec.nodeName}'
   ```

3. 运行以下命令，获取节点的磁盘使用情况：

   ```
   gcloud compute ssh <node-name> --zone=<zone/region> -- df -h
   ```

   在这里，`<node-name>` 是步骤 2 中获取的节点名称，`<zone/region>` 是集群的区域或区域。`df -h` 命令将显示节点上每个磁盘分区的使用情况。

   如果您只想查看特定磁盘分区的使用情况，可以使用以下命令：

   ```
   gcloud compute ssh <node-name> --zone=<zone/region> -- df -h /path/to/partition
   ```

   在这里，`/path/to/partition` 是要查询的分区的路径。

4. 在命令输出中查找要查询的分区，并查看其使用情况。

要统计每个 Pod 占用的磁盘大小，可以使用以下步骤：

1. 运行以下命令，获取 GKE 集群中所有 Pod 的名称：

   ```
   kubectl get pods --all-namespaces -o=jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}'
   ```

   这将返回一个包含所有 Pod 名称的列表，每个名称占一行。

2. 遍历步骤 1 中获取的所有 Pod 名称，对于每个 Pod，运行以下命令，获取它所在的节点名称：

   ```
   kubectl get pod <pod-name> -o=jsonpath='{.spec.nodeName}'
   ```

3. 对于每个 Pod，运行以下命令，获取它所在节点上的所有磁盘分区的使用情况：

   ```
   gcloud compute ssh <node-name> --zone=<zone/region> -- df -h
   ```

4. 在命令输出中查找与该 Pod 相关的磁盘分区，并查看其使用情况。您可以使用 `grep` 命令过滤输出，例如：

   ```
   gcloud compute ssh <node-name> --zone=<zone/region> -- df -h | grep /var/lib/docker/overlay2
   ```

   这将仅显示与 Docker Overlay2 存储驱动程序相关的分区。

5. 将每个 Pod 所在节点上所有相关分区的磁盘使用情况相加，得出该 Pod 的总磁盘占用量。

6. 将每个 Pod 的磁盘占用量汇总，以获取整个集群中每个 Pod 占用的磁盘大小。您可以使用命令行工具（如 `awk` 或 `sed`）来计算总和。

请注意，这种方法只会计算与 Pod 相关的存储使用情况，不包括 Pod 的其他资源使用情况（例如 CPU 和内存）。


如果您的 GKE 节点禁止 SSH 登录，可以使用以下方法查看节点的磁盘占用率：

1. 使用 `gcloud` 命令行工具连接到 GKE 集群。例如：

   ```
   gcloud container clusters get-credentials <cluster-name> --zone <zone/region>
   ```

2. 运行以下命令，获取节点的名称：

   ```
   kubectl get nodes
   ```

   在输出中，找到要查询的节点的名称。

3. 运行以下命令，获取节点的描述信息：

   ```
   kubectl describe node <node-name>
   ```

   在输出中，找到包含节点磁盘信息的部分，例如：

   ```
   Capacity:
   cpu:                4
   ephemeral-storage:  104857600Ki
   memory:             15641684Ki
   ```

   这将显示节点上 ephemeral-storage 容量的总大小，以 KiB 为单位。

4. 运行以下命令，获取节点上可用磁盘空间的使用情况：

   ```
   kubectl get node <node-name> -o jsonpath="{.status.allocatable['ephemeral-storage']}"
   ```

   这将返回节点上 ephemeral-storage 容量的可用空间大小，以 KiB 为单位。

5. 使用第 3 步中显示的节点磁盘信息，计算节点的磁盘使用率。例如，如果节点的 ephemeral-storage 容量为 104857600Ki，可用空间为 52428800Ki，则使用率为：

   ```
   (1 - (52428800 / 104857600)) * 100% = 50%
   ```

   这意味着节点的磁盘使用率为 50%。

请注意，这种方法仅适用于获取节点上可用磁盘空间的使用情况。如果您需要更详细的磁盘使用信息，例如每个文件系统的使用情况或每个进程的磁盘使用情况，您可能需要使用其他工具或方法。


以下是一个可以用来获取 GKE 集群中所有 Pod 占用的磁盘大小的 Shell 脚本示例。您可以将该脚本保存为 `.sh` 文件，然后在终端中执行。

```bash
#!/bin/bash

# 获取所有 Pod 名称
POD_NAMES=$(kubectl get pods --all-namespaces -o=jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}')

# 初始化总磁盘使用量
TOTAL_DISK_USAGE=0

# 遍历每个 Pod
for POD_NAME in ${POD_NAMES}; do
  # 获取 Pod 所在节点的名称
  NODE_NAME=$(kubectl get pod ${POD_NAME} -o=jsonpath='{.spec.nodeName}')

  # 获取节点上所有磁盘分区的使用情况
  DISK_USAGE=$(gcloud compute ssh ${NODE_NAME} --zone=<zone/region> -- df -h)

  # 计算 Pod 占用的磁盘大小
  POD_DISK_USAGE=$(echo ${DISK_USAGE} | grep /var/lib/docker/overlay2 | awk '{print $3}')

  # 将 Pod 磁盘使用量添加到总磁盘使用量中
  TOTAL_DISK_USAGE=$((${TOTAL_DISK_USAGE} + ${POD_DISK_USAGE}))

  # 输出每个 Pod 的磁盘使用量
  echo "Pod ${POD_NAME} uses ${POD_DISK_USAGE} disk space"
done

# 输出总磁盘使用量
echo "Total disk usage for all Pods: ${TOTAL_DISK_USAGE}"
```

请注意，此脚本中的 `<zone/region>` 应替换为您的 GKE 集群所在的区域或地区。此外，为了能够正常运行脚本，您需要具有运行 `gcloud` 和 `kubectl` 命令的权限，并且您的计算机上已经安装了这些工具。
在 `kubectl describe node` 命令的输出中，`ephemeral-storage` 的单位是 `Ki`. 

例如，如果 `ephemeral-storage` 的值为 `104857600Ki`，则表示节点上 ephemeral-storage 容量的总大小为 100 GB，以 KiB 为单位。

在 `kubectl describe node` 命令的输出中，`ephemeral-storage` 的 `allocatable` 值确实没有指定单位。这是因为 `allocatable` 中的 `ephemeral-storage` 值表示节点上 ephemeral-storage 可用容量的数量，而不是总容量。

`ephemeral-storage` 的 `allocatable` 值通常以字节数为单位，但在 `kubectl describe node` 的输出中，它没有明确的单位。所以如果 `ephemeral-storage` 的 `allocatable` 值为 `104857600`，则表示节点上 ephemeral-storage 可用容量的数量为 100 GB。


image size
如果您要统计 Google GKE 中所有 Pod 的镜像大小，
可以使用以下命令：

```bash
gcloud container images list --repository=gcr.io/[PROJECT_ID] --format='get(digest)' | while read digest; do
  gcloud container images describe gcr.io/[PROJECT_ID]/[IMAGE_NAME]@$digest --format='value(image_summary.fully_qualified_digest, image_summary.image_size_bytes/1048576)' | sed "s/gcr.io\//$HOSTNAME\//g"
done
```

这个命令会遍历 GCR 中项目 `[PROJECT_ID]` 
下的所有镜像，并获取它们的完全限定摘要符（fully qualified digest）和镜像大小。
然后，输出结果到终端上，其中包括每个镜像的完全限定摘要符、大小（以 MB 为单位）以及镜像所在的节点名称。

请注意，此命令需要您在执行之前替换命令中的
 `[PROJECT_ID]` 和 `[IMAGE_NAME]` 为实际的项目 ID 和镜像名称。


如果您无法登录到 GKE 节点并且需要获取 Pod 镜像大小的话，可以使用以下命令：

```bash
kubectl get pods --all-namespaces -o=jsonpath="{range .items[*]}{.metadata.name}{'\t'}{.spec.containers[*].image}{'\n'}{end}" | sort -u | while read pod_image; do
  size=$(gcloud container images describe $pod_image --format='value(image_summary.image_size_bytes)')
  printf "%s\t%s\n" "$pod_image" "$((size/1048576)) MB"
done
```

该命令将获取 GKE 集群中所有 Pod 中使用的镜像名称，并使用 `gcloud container images describe` 命令获取每个镜像的大小。然后，将结果输出到终端上。

输出结果将包括每个 Pod 镜像的名称和大小（以 MB 为单位），以制表符（`\t`）分隔。

How to check gcr image size .get image manifest all the layers and size sum size write a script

获取 GCR 中镜像的总大小，可以使用 gcloud 命令行工具来获取镜像的 digest，然后使用 curl 命令来获取镜像的 manifest，最后使用 jq 命令解析 manifest 中的大小信息并将它们相加。
以下是一个示例脚本，可以用来获取 GCR 中所有镜像的大小总和：

#!/bin/bash

# 设置变量
PROJECT_ID=[PROJECT-ID]
IMAGE_NAME=[IMAGE-NAME]
TAG=[TAG]
IMAGE_FULL_NAME=gcr.io/$PROJECT_ID/$IMAGE_NAME:$TAG

# 获取每个层的大小，并将其存储在一个数组中
LAYER_SIZES=($(curl -s -H "Authorization: Bearer $(gcloud auth print-access-token)" https://gcr.io/v2/$PROJECT_ID/$IMAGE_NAME/manifests/$TAG | jq -r '.layers[].size'))

# 计算所有层的大小之和
TOTAL_SIZE=$(echo "${LAYER_SIZES[@]}" | sed 's/ /+/g' | bc)

# 输出结果
echo "GCR Image Name: $IMAGE_FULL_NAME"
echo "Total Size: $TOTAL_SIZE bytes"
echo "Layer Sizes:"
for i in "${!LAYER_SIZES[@]}"; do
  echo "  Layer $i: ${LAYER_SIZES[$i]} bytes"
done

