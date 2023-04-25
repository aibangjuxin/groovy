 
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