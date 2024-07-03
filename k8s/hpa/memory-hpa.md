

学习下面这个文章
https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/
有这么一个公式
```
desiredReplicas = ceil[currentReplicas * ( currentMetricValue / desiredMetricValue )]
```
The control plane skips any scaling action if the ratio is sufficiently close to 1.0 (within a globally-configurable tolerance, 0.1 by default).
那么加上我的情况，现在如下，请给我计算对应的值
currentReplicas 为2
currentMetricValue = 90，desiredMetricValue = 80 。

让我们使用提供的公式和数值来计算desiredReplicas:

公式:
```
desiredReplicas = ceil[currentReplicas * ( currentMetricValue / desiredMetricValue )]
```

给定的值:
- currentReplicas = 2
- currentMetricValue = 90
- desiredMetricValue = 80

让我们代入这些值:

```
desiredReplicas = ceil[2 * (90 / 80)]
                = ceil[2 * 1.125]
                = ceil[2.25]
                = 3
```

因此,根据这个计算,desiredReplicas的值是3。

然而,您还提到了一个重要的点:

"The control plane skips any scaling action if the ratio is sufficiently close to 1.0 (within a globally-configurable tolerance, 0.1 by default)."

让我们检查一下比率是否在这个容差范围内:

```
ratio = currentMetricValue / desiredMetricValue
      = 90 / 80
      = 1.125
```

1.125与1.0的差异是0.125,这超过了默认的0.1容差。因此,在这种情况下,控制平面很可能会执行缩放操作,将副本数从2增加到3。

总结:
- 计算出的desiredReplicas值是3。
- 由于比率(1.125)超出了默认容差范围,控制平面可能会执行缩放操作。
让我们总结一下：

1. **计算desiredReplicas**：
   根据公式：
   ```
   desiredReplicas = ceil[currentReplicas * ( currentMetricValue / desiredMetricValue )]
   ```
   将给定的值代入公式：
   ```
   desiredReplicas = ceil[2 * (90 / 80)]
                   = ceil[2 * 1.125]
                   = ceil[2.25]
                   = 3
   ```

2. **检查比率是否在容差范围内**：
   比率：
   ```
   ratio = currentMetricValue / desiredMetricValue
         = 90 / 80
         = 1.125
   ```
   检查1.125与1.0的差异：
   ```
   差异 = |1.125 - 1.0| = 0.125
   ```
   0.125 超过了默认的0.1容差。因此，比率1.125不在1.0 ± 0.1的容差范围内。

结论：

- **desiredReplicas**的值是3。
- 由于比率1.125超出了默认容差范围，控制平面将执行缩放操作，将副本数从2增加到3。


### Horizontal Pod Autoscaler Calculation

The Horizontal Pod Autoscaler (HPA) in Kubernetes uses the following formula to determine the desired number of replicas:

```
desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]
```

Additionally, the control plane will skip any scaling action if the ratio of `currentMetricValue / desiredMetricValue` is sufficiently close to 1.0, within a globally-configurable tolerance (0.1 by default).

### Given Values
- `currentReplicas = 2`
- `currentMetricValue = 90`
- `desiredMetricValue = 80`

### Calculation

1. **Calculate the desired replicas:**

   Using the formula:
   ```
   desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]
   ```
   Substituting the given values:
   ```
   desiredReplicas = ceil[2 * (90 / 80)]
                   = ceil[2 * 1.125]
                   = ceil[2.25]
                   = 3
   ```

2. **Check if the ratio is within the tolerance:**

   Calculate the ratio:
   ```
   ratio = currentMetricValue / desiredMetricValue
         = 90 / 80
         = 1.125
   ```

   Determine the difference from 1.0:
   ```
   difference = |1.125 - 1.0| = 0.125
   ```

   Compare this difference with the default tolerance of 0.1. Since 0.125 > 0.1, the ratio is not within the tolerance range.

### Conclusion
- The calculated `desiredReplicas` is 3.
- Since the ratio (1.125) exceeds the default tolerance (0.1), the control plane will execute the scaling action, increasing the number of replicas from 2 to 3.

### Examples
Here are a few examples with different values:

1. **Example 1**
   - `currentReplicas = 4`
   - `currentMetricValue = 120`
   - `desiredMetricValue = 100`
   
   Calculation:
   ```
   desiredReplicas = ceil[4 * (120 / 100)]
                   = ceil[4 * 1.2]
                   = ceil[4.8]
                   = 5
   ```
   Ratio:
   ```
   ratio = 120 / 100
         = 1.2
   ```
   Difference from 1.0:
   ```
   difference = |1.2 - 1.0| = 0.2
   ```
   Since 0.2 > 0.1, the scaling action will increase replicas from 4 to 5.

2. **Example 2**
   - `currentReplicas = 5`
   - `currentMetricValue = 95`
   - `desiredMetricValue = 100`
   
   Calculation:
   ```
   desiredReplicas = ceil[5 * (95 / 100)]
                   = ceil[5 * 0.95]
                   = ceil[4.75]
                   = 5
   ```
   Ratio:
   ```
   ratio = 95 / 100
         = 0.95
   ```
   Difference from 1.0:
   ```
   difference = |0.95 - 1.0| = 0.05
   ```
   Since 0.05 < 0.1, the scaling action will not be executed, and replicas remain at 5.

### Summary Document

```markdown
# Horizontal Pod Autoscaler Calculation

## Formula
The Horizontal Pod Autoscaler (HPA) uses the following formula:
```
desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]
```

## Tolerance
The control plane skips any scaling action if the ratio `currentMetricValue / desiredMetricValue` is sufficiently close to 1.0, within a globally-configurable tolerance (0.1 by default).

## Example Calculation

### Given Values
- `currentReplicas = 2`
- `currentMetricValue = 90`
- `desiredMetricValue = 80`

### Steps
1. Calculate the desired replicas:
   ```
   desiredReplicas = ceil[currentReplicas * (currentMetricValue / desiredMetricValue)]
   ```
   Substituting the values:
   ```
   desiredReplicas = ceil[2 * (90 / 80)]
                   = ceil[2 * 1.125]
                   = ceil[2.25]
                   = 3
   ```

2. Check the ratio:
   ```
   ratio = currentMetricValue / desiredMetricValue
         = 90 / 80
         = 1.125
   ```
   Determine the difference from 1.0:
   ```
   difference = |1.125 - 1.0| = 0.125
   ```

   Since 0.125 > 0.1, the ratio is not within the tolerance, and the control plane will execute the scaling action.

### Conclusion
- The calculated `desiredReplicas` is 3.
- The control plane will increase the number of replicas from 2 to 3.

## Additional Examples

### Example 1
- `currentReplicas = 4`
- `currentMetricValue = 120`
- `desiredMetricValue = 100`

Calculation:
```
desiredReplicas = ceil[4 * (120 / 100)]
                = ceil[4 * 1.2]
                = ceil[4.8]
                = 5
```
Ratio:
```
ratio = 120 / 100
      = 1.2
```
Difference from 1.0:
```
difference = |1.2 - 1.0| = 0.2
```
Since 0.2 > 0.1, the scaling action will increase replicas from 4 to 5.

### Example 2
- `currentReplicas = 5`
- `currentMetricValue = 95`
- `desiredMetricValue = 100`

Calculation:
```
desiredReplicas = ceil[5 * (95 / 100)]
                = ceil[5 * 0.95]
                = ceil[4.75]
                = 5
```
Ratio:
```
ratio = 95 / 100
      = 0.95
```
Difference from 1.0:
```
difference = |0.95 - 1.0| = 0.05
```
Since 0.05 < 0.1, the scaling action will not be executed, and replicas remain at 5.
```

If you need any further assistance or examples, feel free to ask!

