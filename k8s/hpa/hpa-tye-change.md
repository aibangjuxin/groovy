### 默认策略

如果你不做任何 `behavior` 配置，HPA将使用默认的扩缩容策略。默认策略包括：

- **扩容**：没有特别的限制，每次扩容可以根据需求增加Pod数量。
- **缩容**：每分钟最多缩减1个Pod。

### 配置推荐

为了按照数值来控制扩缩容行为，可以使用 `behavior` 字段来设置扩容和缩容的具体策略。

#### 默认策略示例

```yaml
apiVersion: autoscaling/v2beta2
kind: HorizontalPodAutoscaler
metadata:
  name: hpa-default
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: your-deployment-name
  minReplicas: 1
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: memory
        target:
          type: Average
          averageValue: 800Mi
```

#### 按数值配置扩缩容策略示例

假设你希望扩容和缩容时每次增加或减少固定的Pod数量，例如扩容时每次增加2个Pod，缩容时每次减少1个Pod，可以使用如下配置：

```yaml
apiVersion: autoscaling/v2beta2
kind: HorizontalPodAutoscaler
metadata:
  name: hpa-custom
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: your-deployment-name
  minReplicas: 1
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: memory
        target:
          type: Average
          averageValue: 800Mi
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 300 # 300秒的扩容稳定窗口期
      policies:
        - type: Pods
          value: 2 # 每次最多扩展2个Pod
          periodSeconds: 60 # 每60秒检查一次
    scaleDown:
      stabilizationWindowSeconds: 300 # 300秒的缩容稳定窗口期
      policies:
        - type: Pods
          value: 1 # 每次最多缩减1个Pod
          periodSeconds: 60 # 每60秒检查一次
```

### 解释

- **scaleUp**：
  - **stabilizationWindowSeconds**：300秒的稳定窗口期，用于防止频繁的扩容。
  - **policies**：
    - **type: Pods**：扩展策略基于固定Pod数量。
    - **value: 2**：每次扩容最多增加2个Pod。
    - **periodSeconds: 60**：每60秒检查一次，并最多扩容2个Pod。

- **scaleDown**：
  - **stabilizationWindowSeconds**：300秒的稳定窗口期，用于防止频繁的缩容。
  - **policies**：
    - **type: Pods**：缩减策略基于固定Pod数量。
    - **value: 1**：每次缩减最多减少1个Pod。
    - **periodSeconds: 60**：每60秒检查一次，并最多缩减1个Pod。

### 总结

- **默认策略**：无需配置 `behavior` 字段，默认策略会每分钟最多缩减1个Pod，扩容没有特别的限制。
- **按数值配置策略**：通过配置 `behavior` 字段，可以精细控制扩容和缩容的速度和数量，避免频繁的Pod变动，提高资源利用效率。

希望这些示例和解释能帮助你配置HPA，以满足你的需求。