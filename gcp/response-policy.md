# format the output of gcloud dns response-policies rules list command

- gcloud dns response-policies rules list --format=json | jq '.[] | {dnsName: .dnsName, kind: "dns#responsePolicyRule", localData: {localDatas: [{kind: "dns#resourceRecordSet", name: .dnsName, rrdatas: [.rrdatas[0]], ttl: 300, type: "A"}]}, ruleName: .ruleName}'
- gcloud dns response-policies rules create $ruleName --response-policy=$your_response_policy_name --dns-name=$dnsName --local-data=name="$dnsName",type="A",ttl=300,rrdatas="$rrdatas" --project $project_id

理解了，你想要获取 `gcloud dns response-policies rules list` 命令的 JSON 输出，并将其格式化成你提供的格式。

你可以通过以下方式实现：

```bash
gcloud dns response-policies rules list --format=json | jq '.[] | {dnsName: .dnsName, kind: "dns#responsePolicyRule", localData: {localDatas: [{kind: "dns#resourceRecordSet", name: .dnsName, rrdatas: [.rrdatas[0]], ttl: 300, type: "A"}]}, ruleName: .ruleName}'
```

这个命令的作用是：

- 使用 `gcloud dns response-policies rules list --format=json` 获取 response policies rules 的 JSON 格式输出。
- 使用 jq 来处理 JSON 数据，将其格式化成你所需的格式。



# I will verify the DNS record in response policy.


#!/bin/bash
project_id=your_project_id
response-policy=your_response_policy_name

# I will create a DNS record in response policy.
# I will create a json file to define the DNS response policy rules.
```json
cat << EOF > dns-responsePolicyRule.json 

[
  {
    "dnsName": "auths.env-region.baidu.com.",
    "kind": "dns#responsePolicyRule",
    "localData": {
      "localDatas": [
        {
          "kind": "dns#resourceRecordSet",
          "name": "auths.env-region.baidu.com.",
          "rrdatas": [
            "100.68.76.42"
          ],
          "ttl": 300,
          "type": "A"
        }
      ]
    },
    "ruleName": "auths"
  },
  {
    "dnsName": "auths.env-region.baidu.com.",
    "kind": "dns#responsePolicyRule",
    "localData": {
      "localDatas": [
        {
          "kind": "dns#resourceRecordSet",
          "name": "lex.env-region.baidu.com.",
          "rrdatas": [
            "100.68.76.43"
          ],
          "ttl": 300,
          "type": "A"
        }
      ]
    },
    "ruleName": "lex"
  }
]
EOF
```
```bash
cat dns-responsePolicyRule.json | jq -r '.[] | .dnsName + " " + .ruleName + " " + .localData.localDatas[0].rrdatas[0]'

the result:
auths.env-region.baidu.com. auths 100.68.76.42
auths.env-region.baidu.com. lex 100.68.76.43


所提供的代码是一个使用 cat 和 jq 这两个实用程序的 shell 命令。cat 是一个标准的 Unix 实用程序，
它按顺序读取文件并将其写入标准输出。在本例中，它被用来读取 dns-responsePolicyRule.json 文件的内容。
cat 命令的输出随后被管道 (|) 送入 jq，这是一个轻量级、灵活的命令行 JSON 处理器。如果没有这个选项，jq 的输出将用引号括起来，
这不是你在 shell 脚本或类似文件中使用输出时想要的。
过滤器'.[] | .dnsName + " " + .ruleName + " " + .localData.localDatas[0].rrdatas[0]' 的作用如下：
- .[]从输入中获取一个 JSON 对象数组，并依次输出每个对象。- .dnsName + " " + .ruleName + " " + .localData.localDatas[0].rrdatas[0] 为每个 JSON 对象构建一个字符串。
它从每个 JSON 对象的 localData 字段中获取 dnsName、ruleName 和第一个 localDatas 中的第一个 rrdatas，然后用空格将它们连接起来。

```
- 上面的逻辑处理
```bash
#!/bin/bash
your_response_policy_name=lex-policy-name
project_id=my-project
echo $your_response_policy_name
echo $project_id
cat dns-responsePolicyRule.json | jq -r '.[] | .dnsName + " " + .ruleName + " " + .localData.localDatas[0].rrdatas[0]'| while read -r dnsName ruleName rrdatas; do
  gcloud dns response-policies rules create $ruleName --response-policy=$your_response_policy_name --dns-name=$dnsName --local-data=name="$dnsName",type="A",ttl=300,rrdatas="$rrdatas" --project $project_id
done
```

# I will filter the json file to get the dnsName and ruleName and rrdatas.

# The output will be:
cat dns-responsePolicyRule.json |jq '.[] | {ruleName, dnsName, rrdatas: .localData.localDatas[].rrdatas[]}'
# the output will be:
{
  "ruleName": "auths",
  "dnsName": "auths.env-region.baidu.com.",
  "rrdatas": "100.68.76.42"
}
{
  "ruleName": "lex",
  "dnsName": "auths.env-region.baidu.com.",
  "rrdatas": "100.68.76.43"
}

# I will use the above output to create a DNS record in response policy.
# gcloud dns response-policies create my-response-policy --description "my response policy"

gcloud dns response-policies rules create $ruleName --response-policy=$your_response_policy_name --dns-name=$dnsName --local-data=name="$dnsName",type="A",ttl=300,rrdatas="$rrdatas" --project $project_id

# I will verify the DNS record in response policy.
gcloud dns response-policies rules list --response-policy=$your_response_policy_name --project $project_id


#!/bin/bash
your_response_policy_name=lex-policy-name
project_id=my-project

echo $your_response_policy_name
echo $project_id

# 读取 JSON 文件，并逐行处理
cat dns-responsePolicyRule.json | jq -r '.[] | "gcloud dns response-policies rules create \(.ruleName) --response-policy='$your_response_policy_name' --dns-name=\(.dnsName) --local-data=name=\"\(.dnsName)\",type=\"A\",ttl=300,rrdatas=\"\(.localData.localDatas[].rrdatas[])\" --project '$project_id'"' | while read -r cmd; do
  # 执行命令
  echo "Executing command: $cmd"
  #eval "$cmd"
done

the shell script running 
```
lex-policy-name
my-project
Executing command: gcloud dns response-policies rules create auths --response-policy=lex-policy-name --dns-name=auths.env-region.baidu.com. --local-data=name="auths.env-region.baidu.com.",type="A",ttl=300,rrdatas="100.68.76.42" --project my-project
Executing command: gcloud dns response-policies rules create lex --response-policy=lex-policy-name --dns-name=auths.env-region.baidu.com. --local-data=name="auths.env-region.baidu.com.",type="A",ttl=300,rrdatas="100.68.76.43" --project my-project
```


这段脚本是一个 Bash 脚本，用于读取一个名为 `dns-responsePolicyRule.json` 的 JSON 文件，并针对该文件中的每个对象执行一条 gcloud 命令。

现在，让我逐行解释这段脚本：

1. `#!/bin/bash`：这是一个 shebang，指定了脚本要使用的解释器，这里是 bash。

2. `your_response_policy_name=lex-policy-name`：这一行定义了一个名为 `your_response_policy_name` 的变量，并将其值设置为 `lex-policy-name`。

3. `project_id=my-project`：这一行定义了一个名为 `project_id` 的变量，并将其值设置为 `my-project`。

4. `echo $your_response_policy_name`：这一行使用 `echo` 命令打印出 `your_response_policy_name` 变量的值。

5. `echo $project_id`：这一行使用 `echo` 命令打印出 `project_id` 变量的值。

6. `cat dns-responsePolicyRule.json`：这个命令会读取名为 `dns-responsePolicyRule.json` 的 JSON 文件的内容。

7. `jq -r '.[] | "gcloud dns response-policies rules create ..."'`：这个命令使用 jq 工具来处理 JSON 数据。解释如下：

   - `.[]`：这个部分会迭代 JSON 数组中的每个元素。
   - `"gcloud dns response-policies rules create ..."`：这部分构建了 gcloud 命令，利用 JSON 对象中的字段值来填充相应的参数。

8. `while read -r cmd; do`：这个命令会逐行读取 jq 输出的命令，并将每行内容赋给变量 `cmd`。

9. `echo "Executing command: $cmd"`：这个命令会输出当前要执行的命令，用于显示执行过程。

10. `#eval "$cmd"`：这行是一个注释，被 `#` 符号注释掉了。在原来的脚本中，这行代码被注释掉了，所以并不会执行 jq 输出的命令。