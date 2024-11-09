根据公式：

$$
\text{desiredReplicas} = \left\lceil \text{currentReplicas} \times \left( \frac{\text{currentMetricValue}}{\text{desiredMetricValue}} \right) \right\rceil
$$

在这种情况下，`currentMetricValue / desiredMetricValue` 只要超过 1.1，就会导致副本数从 2 扩展到 3，因为 `\left\lceil 2 \times 1.1 \right\rceil` 等于 3。

因此，**只要 `currentMetricValue` 达到 85 × 1.1 = 93.5**（向上取整后的 94），HPA 就会触发扩容，将副本数增加到 3 个。

您说得对！在这种情况下，我们只需要 `currentMetricValue` 超过 `1.1 * desiredMetricValue` 就可以触发扩容。下面是正确的计算过程：

## 公式

为了从 2 个副本扩展到 3 个副本，只要满足：

$$
\frac{\text{currentMetricValue}}{\text{desiredMetricValue}} > 1.1
$$

### 示例计算

假设：

- **currentReplicas** = 2
- **desiredMetricValue** = 85（内存利用率目标值为 85%）

那么只要满足：

$$
\text{currentMetricValue} = 1.1 \times 85
$$

### 计算结果

$$
\text{currentMetricValue} = 93.5
$$

### 结果分析

因此，当 **currentMetricValue ≥ 94%** 时，HPA 将触发扩容，将副本数从 2 增加到 3。
