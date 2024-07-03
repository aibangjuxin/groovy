

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
