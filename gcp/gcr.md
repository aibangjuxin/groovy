要移除未标记的旧映像，可以使用以下shell脚本。这个脚本会列出指定仓库中的所有映像，然后找出未标记的映像并删除它们。

```bash
#!/bin/bash

# 设置你的项目ID和仓库名称
PROJECT_ID="your-project-id"
REPOSITORY="your-repository"

# 获取仓库中的所有映像
IMAGES=$(gcloud container images list --repository="gcr.io/${PROJECT_ID}/${REPOSITORY}" --format='value(name)')

# 遍历每个映像
for IMAGE in $IMAGES; do
  # 获取未标记的映像摘要
  UNTAGGED_DIGESTS=$(gcloud container images list-tags "${IMAGE}" --filter='-tags:*' --format='get(digest)')

  # 遍历并删除未标记的映像
  for DIGEST in $UNTAGGED_DIGESTS; do
    gcloud container images delete "${IMAGE}@${DIGEST}" --quiet
  done
done
```

请确保将`PROJECT_ID`和`REPOSITORY`变量替换为你的项目ID和仓库名称。在运行此脚本之前，确保已经安装并配置了Google Cloud SDK，并使用`gcloud auth login`进行身份验证。

这个脚本使用`gcloud container images list`和`gcloud container images list-tags`命令来获取仓库中的映像和未标记的摘要。然后，使用`gcloud container images delete`命令删除这些未标记的映像。

Citations:
[1] https://addshore.com/2020/04/automatic-cleanup-of-old-gcloud-container-images/
[2] https://serverfault.com/questions/979979/how-can-we-garbage-collect-old-docker-images-from-gcr
[3] https://github.com/GoogleCloudPlatform/gcr-cleaner
[4] https://stackoverflow.com/questions/33755768/how-can-i-cleanly-remove-a-container-image-from-the-google-container-registry
[5] https://cloud.google.com/sdk/gcloud/reference/artifacts/docker/images/list-vulnerabilities


是的，你可以使用以下Shell脚本来列出指定仓库中的所有映像，然后找出未标记的映像并删除它们。请注意，这个脚本使用Google Cloud的Container Registry和gcloud命令行工具。

```bash
#!/bin/bash

# 定义仓库和项目
PROJECT_ID="your-project-id"
REPO_NAME="your-repository-name"

# 列出仓库中的所有镜像
ALL_IMAGES=$(gcloud container images list --repository="${REPO_NAME}" --format='value(name)')

# 遍历每个镜像
for IMAGE in $ALL_IMAGES; do
  # 列出镜像的所有标签
  ALL_TAGS=$(gcloud container images list-tags "${REPO_NAME}/${IMAGE}" --format='get(tags)')

  # 将标签转换为数组
  IFS=',' read -ra TAGS_ARRAY <<< "$ALL_TAGS"

  # 遍历每个标签
  for TAG in "${TAGS_ARRAY[@]}"; do
    # 检查标签是否为空（未标记）
    if [ -z "$TAG" ]; then
      # 删除未标记的映像
      gcloud container images delete "${REPO_NAME}/${IMAGE}" --force-delete-tags --quiet
      echo "Deleted untagged image: ${REPO_NAME}/${IMAGE}"
      break  # 一旦找到未标记的映像，就不需要继续检查其他标签
    fi
  done
done
```

在这个脚本中：

- 替换 `your-project-id` 和 `your-repository-name` 为你的项目ID和仓库名称。
- 使用 `gcloud container images list` 列出指定仓库中的所有镜像。
- 对每个镜像，使用 `gcloud container images list-tags` 列出所有标签。
- 如果一个标签为空（未标记），就使用 `gcloud container images delete` 删除整个镜像。

请谨慎使用这个脚本，因为它会永久删除未标记的镜像。确保在生产环境中使用前进行测试，并理解脚本的执行效果。

您提供的脚本逻辑基本可行,可以实现删除GCR仓库中未标记镜像的功能。
我简单调整和补充了几点:

1. 加入project ID,以明确指定项目

2. 为gcloud命令增加--project标志,引用项目ID变量

3. 删除镜像提示中打印镜像全名,便于识别

4. 增加set -e,遇到错误自动退出脚本

调整后的脚本:

```bash
#!/bin/bash
set -e

PROJECT_ID="your-project-id"
REPO_NAME="your-repository-name"

ALL_IMAGES=$(gcloud --project=$PROJECT_ID container images list --repository=$REPO_NAME --format='value(name)')

for IMAGE in $ALL_IMAGES; do
  ALL_TAGS=$(gcloud --project=$PROJECT_ID container images list-tags $REPO_NAME/$IMAGE --format='get(tags)')

  IFS=',' read -ra TAGS_ARRAY <<< "$ALL_TAGS"

  for TAG in "${TAGS_ARRAY[@]}"; do
    if [ -z "$TAG" ]; then
      gcloud --project=$PROJECT_ID container images delete $REPO_NAME/$IMAGE --force-delete-tags --quiet
      echo "Deleted untagged image: $REPO_NAME/$IMAGE"
      break
    fi
  done
done
```

请测试执行脚本,并提出改进意见。感谢您提供代码场景,使我能针对实际需求调整和完善脚本开发能力。

```bash
#!/bin/bash

# 定义变量
PROJECT_ID="my-project-id"
REPO_NAME="my-repository-name"

# 使用 `gcloud container images list-tags` 的 `-filter` 选项来过滤掉未标记的映像
IMAGES=$(gcloud container images list-tags --repository="${REPO_NAME}" --filter="tag:!" --format='value(name)')

# 遍历每个映像
for IMAGE in $IMAGES; do

  # 使用 `gcloud container images delete` 的 `-force-delete-tags` 选项来永久删除映像及其所有标签
  gcloud container images delete "${REPO_NAME}/${IMAGE}" --force-delete-tags

done
```

# auths


# untags
```bash
#!/bin/bash
# 定义仓库和项目
PROJECT_ID="your-project-id"
REPO_NAME="your-repository-name"

# 列出仓库中的所有镜像
ALL_IMAGES=$(gcloud container images list --repository="${REPO_NAME}" --format='value(name)')

# 遍历每个镜像
for IMAGE in $ALL_IMAGES; do
  # 列出镜像的所有标签
  ALL_TAGS=$(gcloud container images list-tags "${REPO_NAME}/${IMAGE}" --format='get(tags)')

  # 将标签转换为数组
  IFS=',' read -ra TAGS_ARRAY <<< "$ALL_TAGS"

  # 遍历每个标签
  for TAG in "${TAGS_ARRAY[@]}"; do
    # 检查标签是否为空（未标记）
    if [ -z "$TAG" ]; then
      # 删除未标记的映像
      gcloud container images delete "${REPO_NAME}/${IMAGE}" --force-delete-tags --quiet
      echo "Deleted untagged image: ${REPO_NAME}/${IMAGE}"
      break  # 一旦找到未标记的映像，就不需要继续检查其他标签
    fi
  done
done


: << 'END'


- get deployment spec template spec containers env name value
```bash
      gcloud container clusters get-credentials "$cluster" --zone "$region" --project "$project"

      export https_proxy=$https_proxy


      dpname=$(kubectl get deployments -n abj-squid -o=jsonpath='{.items[*].metadata.name}')
      counter=1
      for deployment in ${dpname[@]}; do
          # awk 'BEGIN{while (a++<50) s=s "-"; print s,"deployment",s}'
          echo "--------------------- $counter: $deployment-------------------------"
          code=$(kubectl get deployment $deployment -o=jsonpath='{.metadata.labels.saaspcode}' -n abj-squid)
          echo "code: $code"
          #kubectl get deployment $deployment -o=jsonpath='{.metadata.labels.saaspcode}{"\n"}' -n abj-squid
          fqdn=$(kubectl get deployment $deployment -o=jsonpath='{.spec.template.spec.containers[0].env[?(@.name=="TARGET_FQDN")].value}' -n abj-squid)
          echo "fqdn: $fqdn"
          #kubectl get deployment $deployment -o=jsonpath='{"\n"}{.spec.template.spec.containers[0].env[?(@.name=="TARGET_FQDN")].value}{"\n"}' -n abj-squid
          echo "command reference"
          echo "./saasp_install.sh -e "${environment}" -c "${code}" -f "\"${fqdn}\"" "
          counter=$((counter + 1))
      done
```

#!/bin/bash
# 定义仓库和项目
PROJECT_ID="your-project-id"
REPO_NAMES=("your-repository-name1" "your-repository-name2" "your-repository-name3")

# 遍历每个仓库
for REPO_NAME in "${REPO_NAMES[@]}"; do
  # 列出仓库中的所有镜像
  ALL_IMAGES=$(gcloud container images list --repository="${REPO_NAME}" --format='value(name)')

  # 遍历每个镜像
  for IMAGE in $ALL_IMAGES; do
    # 列出镜像的所有标签
    ALL_TAGS=$(gcloud container images list-tags "${REPO_NAME}/${IMAGE}" --format='get(tags)')

    # 将标签转换为数组
    IFS=',' read -ra TAGS_ARRAY <<< "$ALL_TAGS"

    # 遍历每个标签
    for TAG in "${TAGS_ARRAY[@]}"; do
      # 检查标签是否为空（未标记）
      if [ -z "$TAG" ]; then
        # 删除未标记的映像
        gcloud container images delete "${REPO_NAME}/${IMAGE}" --force-delete-tags --quiet
        echo "Deleted untagged image: ${REPO_NAME}/${IMAGE}"
        break  # 一旦找到未标记的映像，就不需要继续检查其他标签
      fi
    done
  done
done



#!/bin/bash
# 定义项目
PROJECT_ID="your-project-id"

# 通过命令获取仓库名并过滤
REPO_NAMES=$(command get_repo_names | grep "your-filter-pattern")

# 将获取的仓库名转换为数组
IFS=$'\n' read -rd '' -a REPO_NAMES_ARRAY <<< "$REPO_NAMES"

# 遍历每个仓库
for REPO_NAME in "${REPO_NAMES_ARRAY[@]}"; do
  # 列出仓库中的所有镜像
  ALL_IMAGES=$(gcloud container images list --repository="${REPO_NAME}" --format='value(name)')

  # 遍历每个镜像
  for IMAGE in $ALL_IMAGES; do
    # 列出镜像的所有标签
    ALL_TAGS=$(gcloud container images list-tags "${REPO_NAME}/${IMAGE}" --format='get(tags)')

    # 将标签转换为数组
    IFS=',' read -ra TAGS_ARRAY <<< "$ALL_TAGS"

    # 遍历每个标签
    for TAG in "${TAGS_ARRAY[@]}"; do
      # 检查标签是否为空（未标记）
      if [ -z "$TAG" ]; then
        # 删除未标记的映像
        gcloud container images delete "${REPO_NAME}/${IMAGE}" --force-delete-tags --quiet
        echo "Deleted untagged image: ${REPO_NAME}/${IMAGE}"
        break  # 一旦找到未标记的映像，就不需要继续检查其他标签
      fi
    done
  done
done


#!/bin/bash

# 设置你的项目ID和仓库名称
PROJECT_ID="your-project-id"
REPOSITORY="your-repository"

# 获取仓库中的所有映像
IMAGES=$(gcloud container images list --repository="gcr.io/${PROJECT_ID}/${REPOSITORY}" --format='value(name)')

# 遍历每个映像
for IMAGE in $IMAGES; do
  # 获取未标记的映像摘要
  UNTAGGED_DIGESTS=$(gcloud container images list-tags "${IMAGE}" --filter='-tags:*' --format='get(digest)')

  # 遍历并删除未标记的映像
  for DIGEST in $UNTAGGED_DIGESTS; do
    gcloud container images delete "${IMAGE}@${DIGEST}" --quiet
  done
done

END
```

# ar-create
```bash
#!/usr/local/bin/bash
# Script for creating Google Artifact Registry repositories
# Reference: https://cloud.google.com/sdk/gcloud/reference/artifacts/repositories/create

environment=$1

region="us-east4"
location="${region}"
project="aibang-11111111-abjus-dev"
REPOSITORIES=("containers" "kong")

if [[ ${environment} == *prd* ]]; then
    echo "Setting proxy for prd environment"
    kms_project="project-kms-prod"
else
    echo "Setting proxy for dev environment"
    kms_project="project-kms-dev"
fi

echo "Using KMS project: ${kms_project}"

kms_location="${region}"
kms="projects/${kms_project}/locations/${kms_location}/keyRings/cloudStorage/cryptoKeys/cloudStorage"

echo "Location: ${location}"
echo "KMS Key: ${kms}"

for repository in "${REPOSITORIES[@]}"
do
    echo "Creating repository: ${repository}"
    echo "gcloud artifacts repositories create ${repository} \
        --location=${location} \
        --repository-format=docker \
        --description=\"Docker repository for ${repository}\" \
        --kms-key=${kms} --project ${project}"
done



: << 'END'
#!/usr/local/bin/bash
# for gcloud artifacts repositories create
# reference
# https://cloud.google.com/sdk/gcloud/reference/artifacts/repositories/create


environment=$1

region="us-east4"
location=${region}
project="aibang-11111111-abjus-dev"
REPOSITORIES=("containers" "kong")

if [[ ${environment} == *prd* ]]; then
    echo "Setting proxy prd proxy"
    kms_project=project-kms-prod
else
    echo "Setting proxy dev proxy"
    kms_project=project-kms-dev
fi


echo ${kms_project}

kms_location=$region
kms="projects/${kms_project}/locations/${kms_location}/keyRings/cloudStorage/cryptoKeys/cloudStorage"

echo "${location}"
echo "${kms}"


for repository in "${REPOSITORIES[@]}"
do
    echo "Creating repository ${repository}"
    echo "gcloud artifacts repositories create ${repository} \
        --location=${location} \
        --repository-format=docker \
        --description="Docker repository for ${repository}" \
        --kms-key=${kms} --project ${project}"
done

END
```


