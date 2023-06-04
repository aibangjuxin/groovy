Mermaid 是一种文本渲染引擎,可以从简单的文本描述生成图表,序列图,甘特图,状态图,等等。它非常适合用在 Markdown 文档中。
这里给出一些 Mermaid 的用法示例:
## 流程图
mermaid
graph LR
A[方形] -- 链接 --> B((圆))
B --> C{条件a}
C -->|a=1| D(结果1)
C -->|a=2| E(结果2)
F[横向流程图]
## 序列图
mermaid
sequenceDiagram
对象1->>对象2: 消息1
对象2-->>对象1: 消息2
对象1->>对象3: 消息3
对象3--x对象2: 消息4
对象1-x对象3: 消息5
## 甘特图
mermaid
gantt
       dateFormat  YYYY-MM-DD
       title 软件开发甘特图
       section 设计
       需求                      :done,    des1, 2014-01-06,2014-01-08
       原型                      :active,  des2, 2014-01-09, 3d
       UI设计                     :         des3, after des2, 5d
    
## 状态图
mermaid
stateDiagram
[*] --> 未启动
未启动 --> [*]
未启动 --> 运行中
运行中 --> 未启动
运行中 --> 等待输入 
等待输入 --> 运行中
等待输入 --> 结束
结束 --> [*]
Mermaid 还支持类图、用户旅程图等等。希望这个示例能给你提供一个 Mermaid 入门的参考。更详细的语法可以参考 Mermaid 官方文档
