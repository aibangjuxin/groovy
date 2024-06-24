

- bash
```
#!/bin/bash

# 创建或清空输出文件
output_file="updated_java_apis_mig.csv" 
> "$output_file"

# 读取 java_apis_mig.csv 中的每一行
while IFS= read -r line; do
    # 提取 API 名称（第三个字段）
    api_name=$(echo "$line" | cut -d',' -f3)

    # 在 java-firestore.csv 中搜索此 API 名称并提取团队（第三个字段）
    team=$(grep "$api_name" java-firestore.csv | cut -d',' -f3 | head -n 1)

    # 将团队名称附加到原始行
    new_line="$line,$team"

    # 将修改后的行写入输出文件
    echo "$new_line" >> "$output_file"
done < java_apis_mig.csv
```
```
```
```bash
#!/bin/bash

input_file="java_apis_mig.csv"
reference_file="java-firestore.csv"
output_file="updated_java_apis_mig.csv"

# 检查输入文件是否存在
if [[ ! -f "$input_file" || ! -f "$reference_file" ]]; then
    echo "错误：输入文件不存在"
    exit 1
fi

# 创建或清空输出文件
> "$output_file"

# 使用 awk 处理文件
awk -F',' '
    NR==FNR {
        teams[$3] = $3;
        next
    }
    {
        team = (teams[$3] != "") ? teams[$3] : "N/A";
        print $0 "," team
    }
' "$reference_file" "$input_file" > "$output_file"

echo "处理完成。结果已保存在 $output_file 中。"
```
