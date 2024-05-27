# summary 
我的GKE环境中部署对应的应用在不同的Namespace。比如我的部署是通过pipeline来部署一个新的版本
我想要保留的版本信息大概如下保留2个major_version 下的一个版本就可以了
```bash
比如一个新的API的Deployment名字如下
abj-aibang-is-onboarding-na-accouts-1-0-11-deployment
abj-aibang-is-onboarding-na-accouts-2-0-1-deployment
那么假设我原来的API名字如下
abj-aibang-is-onboarding-na-accouts-1-0-10-deployment
abj-aibang-is-onboarding-na-accouts-2-0-0-deployment
那么其实这个abj-aibang-is-onboarding-na-accouts-1-0-10-deployment和abj-aibang-is-onboarding-na-accouts-2-0-0-deployment都是要被删除的
```
还有一个问题需要注意我的每个Deployment都有其对应的svc 只是可能包含有loadBalancer 类型可能是 LoadBalancer IP 或者ClusterIP 
且这些IP我都会在对应的cloud DNS里面去创建对应的DNS记录

比如我的记录可能如下
```bash
abj-aibang-is-onboarding-na-accouts-1-0-10-internal.aliyun.aibang.cn.local 172.18.0.46
abj-aibang-is-onboarding-na-accouts-1-0-11-internal.aliyun.aibang.cn.local 172.18.0.47
abj-aibang-is-onboarding-na-accouts-2-0-0-internal.aliyun.aibang.cn.local 172.18.0.45
abj-aibang-is-onboarding-na-accouts-2-0-1-internal.aliyun.aibang.cn.local 172.18.0.48
```
我的需求就是如何确保我新的API部署之后按照对应的逻辑来删除我旧的API？
方法不局限，我需要多个解决方案来平衡
比如我部署新的API的时候直接提供旧的API的名字，那么我在确保新的API部署成功之后
再删除这个旧的API
比如我这次部署的是 abj-aibang-is-onboarding-na-accouts-1-0-11-deployment这个版本，那么我需要删除abj-aibang-is-onboarding-na-accouts-1-0-10-deployment这个版本就可以了
这次部署成功之后，环境里对应的版本是
```bash
abj-aibang-is-onboarding-na-accouts-1-0-11-deployment 和
abj-aibang-is-onboarding-na-accouts-2-0-0-deployment
```
综合上面描述。我需要考虑如何确保新版本部署成功之后删除旧版本
因为我的实际情况是，应用Team部署了新的版本，完全不会去删除旧的版本，这样会占用我们很多资源


你描述的情况涉及在GKE上自动化管理Deployment和对应的Service，以及相关的DNS记录。以下是几种解决方案，每种都能够在新的API部署成功后删除旧的API和相关资源：

## 方案一：基于CI/CD Pipeline的自动化脚本

在CI/CD Pipeline中，可以引入一个步骤，部署新版本后删除旧版本。步骤如下：

1. **部署新版本**：将新的Deployment资源应用到GKE集群中。
2. **等待新版本就绪**：确认新的Pod已经启动并且健康检查通过。这可以通过kubectl命令完成，例如：

    ```sh
    kubectl rollout status deployment/abj-aibang-is-onboarding-na-accouts-1-0-11-deployment -n <namespace>
    ```

3. **删除旧版本**：在新版本成功后，通过kubectl命令删除旧版本：

    ```sh
    kubectl delete deployment abj-aibang-is-onboarding-na-accouts-1-0-10-deployment -n <namespace>
    ```

4. **删除对应的Service资源和DNS记录**：先删除Service，然后删除DNS记录：

    ```sh
    # 删除旧Deployment对应的Service
    kubectl delete service abj-aibang-is-onboarding-na-accouts-1-0-10-service -n <namespace>

    # 更新DNS记录（这个涉及到你使用的DNS提供商的API）
    # 这里提供一个例子，假设使用Cloud DNS，调用API删除记录
    gcloud dns record-sets delete abj-aibang-is-onboarding-na-accouts-1-0-10-internal.aliyun.aibang.cn.local --type A --zone=<zone>
    ```

## 方案二：使用Kubernetes Operator

你可以编写一个Kubernetes Operator来自动化管理你的Deployment和Service生命周期。这个Operator可以监听Deployment事件，确保旧版本在新版本部署成功后被删除。

### 示例Operator框架

这个框架可以用Python的kubebuilder来扩展：

1. **Deployment Event Listener**：监听Deployment创建事件。
2. **检查新版本状态**：确认新版本的Pod都在运行且状态正常。
3. **找到旧版本**：通过标签选择器找到旧版本（类似`app=abj-aibang-is-onboarding-na-accouts`和特定版本号）。
4. **删除旧版本**：在新版本健康运行后，删除旧版本及其对应的Service和DNS记录。

## 方案三：使用Helm生命周期钩子

如果你使用Helm来管理部署，可以利用Helm的pre-install和post-install钩子。

1. **pre-install钩子**：

    ```yaml
    apiVersion: batch/v1
    kind: Job
    metadata:
      name: "delete-old-deployment"
      annotations:
        "helm.sh/hook": "pre-install"
    spec:
      template:
        spec:
          containers:
            - name: delete-old
              image: bitnami/kubectl:latest
              command: ["kubectl", "delete", "deployment", "abj-aibang-is-onboarding-na-accouts-1-0-10-deployment", "-n", "<namespace>"]
          restartPolicy: Never
    ```

2. **post-install钩子**：

    ```yaml
    apiVersion: batch/v1
    kind: Job
    metadata:
      name: "delete-old-service-dns"
      annotations:
        "helm.sh/hook": "post-install"
    spec:
      template:
        spec:
          containers:
            - name: delete-svc-dns
              image: bitnami/kubectl:latest
              command: ["bash", "-c", "kubectl delete svc abj-aibang-is-onboarding-na-accouts-1-0-10-svc -n <namespace> && gcloud dns record-sets delete abj-aibang-is-onboarding-na-accouts-1-0-10-internal.aliyun.aibang.cn.local --type A --zone=<zone>"]
          restartPolicy: Never
    ```

这几种方案都可以自动化地确保新的API部署成功后删除旧的API和相关资源。你可以根据自己的具体情况选择合适的方法。


在CI/CD Pipeline中实现删除旧版本API并保留最新的两个major_version，可以通过以下步骤实现：

1. **获取现有的所有版本**：通过kubectl获取现有Deployment的列表，并按照版本号排序。
2. **识别需要保留和删除的版本**：解析版本号，保留最新的两个major_version。
3. **删除旧版本**：删除除了保留版本之外的所有版本，包括相应的Service和DNS记录。

下面是一个详细的实现示例，假设使用Bash脚本和kubectl命令实现：

### 1. 获取现有版本
```sh
#!/bin/bash

NAMESPACE=<your-namespace>
APP_PREFIX="abj-aibang-is-onboarding-na-accouts"

# 获取所有部署的名称
deployments=$(kubectl get deployments -n $NAMESPACE -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}' | grep "$APP_PREFIX")

# 解析版本号 (假设版本格式为`<prefix>-<major>-<minor>-<patch>-deployment`)
versions=()
for dep in $deployments; do
  version=$(echo $dep | sed -e "s/$APP_PREFIX-//" -e 's/-deployment//')
  versions+=($version)
done

# 按照版本号排序
sorted_versions=$(printf "%s\n" "${versions[@]}" | sort -t'-' -k1,1nr -k2,2nr -k3,3nr)

# 获取最新两个major_version
declare -A latest_versions
for ver in $sorted_versions; do
  major_version=$(echo $ver | cut -d'-' -f1)
  if [ ${#latest_versions[@]} -eq 0 ]; then
    latest_versions[$major_version]=$ver
  elif [ $major_version -ne ${!latest_versions[@]} ]; then
    if [ ${#latest_versions[@]} -eq 1 ]; then
      latest_versions[$major_version]=$ver
    else
      break
    fi
  fi
done

# 保留最新两个major_version的完整版本号
versions_to_keep=()
for key in "${!latest_versions[@]}"; do
  versions_to_keep+=("${latest_versions[$key]}")
done

```

### 2. 删除旧版本
```sh
# 删除需要删除的版本
for ver in $sorted_versions; do
  # 需要保留的版本不删除
  if [[ " ${versions_to_keep[@]} " =~ " ${ver} " ]]; then
    continue
  fi

  # 构建Deployment, Service和DNS名称
  dep_name="$APP_PREFIX-$ver-deployment"
  svc_name="$APP_PREFIX-$ver-svc"
  dns_name="$APP_PREFIX-$ver-internal.aliyun.aibang.cn.local"

  # 删除Deployment
  kubectl delete deployment $dep_name -n $NAMESPACE

  # 删除Service
  kubectl delete service $svc_name -n $NAMESPACE

  # 删除DNS记录 (假设使用gcloud dns)
  gcloud dns record-sets delete $dns_name --type A --zone=<zone>
done
```

### 3. 集成到CI/CD Pipeline
你可以将上述脚本集成到你的CI/CD Pipeline中。下面是一个Jenkins管道示例：

```groovy
pipeline {
    agent any

    environment {
        NAMESPACE = "<your-namespace>"
        KUBECONFIG = credentials('your-kubeconfig-credential')
    }

    stages {
        stage('Deploy New Version') {
            steps {
                script {
                    sh 'kubectl apply -f path/to/your/new/deployment.yaml'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    sh 'kubectl rollout status deployment/abj-aibang-is-onboarding-na-accouts-<new-version>-deployment -n $NAMESPACE'
                }
            }
        }

        stage('Cleanup Old Versions') {
            steps {
                script {
                    sh 'path/to/your/cleanup-script.sh'
                }
            }
        }
    }
}
```

通过上述方式，你可以确保在部署新版本API后，自动删除旧版本API，只保留最新的两个major_version，包括相应的Service和DNS记录。这种方式可以减少手动操作，确保资源的及时回收。


当然可以！要识别需要保留和删除的版本，我们需要按照版本号对所有现有的Deployments进行排序，并且只保留最新的两个major版本。

### 具体步骤

1. **获取所有Deployments**：首先获取所有相关Deployments的名字。
2. **解析版本信息**：从Deployment名字中提取版本号。
3. **排序版本**：对版本号进行排序，以确定哪些是最新的。
4. **识别需要保留的版本**：仅保留最新的两个major版本，其余的即为需要删除的版本。

### 示例解析

假设我们有以下Deployment名称：

```
abj-aibang-is-onboarding-na-accouts-1-0-10-deployment
abj-aibang-is-onboarding-na-accouts-2-0-0-deployment
abj-aibang-is-onboarding-na-accouts-1-0-11-deployment
abj-aibang-is-onboarding-na-accouts-2-0-1-deployment
```

#### 步骤1: 获取所有Deployments

```sh
NAMESPACE=<your-namespace>
APP_PREFIX="abj-aibang-is-onboarding-na-accouts"

# 获取所有部署的名称
deployments=$(kubectl get deployments -n $NAMESPACE -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}' | grep "$APP_PREFIX")
```

#### 步骤2: 解析版本信息

我们可以使用Bash的字符串处理功能来提取版本号。

```sh
versions=()
for dep in $deployments; do
  version=$(echo $dep | sed -e "s/$APP_PREFIX-//" -e 's/-deployment//')
  versions+=($version)
done
```

解析结果应为：

```
1-0-10
2-0-0
1-0-11
2-0-1
```

#### 步骤3: 排序版本

排序的目的是找到最新的两个major版本。我们可以用sort命令对版本号进行排序。

```sh
sorted_versions=$(printf "%s\n" "${versions[@]}" | sort -t'-' -k1,1nr -k2,2nr -k3,3nr)
```

排序结果应为：

```
2-0-1
2-0-0
1-0-11
1-0-10
```

#### 步骤4: 识别需要保留的版本

我们需要保留最新的两个major版本。在这个例子中是major版本2和1。

```sh
declare -A latest_versions
for ver in $sorted_versions; do
  major_version=$(echo $ver | cut -d'-' -f1)
  if [ ${#latest_versions[@]} -eq 0 ]; then
    latest_versions[$major_version]=$ver
  elif [[ ! ${latest_versions[$major_version]} ]]; then
    if [ ${#latest_versions[@]} -eq 1 ]; then
      latest_versions[$major_version]=$ver
    else
      break
    fi
  fi
done

# 保留最新两个major版本的完整版本号
versions_to_keep=()
for key in "${!latest_versions[@]}"; do
  versions_to_keep+=("${latest_versions[$key]}")
done
```

结果`latest_versions`将会包含：

```
2 -> 2-0-1
1 -> 1-0-11
```

即，`versions_to_keep`中将会有：

```
2-0-1
1-0-11
```

#### 步骤5: 删除旧版本

```sh
for ver in $sorted_versions; do
  if [[ " ${versions_to_keep[@]} " =~ " ${ver} " ]]; then
    continue
  fi

  dep_name="$APP_PREFIX-$ver-deployment"
  svc_name="$APP_PREFIX-$ver-svc"
  dns_name="$APP_PREFIX-$ver-internal.aliyun.aibang.cn.local"

  kubectl delete deployment $dep_name -n $NAMESPACE
  kubectl delete service $svc_name -n $NAMESPACE
  gcloud dns record-sets delete $dns_name --type A --zone=<zone>
done
```

是的，你完全可以在CI/CD Pipeline运行过程中，先部署新的版本，然后获取和打印出正在运行的所有相关API版本，并提示用户确认删除旧版本。这样的流程能更好地控制删除操作，避免误删。

### 流程大致如下：

1. **部署新版本**：在Pipeline中执行新版本的部署。
2. **获取所有相关版本**：读取和解析现有的所有Deployment，并排序。
3. **确定保留和删除的版本**：准备用于用户确认的旧版本列表。
4. **用户确认**：在Pipeline中暂停，等待用户确认。
5. **删除旧版本**：收到确认后，删除不需要的旧版本。

### 详细实现示例：

假设你使用Jenkins Pipeline进行这个流程：

#### 1. Jenkins Pipeline 示例：

```groovy
pipeline {
    agent any

    environment {
        NAMESPACE = "<your-namespace>"
        KUBECONFIG = credentials('your-kubeconfig-credential')
        APP_PREFIX = "abj-aibang-is-onboarding-na-accouts"
    }

    stages {
        stage('Deploy New Version') {
            steps {
                script {
                    // 部署新的版本，比如通过kubectl apply
                    sh 'kubectl apply -f path/to/your/new/deployment.yaml'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    // 确认新的 Deployment 已成功部署
                    sh 'kubectl rollout status deployment/${APP_PREFIX}-<new-version>-deployment -n $NAMESPACE'
                }
            }
        }

        stage('Show Running Versions') {
            steps {
                script {
                    // 执行 Bash 脚本来获取和解析现有版本
                    def output = sh(script: '''#!/bin/bash
                    deployments=$(kubectl get deployments -n $NAMESPACE -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}' | grep "$APP_PREFIX")

                    versions=()
                    for dep in $deployments; do
                      version=$(echo $dep | sed -e "s/$APP_PREFIX-//" -e 's/-deployment//')
                      versions+=($version)
                    done

                    sorted_versions=$(printf "%s\n" "${versions[@]}" | sort -t'-' -k1,1nr -k2,2nr -k3,3nr)

                    declare -A latest_versions
                    for ver in $sorted_versions; do
                      major_version=$(echo $ver | cut -d'-' -f1)
                      if [ ${#latest_versions[@]} -eq 0 ]; then
                        latest_versions[$major_version]=$ver
                      elif [[ ! ${latest_versions[$major_version]} ]]; then
                        if [ ${#latest_versions[@]} -eq 1 ]; then
                          latest_versions[$major_version]=$ver
                        else
                          break
                        fi
                      fi
                    done

                    versions_to_keep=()
                    for key in "${!latest_versions[@]}"; do
                      versions_to_keep+=("${latest_versions[$key]}")
                    done

                    echo "Running versions: ${sorted_versions[@]}"
                    echo "Versions to keep: ${versions_to_keep[@]}"

                    for ver in $sorted_versions; do
                      if [[ " ${versions_to_keep[@]} " =~ " ${ver} " ]]; then
                        continue
                      fi

                      dep_name="$APP_PREFIX-$ver-deployment"
                      echo "To be deleted: $dep_name"
                    done
                    ''', returnStdout: true).trim()
                    // 打印输出
                    echo output
                }
            }
        }

        stage('User Confirmation') {
            steps {
                input message: "确认删除以上旧版本?", ok: "继续"
            }
        }

        stage('Delete Old Versions') {
            steps {
                script {
                    sh '''#!/bin/bash
                    deployments=$(kubectl get deployments -n $NAMESPACE -o jsonpath='{range .items[*]}{.metadata.name}{"\n"}{end}' | grep "$APP_PREFIX")

                    versions=()
                    for dep in $deployments; do
                      version=$(echo $dep | sed -e "s/$APP_PREFIX-//" -e 's/-deployment//')
                      versions+=($version)
                    done

                    sorted_versions=$(printf "%s\n" "${versions[@]}" | sort -t'-' -k1,1nr -k2,2nr -k3,3nr)

                    declare -A latest_versions
                    for ver in $sorted_versions; do
                      major_version=$(echo $ver | cut -d'-' -f1)
                      if [ ${#latest_versions[@]} -eq 0 ]; then
                        latest_versions[$major_version]=$ver
                      elif [[ ! ${latest_versions[$major_version]} ]]; then
                        if [ ${#latest_versions[@]} -eq 1 ]; then
                          latest_versions[$major_version]=$ver
                        else
                          break
                        fi
                      fi
                    done

                    versions_to_keep=()
                    for key in "${!latest_versions[@]}"; do
                      versions_to_keep+=("${latest_versions[$key]}")
                    done

                    for ver in $sorted_versions; do
                      if [[ " ${versions_to_keep[@]} " =~ " ${ver} " ]]; then
                        continue
                      fi

                      dep_name="$APP_PREFIX-$ver-deployment"
                      svc_name="$APP_PREFIX-$ver-svc"
                      dns_name="$APP_PREFIX-$ver-internal.aliyun.aibang.cn.local"

                      kubectl delete deployment $dep_name -n $NAMESPACE
                      kubectl delete service $svc_name -n $NAMESPACE
                      gcloud dns record-sets delete $dns_name --type A --zone=<zone>
                    done
                    '''
                }
            }
        }
    }
}
```

### 解释

- **Deploy New Version**：部署新版本的应用。
- **Verify Deployment**：验证新版本的部署状态。
- **Show Running Versions**：执行脚本，打印出所有当前运行的版本，并列出需要删除的旧版本。
- **User Confirmation**：使用Jenkins的`input`步骤等待用户确认。
- **Delete Old Versions**：在用户确认后，删除旧的版本及其相关资源。

这种方式确保你可以在每次新版本部署后，通过人工确认来删除旧版本，防止误删或者因为误操作导致生产环境出现问题。用户确认后删除旧版本可以提高操作的安全性。
