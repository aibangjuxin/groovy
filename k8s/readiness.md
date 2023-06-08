# Liveness 和 Readiness 探测

Kubernetes 中的 Liveness 和 Readiness 探测用于检测容器的健康状况,并在需要时重启容器。

## Liveness 探测 

- 检测容器是否处于运行状态,如果不健康会重启容器
-  configs:
  - livenessProbe
- 推荐配置:
  - 对所有容器设置 liveness 探测
  - httpGet 探测: 定义路径和端口,返回 200-399 范围内的状态码
  - initialDelaySeconds 和 periodSeconds 合理设置,比如10秒开始第一次探测,之后每30秒探测一次

## Readiness 探测

- 检测容器是否就绪,以决定是否将流量转发到该容器 
- configs:
  - readnessProbe 
- 推荐配置:
  - 对所有容器设置 readiness 探测
  - httpGet 探测: 定义路径和端口,返回 200-399 范围内的状态码  
  - initialDelaySeconds 建议稍长,比如30秒,以给容器足够初始化的时间
  - periodSeconds 取值与 Liveness 探测期望值相同

## 两者关系

- Liveness 探测重启容器,Readiness 探测移除 Pod 副本从服务端点
- Liveness 应该更频繁用于确保 Pod 健康
- Readiness应稍慢,以给 Pod 副本充分准备好从接收流量
- 如果 Readiness 失败,Pod会被移除从服务端点,Liveness 会重启 Pod
- 两者的初始延迟应该不同,Liveness 的更短

所以,通过适当配置 Liveness 和 Readiness 探测,可以更好的监控容器的健康状况,并做出相应的维护与治理。这是 Kubernetes 中非常重要的概念与技术。
