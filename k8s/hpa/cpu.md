学习下面这个文章https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/
在 Kubernetes 中，Horizontal Pod Autoscaling（HPA）通过自动调整工作负载的 Pod 副本数量来匹配需求。其计算公式为：
desiredReplicas = ceil[currentReplicas * ( currentMetricValue / desiredMetricValue )]
其中，ceil表示向上取整。
因为我在deployment 设置了CPU的limit 和request

我的hpa deployment如下 关于内存的扩容这一部分我已经测试过了
我现在不太理解的是CPU 在怎样的条件下能够扩容？
在怎样的条件下又会缩容
按照文章里面的提示，如果我设置了limit 我的750设置也是一个合理值？

apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
  name: aibang-deployment-hpa
  namespace: aibang
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: aibang-deployment
  minReplicas: 1
  maxReplicas: 5
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 750
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80​​​​​​​​​​​​​​​​