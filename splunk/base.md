## 在Splunk中根据搜索日志结果创建Alert、Report和Dashboard

### 创建Alert

1. 执行搜索日志的搜索语法,过滤出需要监控的结果。

2. 点击"Save As" -> "Alert",将搜索保存为一个警报。  

3. 在 "Trigger conditions" 设置触发条件,如搜索结果数量等。

4. 在 "Alert actions" 中设置警报触发后的动作,如发送邮件、执行脚本等。

5. 保存警报,当满足触发条件时就会执行已设置的动作。

### 创建Report

1. 执行搜索日志的搜索语法,过滤出需要的结果。 

2. 点击"Save As" -> "Report",将搜索保存为一个报表。

3. 在 "Report settings" 中调整报表的显示方式,如表格、图表等。

4. 点击"Preview",查看并修改报表的样式。

5. 保存报表,之后就可以定时生成或手动打开该报表。

### 创建Dashboard

1. 准备好要添加到仪表板的报表、图表等内容。

2. 点击"Create" -> "Dashboard",创建一个新的空白仪表板。 

3. 点击"Add" -> "Report/Chart"等,将准备好的内容添加到仪表板。

4. 调整仪表板布局,设置自动刷新间隔等参数。

5. 保存仪表板,之后可以打开和共享这个仪表板。

在Splunk中,要添加到仪表板的报表、图表,一般可以通过以下几种方式生成:

## 生成报表

1. 直接从搜索结果保存为报表
    
    执行搜索后,点击"Save As" -> "Report",将搜索结果保存为报表。

2. 使用报表向导创建
    
    点击"Create" -> "Report",使用报表向导步骤创建报表。

3. 从 existing pivot报表转换 
    
    选择现有的pivot报表,点击"Convert to Report"转换为报表。

### 生成图表

1. 从搜索结果保存为图表

    执行搜索后,点击"Visualization",选择图表类型保存为图表。

2. 使用图表向导创建

    点击"Create" -> "Chart",使用图表向导步骤创建图表。
    
3. 从报表中提取图表

    打开报表后,点击感兴趣的图表,选择"Extract Chart"提取为图表。

4. 直接绘制统计图表

    使用`chart`、`timechart`等命令直接绘制不同类型的图表。

#### 小结

- 从现有搜索和报表转换是最快捷的方式
- 也可以利用向导和命令从头创建图表
- 结合使用各种方式来生成丰富的图表内容


好的,根据你提供的示例,可以总结出以下在Splunk中过滤k8s日志的技巧:

# Splunk过滤k8s日志技巧

## 根据命名空间过滤

- 过滤指定命名空间namespace的日志

index="abc-lex-env*" namespace="namespace"

## 根据sourcetype过滤

- 过滤kube:container:proxy类型的日志

sourcetype="kube:container:proxy" 

## 根据字符串正则过滤

- 过滤包含[error]的日志 

| regex _raw="\[error\]"

- 过滤包含[crit]的日志

| regex _raw="\[crit\]"

## 组合过滤

- 过滤namespace-dev的proxy日志,且包含[error]和[clustering]

index="abc_lex_env*" namespace="namespace-dev"  
sourcetype="kube:container:proxy"
| regex _raw="\[error\]" 
| regex _raw="\[clustering\]"


## 统计关键字出现次数

- 统计communicate()出现次数

| regex _raw="communicate()" | stats count by _raw

## 总结

- 组合索引、命名空间、源类型和字符串模式进行过滤
- 可以统计错误或关键字出现频次
- 充分利用Splunk的搜索语言分析日志