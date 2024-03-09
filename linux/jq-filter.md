- filter-all-offplatform-apis-deployMigRefs.sh
```bash
#!/bin/bash
# step 1 提取我要的字段
# cat all-offplatform-apis-deployMigRefs.json|jq '{name: .name, assetId: .assetId, majorVersion: .majorVersion, instanceLabel: .instanceLabel, minor_version: .version,deployedMigRefs: .deployedMigRefs}' > filter-all-deployMigrefs.json 
# step 2
# cp filter-all-deployMigrefs.json data.json 
# 定义环境列表
environments=("dev.env" "ad.env")

# 循环遍历每个环境
for env in "${environments[@]}"; do
    # 执行jq命令，并传递环境信息作为参数
    cat data.json | jq -r --arg env "$env" 'select(.deployedMigRefs[$env]) | {env: $env | split(".") | .[0], region: $env | split(".") | .[1], name: .name, assetId: .assetId, majorVersion: .majorVersion, minor_version: .minor_version, segment: .deployedMigRefs[$env].path.segments[1]} | [.env, .region, .name, .assetId, .majorVersion, .minor_version, .segment] | @csv'
done
