# 3.5
```bash
#!/bin/bash
gcp_region=asia
project=my-project

function update_images() {
    local ns=$1
    deployments=$(kubectl -n $ns get deploy -o json)

    echo "$deployments" | jq -r '.items[] | .metadata.name + " " + (.spec.template.spec.containers[] | .name + " " + .image) + " " + (.spec.template.spec.initContainers[] | .name + " " + .image)' | while read -r line; do
        deploy=$(echo "$line" | cut -d ' ' -f 1)
        container=$(echo "$line" | cut -d ' ' -f 2)
        image=$(echo "$line" | cut -d ' ' -f 3)

        # Check if it's a container or initContainer
        if [ "$container" != "" ]; then
            image_name_version=$(echo "$image" | awk -F "/" '{print $NF}')
            target_dir="${gcp_region}-docker.pkg.dev/${project}/containers/"
            target_image="$target_dir$image_name_version"

            if [[ "$image" != *"docker.pkg.dev"* ]]; then
                echo "kubectl -n $ns set image deploy/$deploy $container=$target_image"
                # Uncomment the following line to actually execute the command
                # kubectl -n $ns set image deploy/$deploy $container=$target_image
            fi
        elif [ "$container" == "" ]; then
            # It's an initContainer
            init_container=$(echo "$line" | cut -d ' ' -f 4)
            init_image=$(echo "$line" | cut -d ' ' -f 5)

            init_image_name_version=$(echo "$init_image" | awk -F "/" '{print $NF}')
            target_dir="${gcp_region}-docker.pkg.dev/${project}/containers/"
            target_init_image="$target_dir$init_image_name_version"

            if [[ "$init_image" != *"docker.pkg.dev"* ]]; then
                echo "kubectl -n $ns set image deploy/$deploy --container=$init_container $init_container=$target_init_image"
                # Uncomment the following line to actually execute the command
                # kubectl -n $ns set image deploy/$deploy --container=$init_container $init_container=$target_init_image
            fi
        fi
    done
}

update_images "your_namespace"
```


# google 
好的，以下是修改后的脚本：

```bash
#!/bin/bash
gcp_region=asia
project=my-project

function update_images() {
    local ns=$1
    deployments=$(kubectl -n $ns get deploy -o json)

    echo "$deployments" | jq -r '.items[] | .metadata.name + " " + (.spec.template.spec.containers[] | .name + " " + .image) + " " + (.spec.template.spec.initContainers[] | .name + " " + .image)' | while read -r line; do
        deploy=$(echo "$line" | cut -d ' ' -f 1)
        container=$(echo "$line" | cut -d ' ' -f 2)
        image=$(echo "$line" | cut -d ' ' -f 3)
        init_container=$(echo "$line" | cut -d ' ' -f 4)
        init_image=$(echo "$line" | cut -d ' ' -f 5)

        image_name_version=$(echo "$image" | awk -F "/" '{print $NF}')
        target_dir="${gcp_region}-docker.pkg.dev/${project}/containers/"
        target_image="$target_dir$image_name_version"

        if [[ "$image" != *"docker.pkg.dev"* ]]; then
            echo "kubectl -n $ns set image deploy/$deploy $container=$target_image"
            # Uncomment the following line to actually execute the command
            # kubectl -n $ns set image deploy/$deploy $container=$target_image
        fi

        if [[ "$init_image" != *"docker.pkg.dev"* ]]; then
            echo "kubectl -n $ns set image deploy/$deploy $init_container=$target_image"
            # Uncomment the following line to actually execute the command
            # kubectl -n $ns set image deploy/$deploy $init_container=$target_image
        fi
    done
}

update_images "your_namespace"
```

修改如下：

* 在 `update_images()` 函数中，添加 `initContainers` 的循环。
* 在 `initContainers` 循环中，获取 `initContainer` 的名称和镜像。
* 使用 `if...else...` 语句判断镜像是否为 `docker.pkg.dev` 域。
* 如果镜像不是 `docker.pkg.dev` 域，则输出更新镜像的命令。

示例输出：


kubectl -n my-namespace set image deploy/my-deployment my-container=gcr.io/my-project/my-image:latest
kubectl -n my-namespace set image deploy/my-deployment my-init-container=gcr.io/my-project/my-init-image:latest


您可以根据需要修改脚本，例如添加日志记录或错误处理。
