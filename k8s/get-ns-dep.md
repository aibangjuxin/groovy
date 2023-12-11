
若要排除以 "kube-" 开头的系统 namespace，你可以在循环之前添加一个条件来检查 namespace 名称。以下是修改后的脚本：

```bash
#!/bin/bash

# 获取所有的 namespace，但排除以 "kube-" 开头的系统 namespace
namespaces=$(kubectl get namespaces -o jsonpath='{.items[*].metadata.name}' | grep -vE '^kube-')

# 循环处理每个 namespace
for namespace in $namespaces; do
    echo "Namespace: $namespace"

    # 获取该 namespace 下的 deployments 数量
    deployment_count=$(kubectl get deployments --namespace=$namespace --no-headers | wc -l)
    echo "Number of Deployments: $deployment_count"

    # 获取该 namespace 下的 pods 数量
    pod_count=$(kubectl get pods --namespace=$namespace --no-headers | wc -l)
    echo "Number of Pods: $pod_count"

    echo "----------------------"
done
```

在这个例子中，`grep -vE '^kube-'` 用于排除以 "kube-" 开头的系统 namespace。确保根据实际需求调整脚本。

#!/bin/bash

# 获取所有的 namespace，但排除以 "kube-" 开头的系统 namespace
namespaces=$(kubectl get namespaces -o jsonpath='{.items[?(@.metadata.name !~ /^kube-/)].metadata.name}')

# 循环处理每个 namespace
for namespace in $namespaces; do
    echo "Namespace: $namespace"

    # 获取该 namespace 下的 deployments 数量
    deployment_count=$(kubectl get deployments --namespace=$namespace --no-headers | wc -l)
    echo "Number of Deployments: $deployment_count"

    # 获取该 namespace 下的 pods 数量
    pod_count=$(kubectl get pods --namespace=$namespace --no-headers | wc -l)
    echo "Number of Pods: $pod_count"

    echo "----------------------"
done

