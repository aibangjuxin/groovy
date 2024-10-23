
```sql
WITH team_backup_counts AS (
  SELECT
    teamName,
    COUNT(DISTINCT DATE(PARSE_TIMESTAMP('%m/%d/%Y, %H:%M:%S', backupTime))) AS backup_count
  FROM
    `project.aibang.api_data.team_level_api_dev_historical_data`
  WHERE
    PARSE_TIMESTAMP('%m/%d/%Y, %H:%M:%S', backupTime) BETWEEN TIMESTAMP('2024-09-01 00:00:00') AND TIMESTAMP('2024-09-30 23:59:59')
  GROUP BY
    teamName
), 
SELECT
  teamName,
  t.backup_count,
  SUM(
    CASE
      WHEN REGEXP_CONTAINS(api.memory_limit, r'Gi') THEN CAST(REGEXP_REPLACE(api.memory_limit, r'Gi', '') AS FLOAT64) * 1024
      WHEN REGEXP_CONTAINS(api.memory_limit, r'Mi') THEN CAST(REGEXP_REPLACE(api.memory_limit, r'Mi', '') AS FLOAT64)
      ELSE 0
    END * api.pod_count
  ) AS memory_total_mi,
  SUM(CAST(api.cpu_limit AS FLOAT64) * api.pod_count) AS cpu_total,
  SUM(
    CASE
      WHEN REGEXP_CONTAINS(api.memory_limit, r'Gi') THEN CAST(REGEXP_REPLACE(api.memory_limit, r'Gi', '') AS FLOAT64) * 1024
      WHEN REGEXP_CONTAINS(api.memory_limit, r'Mi') THEN CAST(REGEXP_REPLACE(api.memory_limit, r'Mi', '') AS FLOAT64)
      ELSE 0
    END * api.pod_count
  ) / t.backup_count AS avg_memory_per_day_mi,
  SUM(CAST(api.cpu_limit AS FLOAT64) * api.pod_count) / t.backup_count AS avg_cpu_per_day
FROM
  `project.aibang.api_data.team_level_api_dev_historical_data` AS d
JOIN UNNEST(d.apis) AS api
JOIN team_backup_counts AS t ON d.teamName = t.teamName
WHERE
  PARSE_TIMESTAMP('%m/%d/%Y, %H:%M:%S', d.backupTime) BETWEEN TIMESTAMP('2024-09-01 00:00:00') AND TIMESTAMP('2024-09-30 23:59:59')
GROUP BY
  t.teamName,
  t.backup_count
```

该SQL查询语句用于计算每个团队在2024年9月每天的平均内存和CPU使用情况。

**查询逻辑:**

1. **CTE (Common Table Expression) - team_backup_counts**:
    * 首先，使用 `WITH` 语句定义一个名为 `team_backup_counts` 的 CTE。
    * 从表 `project.aibang.api_data.team_level_api_dev_historical_data` 中选择数据。
    * 计算每个团队在9月份的备份天数 (`backup_count`)，通过 `COUNT(DISTINCT DATE(PARSE_TIMESTAMP(...)))` 实现。
    * 使用 `WHERE` 子句过滤9月份的数据。
    * 按 `teamName` 分组数据。

2. **主查询**:
    * 从 `project.aibang.api_data.team_level_api_dev_historical_data` 表中选择数据，并使用别名 `d`。
    * 使用 `UNNEST(d.apis)` 将 `apis` 数组展开，以便访问每个API的详细信息。
    * 使用 `JOIN` 将 `team_backup_counts` CTE 与主表连接，连接条件为 `d.teamName = t.teamName`。
    * 使用 `WHERE` 子句过滤9月份的数据。
    * 计算以下指标：
        * `memory_total_mi`: 每个团队的总内存使用量 (MiB)。
        * `cpu_total`: 每个团队的总CPU使用量。
        * `avg_memory_per_day_mi`: 每个团队每天的平均内存使用量 (MiB)。
        * `avg_cpu_per_day`: 每个团队每天的平均CPU使用量。
    * 使用 `GROUP BY` 按 `teamName` 和 `backup_count` 分组数据。

**关键点:**

* **REGEXP_CONTAINS & REGEXP_REPLACE**: 用于处理 `memory_limit` 字段，将其转换为 MiB 单位。
* **CAST( AS FLOAT64)`**: 将字符串类型的数值转换为浮点数，以便进行计算。
* **CTE**: 通过预先计算备份天数，简化了主查询的逻辑。

**输出结果**:

该查询将返回一个包含以下列的表格：

* `teamName`: 团队名称。
* `backup_count`: 9月份的备份天数。
* `memory_total_mi`: 总内存使用量 (MiB)。
* `cpu_total`: 总CPU使用量。
* `avg_memory_per_day_mi`: 每天的平均内存使用量 (MiB)。
* `avg_cpu_per_day`: 每天的平均CPU使用量。


