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

## 这里是一些更高级的Splunk日志过滤用法示例:

1. 使用正则表达式进行更复杂的匹配

index="abc-lex-env*" namespace="namespace" sourcetype="kube:container:proxy" | regex _raw="[Ee]rror: (.*)"


上例将匹配包含"Error:"或"error:"的日志,并提取出错误信息。

2. 组合多个搜索条件

  
index="abc-lex-env*" namespace="namespace-dev" (sourcetype="kube:container:proxy" OR sourcetype="other") | regex _raw="\[error\].*\[clustering\]"


上例将在指定的index和namespace下,搜索两个sourcetype,并检查是否同时包含"[error]"和"[clustering]"。

3. 使用NOT操作符排除某些结果

index="abc-lex-env*" namespace="namespace" sourcetype="kube:container:proxy" NOT (_raw="\[info\]") | regex _raw="\[error\]"


上例将首先排除包含"[info]"的日志,然后再匹配"[error]"。

4. 使用字段提取功能

index="abc-lex-env*" | rex field=_raw "^(?<timestamp>\d{4}-\d{2}-\d{2}) (?<log_level>\w+) (?<message>.*)" | search log_level="error"


上例使用rex命令提取日志的时间戳、日志级别和消息字段,然后根据日志级别"error"进行搜索。

5. 使用统计函数聚合

index="abc-lex-env*" sourcetype="kube:container:proxy" | stats count by host
 

上例可以统计每个主机上的日志数量

好的,这里再提供一些更高级的Splunk日志过滤和分析的用法:

6. 将搜索结果输出到CSV文件

index="abc-lex-env*" sourcetype="kube:container:proxy" | eval hostname=host | fields hostname,source,message 
| outputlookup append=t /tmp/logs.csv


上例将指定的字段输出到 /tmp/logs.csv 文件中。

7. 创建数据模型进行联表分析 

可以使用`datamodels`命令创建数据模型,将不同的数据源进行联表分析。

8. 创建仪表板进行数据可视化

可以使用`dashboard`命令创建仪表板,将搜索结果进行可视化展示,如折线图、柱状图等。

9. 计划报告和警报

可以使用`alert`命令根据搜索条件设置报警规则,并使用`report`命令定期生成报告。

10. 统计不同时间段的结果

index=web | timechart span=1h count by status_code


上例可以统计每小时不同状态码的数量变化。

11. 使用子搜索优化复杂搜索

可以使用`tstats`等命令进行子搜索,提高搜索性能。

12. 使用滚动时间窗口分析最近时间段的数据。

index=web | timewrap earliest=-15m | stats count by status_code


上例统计最近15分钟内的数据。

请让我知道如果还需要补充解释哪些方面的高级用法!

### gtp 4
以下是一些更高级的Splunk过滤规则示例：

1. 匹配包含"[error]"的日志，并统计每个小时的错误数量：

   `````
   index="abc-lex-env*" namespace="namespace" sourcetype="kube:container:proxy"
   | regex _raw="\[error\]"
   | timechart span=1h count
   ```

2. 匹配同时包含"[error]"和"[clustering]"的日志，按照日志源进行分组：

   ````
   index="abc_lex_env*" namespace="namespace-dev" sourcetype="kube:container:proxy"
   | regex _raw="\[error\].*\[clustering\]"
   | stats count by source
   ````

3. 匹配包含"[crit]"的日志，并显示最近10条记录：

   ````
   index="abc-lex-env*" namespace="namespace"
   | regex _raw="\[crit\]"
   | head 10
   ````

4. 使用`eval`函数对日志进行自定义分类：

   ````
   index="abc-lex-env*" namespace="namespace" sourcetype="kube:container:proxy"
   | regex _raw="\[error\]|\[crit\]"
   | eval log_level=if(match(_raw, "\[error\]"), "Error", "Critical")
   | stats count by log_level
   ````

5. 匹配包含"[warn]"的日志，并提取额外的字段：

   ````
   index="abc-lex-env*" namespace="namespace"
   | regex _raw="\[warn\]"
   | rex field=_raw "user=(?<user>\w+)"
   | rex field=_raw "action=(?<action>\w+)"
   | table _time, user, action
   ````

请根据您的需求调整这些示例。如果需要更多高级功能，请参考[Splunk官方文档](https://docs.splunk.com/Documentation/Splunk/latest/SearchReference/Whatsinthismanual)。





