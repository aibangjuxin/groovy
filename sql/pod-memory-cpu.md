你想要在 BigQuery 中通过 api_name 汇总每个 api_name 所有 Pod 的 CPU 和内存的使用情况。你可以使用 SQL 查询来进行这些累加计算。以下是如何编写查询的步骤：

假设表结构如下：

	•	v4 表：
	•	api_name: API 名称（唯一标识符）
	•	cpu_limit: Pod 的 CPU 限制（可能为每个 Pod 都有一个值）
	•	memory_limit: Pod 的内存限制（可能为每个 Pod 都有一个值）

查询步骤

我们需要对 cpu_limit 和 memory_limit 按照 api_name 进行分组，使用 SUM() 函数来累计每个 api_name 的 CPU 和内存限制。

SELECT
  api_name,
  SUM(cpu_limit) AS total_cpu_limit,
  SUM(memory_limit) AS total_memory_limit
FROM
  `your_project.your_dataset.v4`
GROUP BY
  api_name;

解释：

	1.	SUM(cpu_limit)：汇总每个 api_name 的所有 Pod 的 CPU 限制。
	2.	SUM(memory_limit)：汇总每个 api_name 的所有 Pod 的内存限制。
	3.	GROUP BY api_name：按 api_name 分组以确保每个 api_name 只返回一行，且所有相同 api_name 的 Pod 的 CPU 和内存限制会被累加。

示例：

假设 v4 表中的数据如下：

api_name	cpu_limit	memory_limit
api_1	500m	512Mi
api_1	300m	256Mi
api_2	1000m	1024Mi
api_2	500m	512Mi

运行上述查询后将得到：

api_name	total_cpu_limit	total_memory_limit
api_1	800m	768Mi
api_2	1500m	1536Mi

你可以将该查询作为定期作业运行，自动汇总 cpu_limit 和 memory_limit 的值，并将结果插入到另一个 BigQuery 表中，或者在同一个表中更新汇总数据。
