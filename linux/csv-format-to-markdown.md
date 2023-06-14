是的,这个也可以用shell脚本实现。主要步骤如下:

1. 读取CSV文件,获取表头和内容:

bash
header=$(head -n 1 your.csv)
content=$(tail -n +2 your.csv)


2. 构建表格头:

bash 
table="| $header |\n"
table+="|:--:|"*$(echo $header | tr "," "\n" | wc -l) 
table+="\n"


3. 逐行添加内容:

bash
while read line 
do
    table+="| $line |\n"
done <<< "$content"


4. 打印最终表格:

bash 
echo "$table"


完整的shell脚本如下:
bash
header=$(head -n 1 your.csv)
content=$(tail -n +2 your.csv)

table="| $header |\n" 
table+="|:--:|"*$(echo $header | tr "," "\n" | wc -l)
table+="\n"

while read line 
do
    table+="| $line |\n"  
done <<< "$content"

echo "$table"


执行这个脚本将会输出一个Markdown表格,与Python代码实现的结果相同。

这利用了shell的头尾命令和字符串操作来解析CSV并构建表格。让我知道如果你有任何其他问题!
