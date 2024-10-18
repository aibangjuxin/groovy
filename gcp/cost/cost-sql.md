- enable cost allocation

````
# 配置 GKE 集群的资源使用导出
gcloud container clusters update CLUSTER_NAME \
    --resource-usage-bigquery-dataset=DATASET_NAME \
    --enable-network-egress-metering \
    --enable-resource-consumption-metering

# 验证设置
gcloud container clusters describe CLUSTER_NAME \
    --format="value(resourceUsageExportConfig)"
    你是对的，正确的命令应该包含多个标志，用于启用资源使用的 BigQuery 数据集和计量功能。参考命令如下：

```bash
gcloud container clusters update CLUSTER_NAME \
    --resource-usage-bigquery-dataset=DATASET_NAME \
    --enable-network-egress-metering \
    --enable-resource-consumption-metering
````

### 解释

- `--resource-usage-bigquery-dataset=DATASET_NAME`：将集群的资源使用情况（包括 CPU、内存、网络带宽等）发送到指定的 BigQuery 数据集（即 `DATASET_NAME`）。
- `--enable-network-egress-metering`：启用网络出口流量计量，记录集群的网络出口使用情况。
- `--enable-resource-consumption-metering`：启用资源消耗计量，跟踪 CPU 和内存等资源的使用情况。

### 示例

假设你已经创建了名为 `aibangcostallocation` 的 dataset，集群名称为 `my-cluster`，命令如下：

```bash
gcloud container clusters update my-cluster \
    --resource-usage-bigquery-dataset=aibangcostallocation \
    --enable-network-egress-metering \
    --enable-resource-consumption-metering
```

```

```

```

提取的 SQL 语句:

SELECT
column_name,
data_type
FROM
`project.lexcostallocation.INFORMATION_SCHEMA.COLUMNS`
WHERE
table_name = 'gke_cluster_resource_usage';

Mermaid 表格:

| 字段名              | 数据类型                                |
| ------------------- | --------------------------------------- |
| cluster_location    | STRING                                  |
| cluster_name        | STRING                                  |
| namespace           | STRING                                  |
| resource_name       | STRING                                  |
| sku_id              | STRING                                  |
| start_time          | TIMESTAMP                               |
| end_time            | TIMESTAMP                               |
| fraction            | FLOAT64                                 |
| cloud_resource_size | INT64                                   |
| labels              | ARRAY<STRUCT<key STRING, value STRING>> |
| project             | STRING                                  |
| usage               | STRUCT<amount FLOAT64, unit STRING>     |
| \_PARTITIONTIME     | TIMESTAMP                               |





gke_cluster_resource_usage 表字段解释及分析

以下是 gke_cluster_resource_usage 表中每个字段的含义以及如何分析这些数据：

字段含义:

字段名 数据类型 解释
cluster_location STRING GKE 集群所在的区域（例如：us-central1）
cluster_name STRING GKE 集群的名称
namespace STRING Kubernetes 命名空间，资源所在的命名空间
resource_name STRING 资源类型，例如：cpu, memory, networkEgress, disk 等
sku_id STRING 资源的计费 SKU ID，用于标识不同的计费维度
start_time TIMESTAMP 资源使用计费周期的开始时间
end_time TIMESTAMP 资源使用计费周期的结束时间
fraction FLOAT64 在计费周期内，资源被使用的比例 (0.0 - 1.0)
cloud_resource_size INT64 资源的大小，例如：CPU 核数，内存大小 (字节)
labels ARRAY<STRUCT<key STRING, value STRING>> Kubernetes 资源上的标签，key-value 形式存储
project STRING GCP 项目 ID
usage STRUCT<amount FLOAT64, unit STRING> 资源使用量，包含数量 (amount) 和单位 (unit)
_PARTITIONTIME TIMESTAMP 数据写入 BigQuery 的分区时间



，下面是 `gke_cluster_resource_usage` 表中各个字段的详细解释：

1. **cluster_location**: 集群所在的地理位置或区域信息。

2. **cluster_name**: GKE 集群的名称。

3. **namespace**: Kubernetes 命名空间，用于将集群中的资源隔离。

4. **resource_name**: 资源的名称，可能是特定的 Pod、服务或其他 Kubernetes 资源。

5. **sku_id**: 资源的 SKU（Stock Keeping Unit），用于标识资源使用的具体类型或类别。

6. **start_time**: 此条使用记录的开始时间。

7. **end_time**: 此条使用记录的结束时间。

8. **fraction**: 表示使用的资源在总资源中的占比。通常用于显示部分资源的使用情况。

9. **cloud_resource_size**: 资源的大小或数量，具体数值因资源种类而异。

10. **labels**: 资源的标签列表，用于标识或组织资源。这是一个数组，包含键值对。

11. **project**: 与资源使用相关的 Google Cloud 项目名称或 ID。

12. **usage**: 资源使用详情，包括使用量（amount）和单位（unit）。

13. **_PARTITIONTIME**: 分区时间戳，通常用于分区表中。

这些字段帮助你理解和分析在 GKE 中的资源使用情况，可以用于成本跟踪和资源优化。

数据分析:

通过过滤和分析 gke_cluster_resource_usage 表的数据，您可以获得以下信息:

 • 集群资源使用情况: 通过 resource_name、 usage.amount 和 usage.unit  字段，您可以分析不同资源 (CPU、内存、网络等) 的使用情况，并找到使用率最高的资源。
 • 命名空间资源使用: 通过 namespace 字段，您可以分析每个命名空间的资源使用情况，并识别出资源使用量大的应用。
 • 成本分摊:  通过 sku_id、 usage.amount 和 usage.unit 字段，结合 GCP 的计费信息，您可以计算出每个命名空间、每个应用甚至每个 Pod 的成本，实现成本分摊。
 • 资源使用趋势: 通过 start_time 和 end_time  字段，您可以分析资源使用随时间的变化趋势，预测未来资源需求。

关于 resource_name 和具体费用:

resource_name  字段本身只表示资源类型，不直接代表费用。 费用计算需要结合  sku_id 、 usage.amount 、 usage.unit  以及 GCP 的计费规则。

例如:

 • resource_name 为 cpu， sku_id  为  Custom-0-614400， usage.amount 为 1000， usage.unit 为 seconds，表示使用了 1000 秒的 CPU 时间。您需要根据  Custom-0-614400  这个  sku_id  在 GCP 计费文档中查找对应的价格才能计算出具体的 CPU 费用。

通过分析 gke_cluster_resource_usage 表的数据，您可以更好地了解 GKE 集群的资源使用情况，并进行成本优化和容量规划。




```

```sql
SELECT *
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`
WHERE
    cluster_name = "dev-cn-cluster-aibang-123456"
    AND namespace = "aibang-common"
    AND EXISTS (
        SELECT 1
        FROM UNNEST(labels) AS label
        WHERE label.key = "app" AND label.value = "wwap-sssssss-aibang-pa-healthcheck-2-1-2"
    )
```

基于你提供的表结构及字段，你可以写很多有用的查询来分析和统计资源使用情况。以下是一些示例 SQL 查询：

1. **按集群和命名空间统计总资源使用量**

```sql
SELECT
    cluster_name,
    namespace,
    SUM(usage.amount) AS total_usage,
    usage.unit
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`
GROUP BY
    cluster_name, namespace, usage.unit
```

2. **按时间范围统计特定 SKU 的资源使用量**

```sql
SELECT
    sku_id,
    SUM(usage.amount) AS total_usage,
    usage.unit
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`
WHERE
    start_time >= TIMESTAMP('2023-01-01')
    AND end_time <= TIMESTAMP('2023-12-31')
GROUP BY
    sku_id, usage.unit
```

3. **按标签统计资源使用情况**

```sql
SELECT
    label.key AS label_key,
    label.value AS label_value,
    SUM(usage.amount) AS total_usage,
    usage.unit
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`,
    UNNEST(labels) AS label
GROUP BY
    label_key, label_value, usage.unit
```

4. **计算每个集群在特定时间段内的平均使用量**

```sql
SELECT
    cluster_name,
    AVG(usage.amount) AS avg_usage,
    usage.unit
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`
WHERE
    start_time >= TIMESTAMP('2023-01-01')
    AND end_time <= TIMESTAMP('2023-12-31')
GROUP BY
    cluster_name, usage.unit
```

5. **找出使用量超过特定阈值的资源**

```sql
SELECT
    resource_name,
    SUM(usage.amount) AS total_usage,
    usage.unit
FROM
    `project.lexcostallocation.gke_cluster_resource_usage`
GROUP BY
    resource_name, usage.unit
HAVING
    total_usage > 1000  -- 设定你需要的阈值
```

这些例子可以帮助你合理分析数据中的各种使用和成本信息，并根据需要调整查询。
