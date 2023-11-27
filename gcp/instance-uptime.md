要获取Google Cloud上实例的启动时间，您可以使用以下命令:

```bash
gcloud compute instances describe INSTANCE_NAME --format="value(startTime)"
```

请将 `INSTANCE_NAME` 替换为您要查询的实例的名称。这将返回实例的启动时间。

如果您想得到实例已经运行的时间，可以结合使用 `date` 命令来计算:

```bash
INSTANCE_NAME="your-instance-name"
START_TIME=$(gcloud compute instances describe $INSTANCE_NAME --format="value(startTime)")
CURRENT_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
echo "Instance $INSTANCE_NAME has been running for: $(date -u -d "$CURRENT_TIME" -d "$START_TIME" +"%H:%M:%S")"
```

这会输出实例已经运行的时间。请注意，这里使用 `-u` 选项来确保使用协调世界时 (UTC)。

如果您想以 Markdown 格式提供结果，我可以帮您转换成相应的格式。


使用 `gcloud compute instance list` 命令列出所有实例时，可以使用 `--format` 选项来指定输出格式。以下是获取启动时间的示例：

```
gcloud compute instances list --format="value(zone, name, creation_timestamp)"
```

此命令将输出以下格式的表格：

```
zone | name | creation_timestamp
------- | -------- | --------
us-central1-a | my-instance | 2023-11-27T14:50:00.000-08:00
```

`creation_timestamp` 列包含实例的启动时间戳。您可以使用 `date` 命令将此时间戳转换为更易读的格式，例如：


date -d @$(gcloud compute instances list --format="value(zone, name, creation_timestamp)" | awk '{print $3}')


此命令将输出以下内容：

```
2023-11-27T06:50:00+08:00
```

您还可以使用 `gcloud compute instances describe` 命令获取实例的详细信息，包括启动时间。以下是获取启动时间的示例：

```
gcloud compute instances describe my-instance
```

此命令将输出以下内容：

```
Name: my-instance
Zone: us-central1-a
Machine type: n1-standard-1
Boot disk size: 10 GB
Boot disk type: pd-standard
Boot disk device name: /dev/disk/by-id/google-pd-standard-us-central1-a
Boot disk path: /dev/disk/by-id/google-pd-standard-us-central1-a
Operating system: Debian GNU/Linux 11 (bullseye)
Metadata:
  creation-timestamp: 2023-11-27T14:50:00.000-08:00
```

`creation-timestamp` 属性包含实例的启动时间戳。

```bash
#!/bin/bash
keyword="aibangrt"

instance_list=$(gcloud compute instances list --filter="name~${keyword}*" --format="value(name,ZONE)")
while read -r instances; do
    NAME=$(echo "$instances" | cut -f1)
    zone=$(echo "$instances" | cut -f2)
    
    # 获取实例的启动时间，并将其转换为本地时区时间
    START_TIME=$(gcloud compute instances describe $NAME --zone $zone --format="value(creationTimestamp)")
    START_TIME_LOCAL=$(echo $START_TIME | awk '{print strftime("%Y-%m-%dT%H:%M:%S%z", $1)}')

    CURRENT_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
    echo "Instance $NAME has been running for: $(date -u -d "$CURRENT_TIME" -d "$START_TIME_LOCAL" +"%H:%M:%S")"
done <<< "$instance_list"

#!/bin/bash
keyword="aibangrt"

instance_list=$(gcloud compute instances list --filter="name~${keyword}*" --format="value(name,ZONE)")
while read -r instances; do
    NAME=$(echo "$instances" | cut -f1)
    zone=$(echo "$instances" | cut -f2)

    # 获取实例的启动时间戳
    START_TIMESTAMP=$(gcloud compute instances describe $NAME --zone $zone --format="value(creationTimestamp.timestamp())")

    # 使用awk处理时间戳格式
    START_TIME_LOCAL=$(echo $START_TIMESTAMP | awk -F"." '{print strftime("%Y-%m-%dT%H:%M:%S%z", $1)}')

    CURRENT_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ")
    echo "Instance $NAME has been running for: $(date -u -d "$CURRENT_TIME" -d "$START_TIME_LOCAL" +"%H:%M:%S")"
done <<< "$instance_list"
```
