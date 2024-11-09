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

使用 `kubectl apply --server-side --force-conflicts -f test.yaml` 命令时，`--force-conflicts` 选项允许在出现字段管理冲突时强制应用更改。这一选项特别适用于使用服务器端应用（Server-Side Apply, SSA）时，能够覆盖其他管理者对同一字段的控制。

## 关键点

1. **冲突的定义**：
   在 Kubernetes 中，冲突发生在一个 `Apply` 操作试图更改一个字段，而另一个管理者也声称管理该字段时。此机制防止意外覆盖其他用户设置的值[2][6]。

2. **使用 `--force-conflicts`**：
   当你使用 `--server-side` 和 `--force-conflicts` 时，如果存在冲突，Kubernetes 会强制执行你的更改，并将该字段从其他管理者的管理中移除。这意味着你将成为该字段的唯一管理者[4][6]。

3. **警告机制**：
   当前版本的 `kubectl` 在使用 `--force-conflicts` 时并不会自动显示被覆盖的冲突信息。社区正在讨论增加此功能，以便用户能够更好地了解哪些字段被强制覆盖，这对于迁移到 SSA 的用户尤其重要[1][2]。

4. **命令示例**：
   使用命令时，可以这样写：

   ```bash
   kubectl apply --server-side --force-conflicts -f test.yaml
   ```

5. **注意事项**：
   - 使用 `--force-conflicts` 时要小心，因为这可能导致意外覆盖重要配置。
   - 确保在应用之前检查 YAML 文件中的配置，以避免不必要的服务中断。

## 总结

通过使用 `kubectl apply --server-side --force-conflicts`，你可以在处理 Kubernetes 资源时强制覆盖现有的字段管理冲突。这为自动化和集中管理提供了灵活性，但也需要谨慎操作，以确保不会意外覆盖重要配置。

Citations:
[1] <https://github.com/kubernetes/kubernetes/issues/113612>
[2] <https://kubernetes.io/docs/reference/using-api/server-side-apply/>
[3] <https://github.com/gavinbunney/terraform-provider-kubectl/issues/139>
[4] <https://kubernetes.io/docs/reference/kubectl/generated/kubectl_apply/>
[5] <https://qiita.com/superbrothers/items/aeba9406691388b6a19e>
[6] <https://eng.d2iq.com/blog/conflict-resolution-kubernetes-server-side-apply/>
[7] <https://kubernetes.io/blog/2022/10/20/advanced-server-side-apply/>
[8] <https://support.huaweicloud.com/basics-cce/kubernetes_0036.html>

`kubectl apply --server-side --force-conflicts -f test.yaml` 这个命令中的参数可能会导致配置与预期不一致，以下是相关细节和可能影响扩容行为的因素。

### 参数分析

1. **`--server-side`**：启用服务端应用模式，意味着 Kubernetes API 服务器会处理变更的合并。此模式在合并多个源配置时比较有用，比如当多个控制器对相同资源进行管理时。但可能会引发合并冲突或者忽略客户端指定的某些字段，特别是在 HPA 的配置项上（如 `metrics` 的配置）。

2. **`--force-conflicts`**：强制覆盖冲突。此参数会在检测到配置冲突时覆盖现有配置，可能会导致部分配置项被更新或删除。如果 HPA 的某些配置被强制覆盖，可能导致目标 `desiredMetricValue` 被修改或自动调整不一致。

### 可能的影响

在使用 `--server-side` 和 `--force-conflicts` 时，如果 HPA 配置与集群中的已有配置有冲突，API 服务器会优先保留服务端配置，并覆盖冲突的字段。这可能导致：

- **指标配置被忽略或重置**：HPA 的目标 `desiredMetricValue` 或相关指标配置可能没有被正确应用。
- **非预期的扩容行为**：如果 Kubernetes 在处理配置合并时修改了 HPA 的内部状态，可能导致实际的扩容行为和配置不符。

### 排查步骤

1. **检查 HPA 的实际配置**  
   使用以下命令查看 HPA 的配置是否正确应用：

   ```bash
   kubectl get hpa <your-hpa-name> -n <namespace> -o yaml
   ```

   特别检查 `metrics` 下的 `desiredMetricValue` 和当前的配置是否与您期望的 85% 一致。

2. **验证 Metrics Server 的数据是否准确**  
   如果 `metrics-server` 数据不准确，HPA 可能会根据错误的内存使用率触发扩容。可以使用以下命令检查当前的 Pod 利用率：

   ```bash
   kubectl top pod -n <namespace>
   ```

3. **确认 HPA 事件日志**  
   使用以下命令查看 HPA 的事件日志，确认是否有扩容记录和触发原因：

   ```bash
   kubectl describe hpa <your-hpa-name> -n <namespace>
   ```

4. **避免强制覆盖**  
   如果不确定配置是否被正确应用，建议在测试环境中使用标准的 `kubectl apply -f test.yaml`（不加 `--server-side` 和 `--force-conflicts` 参数），以确保配置完全来自客户端 YAML 文件，并避免意外的覆盖。

### 建议

如果发现 HPA 配置项在 `server-side` 模式下被部分忽略，建议：

- 使用客户端模式重新应用配置：`kubectl apply -f test.yaml`
- 或者，将 HPA 配置单独管理，避免和其他控制器配置的冲突。

通过以上步骤和检查，相信可以更准确地定位和修复扩容触发条件不符的问题。
