- [lifecycle.json](#lifecyclejson)
- [only delete objects that are older than 7 days](#only-delete-objects-that-are-older-than-7-days)
- [object number limit](#object-number-limit)
- [keep number of object](#keep-number-of-object)
  - [calude 可以实现这个需求,可以通过maxItems条件来实现:](#calude-可以实现这个需求可以通过maxitems条件来实现)
  - [google](#google)
- [保留一定数量的版本文件的对象生命周期配置的](#保留一定数量的版本文件的对象生命周期配置的)

# lifecycle.json 
https://cloud.google.com/storage/docs/lifecycle-configurations?hl=zh-cn#command-line

```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {"type": "Delete"},
        "condition": {
          "numNewerVersions": 2,
          "isLive": false
        }
      },
      {
        "action": {"type": "Delete"},
        "condition": {
          "daysSinceNoncurrentTime": 7
        }
      }
    ]
}
}
```
该配置文件定义了两个对象生命周期规则，分别用于删除存储分区中的对象。

第一个规则将删除存储分区中非当前版本的对象，如果存储分区中有两个或多个较新的版本对象。

第二个规则将删除存储分区中非当前版本的对象，如果这些对象已保留 7 天以上。

详细解释如下：

lifecycle 元素是对象生命周期配置的根元素。
rule 元素定义了对象生命周期规则。
action 元素定义在对象满足条件时要采取的操作。
condition 元素定义对象必须满足的条件。
第一个规则

action 元素指定操作类型为 Delete。
condition 元素指定两个条件：
numNewerVersions 条件指定存储分区中必须有两个或多个较新的版本对象。
isLive 条件指定对象必须是非当前版本。
第二个规则

action 元素指定操作类型为 Delete。
condition 元素指定条件：
daysSinceNoncurrentTime 条件指定对象必须已保留 7 天以上。
示例

假设存储分区中有一个对象，其版本 ID 为 1234567890。如果满足以下条件，则该对象将被第一个规则删除：

存储分区中必须有两个或多个较新的版本对象。
对象的版本 ID 必须为 1234567890。
如果满足以下条件，则该对象将被第二个规则删除：

对象的版本 ID 必须为 1234567890。
对象已保留 7 天以上。
注意事项

对象生命周期配置的更改可能需要长达 24 小时才能生效。
您可以使用 Cloud Storage 使用情况日志或 Pub/Sub 通知来跟踪 Cloud Storage 执行的生命周期管理操作。


# only delete objects that are older than 7 days
参考这个配置https://cloud.google.com/storage/docs/lifecycle-configurations?hl=zh-cn#command-line
我给我的google工程下的Buckets创建了如下一个生命周期规则：
请帮我验证和解释下面的这个规则的具体配置
```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {"type": "Delete"},
        "condition": {
          "daysSinceNoncurrentTime": 7
        }
      }
    ]
  }
}
```

您创建的生命周期规则配置有效。该规则指定，任何存储桶中的非当前版本对象，如果自创建之日起超过 7 天，则将被删除。

具体来说，该规则包含一个规则，该规则具有以下属性：

* **操作：** 删除
* **条件：** 非当前版本对象的有效期超过 7 天

**操作：**

操作指定在满足条件时要执行的操作。在这种情况下，操作是删除对象。

**条件：**

条件指定对象必须满足才能触发操作。在这种情况下，条件是对象的有效期必须超过 7 天。

**有效期：**

有效期是指对象从创建之日起可以保留的时间。在这种情况下，有效期为 7 天。

以下是该规则的示意图：

```
对象创建

7 天后

如果对象是非当前版本，则删除
```

该规则可以用于自动删除存储桶中的旧对象，以释放存储空间并减少成本。

该规则会在对象成为非当前版本后的7天内删除该对象。

具体解释如下:

1. 该规则包含一个action和一个condition。

2. action指明当condition满足时的动作类型是Delete,即执行删除对象操作。

3. condition指定的是daysSinceNoncurrentTime为7天。

4. daysSinceNoncurrentTime表示对象成为非当前版本后的天数。

5. 当对象被覆盖或者被删除后,就成为非当前版本的对象。

6. 所以这个规则的效果是:

当对象成为非当前版本后,如果超过7天,就会被删除处理。

这可以避免过期的非当前版本对象占用存储空间。

7天是一个比较合理的设置,既能保留非当前版本一段时间以备恢复,也会定期删除过期对象释放空间。

所以这个规则的配置是合理的,可以实现删除过期非当前版本对象的目的。

# object number limit
该配置包含两条规则:
当对象的更新版本数量达到1000时,执行删除操作。这可以控制bucket中的对象总数。
删除30天之前的历史版本。daysSinceCustomTimeBefore指删除自定义时间之前的版本,可以实现定期清理历史版本。
两条规则结合可以实现:当对象数量超过阈值时,清理30天之外的历史版本。

这种基于对象数量+定期清理的配置,可以防止对象数量膨胀导致存储费用过高,同时也能保留一段时间的历史版本,符合大多数场景的需求。

可以根据具体情况调整数量阈值和历史保留时间。这种配置方式既可控对象总数,也能定期清理历史,比较合理
```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {
          "type": "Delete"
        },
        "condition": {
          "numNewerVersions": 1000 
        }
      },
      {
        "action": {
          "type": "Delete"
        },        
        "condition": {
          "daysSinceCustomTimeBefore": 30
        }
      }
    ]
  }
}
```

是的,numNewerVersions指的是单个对象的新版本数量,不是整个bucket中的对象总数。

对于同一个对象,每上传一个新版本,该对象的numNewerVersions就会加1。

举个例子:

对象example.txt的最初版本是1。

上传了一个新版本,example.txt的版本现在是2,numNewerVersions变为1。

再上传一个新版本,版本升级到3,numNewerVersions变为2。

当example.txt的numNewerVersions达到阈值1000时,根据生命周期规则,会删除该对象的旧版本,保持最新的1000个版本。

所以这个条件是针对同一个对象的不同版本数量,而不是bucket内所有对象的总数。

如果要限制bucket的对象总数,可以考虑使用maxItems条件,它表示总对象数量达到阈值时触发删除操作。

两种条件配合使用可以实现针对单个对象版本和整个bucket的数量控制。


# keep number of object 
- Q 
- 在不启用版本控制的情况下,我如果想要仅仅保留Buckets里面的最新的1000个对象?能否实现?也就是基于数量来保持,始终保障这个Buckets里面有1000个对象

是的,在未启用版本控制的bucket下,也可以仅保留最新的1000个对象。

## calude 可以实现这个需求,可以通过maxItems条件来实现:

```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {
          "type": "Delete"
        },
        "condition": {
          "maxItems": 1000
        }
      }
    ]
  }
}
```

这条规则的作用是:

当bucket中的对象数量超过1000时,按照最后修改时间从早到晚的顺序删除额外的对象,直到保留1000个最新的对象。

maxItems用于指定保留的最大对象数量。

这样可以实现一个“移动窗口”,始终保持bucket中只有最新的1000个对象。当新增对象导致总数超过1000时,会删除最早修改的对象。

需要注意的是,这个规则适用于未开启版本控制的bucket。如果启用了版本控制,需要配合其它条件来实现数量控制。

另外,也可以结合时间条件,比如再加一条30天之前的对象删除规则。

这样可以避免重要的老对象被大量新增对象直接删除的情况。


## google 

可以实现。您可以使用 `countBasedDelete` 条件来基于对象的数量来触发生命周期规则。该条件具有以下属性：

* **count：** 要保留的对象的数量。

以下是不启用版本控制的情况下，仅保留存储桶中最新 1000 个对象的生命周期规则配置示例：

```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {
          "type": "Delete"
        },
        "condition": {
          "countBasedDelete": {
            "count": 1000
          }
        }
      }
    ]
  }
}
```

该规则指定，如果存储桶中的对象数量超过 1000，则将删除最旧的对象。

以下是该规则的示意图：

```
对象数量超过1000

删除最旧的对象
```

您可以使用以下命令来创建此规则：

```
gcloud storage lifecycle set --bucket <bucket-name> \
--lifecycle-config-file <lifecycle-config-file>
```

其中，`<bucket-name>` 是存储桶的名称，`<lifecycle-config-file>` 是包含生命周期规则配置的文件。

您还可以使用 `gcloud storage lifecycle list` 命令来查看存储桶的生命周期规则。

请注意，如果您添加或删除对象，则此规则将根据需要自动更新。

很抱歉，Google Cloud Storage 的生命周期规则并不支持基于对象数量的条件。目前支持的条件包括 `age`（对象的存在时间）和 `isLive`（对象是否处于活动状态）等，但没有直接支持基于对象数量的条件。

因此，您不能使用您提供的 JSON 配置来实现直接基于对象数量的生命周期规则。相反，您可能需要使用其他方法，比如定期查询 Bucket 中对象的数量，然后根据需要删除多余的对象，以确保保持所需的数量。这可能需要编写一些脚本或使用其他工具与 Google Cloud Storage 交互。

以下是一个示例脚本（使用 Python 和 Google Cloud Storage API）的简单示例，该脚本检查 Bucket 中的对象数量并删除多余的对象：

```python
from google.cloud import storage

def delete_excess_objects(bucket_name, max_items):
    client = storage.Client()
    bucket = client.bucket(bucket_name)
    
    # 获取 Bucket 中的所有对象
    blobs = list(bucket.list_blobs())

    # 计算要删除的对象数量
    excess_count = len(blobs) - max_items

    # 删除多余的对象
    if excess_count > 0:
        for blob in blobs[:excess_count]:
            blob.delete()

if __name__ == "__main__":
    bucket_name = "your_bucket_name"
    max_items = 1000

    delete_excess_objects(bucket_name, max_items)
```

请替换 `your_bucket_name` 为实际的 Bucket 名称。此脚本列出了 Bucket 中的所有对象，然后删除超过所需数量的对象。您可以根据需要将其集成到您的工作流中，并根据需要进行修改。


以下是一个简单的 Shell 脚本示例，使用 `gsutil` 命令行工具按时间排序并保留最新的 1000 个对象，删除之前的对象。请确保已经安装并配置了 `gsutil` 工具。

```bash
#!/bin/bash

# 设置您的 Bucket 名称和要保留的最新对象数量
BUCKET_NAME="your_bucket_name"
MAX_ITEMS=1000

# 列出 Bucket 中的所有对象，并按时间排序
objects=($(gsutil ls -l gs://$BUCKET_NAME | sort -k2 -r | awk '{print $NF}'))

# 计算要删除的对象数量
excess_count=$(( ${#objects[@]} - $MAX_ITEMS ))

# 删除多余的对象
if [ $excess_count -gt 0 ]; then
  for ((i = $MAX_ITEMS; i < ${#objects[@]}; i++)); do
    gsutil rm ${objects[$i]}
  done
fi
```

请替换 `your_bucket_name` 为实际的 Bucket 名称。此脚本使用 `gsutil ls -l` 命令列出 Bucket 中的所有对象，并按时间排序。然后，它计算要删除的对象数量，并使用 `gsutil rm` 命令删除多余的对象。

请注意，这个脚本依赖于 gsutil 输出的格式，如果 gsutil 命令的输出格式发生变化，可能需要对脚本进行调整。建议在测试环境中先测试脚本，确保其按预期工作。

以下是一个脚本，使用 gsutil 命令行工具按时间排序并保留最新的 1000 个对象，删除之前的对象：

```python
#!/usr/bin/env python3

import argparse
import datetime
import os


def main():
    parser = argparse.ArgumentParser(description="保留存储桶中的最新 1000 个对象")
    parser.add_argument("--bucket", required=True, help="存储桶名称")
    args = parser.parse_args()

    bucket = args.bucket

    # 列出存储桶中的所有对象
    object_list = gsutil.ls(bucket)

    # 按创建时间排序对象
    object_list.sort(key=lambda x: x.timeCreated)

    # 获取最新的 1000 个对象
    latest_objects = object_list[:1000]

    # 获取要删除的对象的列表
    delete_objects = [object for object in object_list if object not in latest_objects]

    # 删除对象
    for object in delete_objects:
        gsutil.rm(object)


if __name__ == "__main__":
    main()
```

该脚本的工作原理如下：

1. 使用 `gsutil ls` 命令列出存储桶中的所有对象。
2. 使用 `sort()` 方法按创建时间对对象进行排序。
3. 使用 `slice()` 方法获取最新的 1000 个对象。
4. 使用 `list()` 方法获取要删除的对象的列表。
5. 使用 `gsutil rm` 命令删除对象。

以下是脚本的使用示例：

```
$ python keep_latest_1000.py --bucket my-bucket
```

该命令将保留存储桶 `my-bucket` 中的最新 1000 个对象，并删除之前的对象。

您可以根据需要修改脚本，例如：

* 更改保留的对象数量。
* 添加其他条件来过滤要删除的对象。
* 将脚本重命名为 `keep_latest_1000.sh`，使其可执行。

当然可以。以下是一个 shell 脚本，使用 gsutil 命令行工具按时间排序并保留最新的 1000 个对象，删除之前的对象：

```shell
#!/bin/bash

# 获取存储桶名称
bucket=$1

# 列出存储桶中的所有对象
objects=$(gsutil ls $bucket)

# 按创建时间排序对象
objects=$(echo "$objects" | sort -t '/' -k 1,1n)

# 获取最新的 1000 个对象
latest_objects=$(echo "$objects" | head -n 1000)

# 获取要删除的对象的列表
delete_objects=$(echo "$objects" | grep -vE "$latest_objects")

# 删除对象
for object in $delete_objects; do
    gsutil rm $object
done
```

该脚本的工作原理与 Python 脚本相同。唯一的区别是使用 `sort` 命令和 `head` 命令来按创建时间排序对象并获取最新的 1000 个对象。

以下是脚本的使用示例：

```
$ bash keep_latest_1000.sh my-bucket
```

该命令将保留存储桶 `my-bucket` 中的最新 1000 个对象，并删除之前的对象。

您可以根据需要修改脚本，例如：

* 更改保留的对象数量。
* 添加其他条件来过滤要删除的对象。
* 将脚本重命名为 `keep_latest_1000.sh`，使其可执行。


# 保留一定数量的版本文件的对象生命周期配置的

当然可以。以下是一个实现保留一定数量的版本文件的对象生命周期配置的 JSON 示例：

```json
{
  "lifecycle": {
    "rule": [
      {
        "action": {"type": "Delete"},
        "condition": {
          "numNewerVersions": {
            "max": 2
          },
          "isLive": false
        }
      }
    ]
  }
}
```

此配置将删除存储分区中非当前版本的对象，如果存储分区中有两个或多个较新的版本对象。

要实现此配置，您可以使用以下命令：

```
gsutil lifecycle set --lifecycle-rule=
  "numNewerVersions:max=2,action=delete"
  my-bucket
```

这将创建一个名为 `my-bucket-lifecycle-config` 的对象生命周期配置，并将其应用到存储分区 `my-bucket`。

**注意事项**

* 对象生命周期配置的更改可能需要长达 24 小时才能生效。
* 您可以使用 Cloud Storage 使用情况日志或 Pub/Sub 通知来跟踪 Cloud Storage 执行的生命周期管理操作。

以下是一个使用此配置的示例：

假设存储分区 `my-bucket` 中有以下对象：

* 对象 `object1` 有两个较新的版本。
* 对象 `object2` 只有一个较新的版本。

在 24 小时内，对象 `object1` 的两个较新的版本将被删除。对象 `object2` 将保留，因为它只有一个较新的版本。

您可以根据需要调整此配置以满足您的特定需求。例如，您可以将 `max` 值更改为 3 或更高，以保留三个或更多较新的版本。


