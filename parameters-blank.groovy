string="csrs1234"

if [[ $string == csrs* ]]; then
    result=${string:4} # 如果以 "csrs" 开头，从第五个字符开始截取到结尾
else
    result=${string:0:5} # 如果不以 "csrs" 开头，截取前五个字符
fi

echo


在Jenkins Pipeline中，您可以使用Groovy语言来判断参数是否为空，并将其赋值给另一个变量。以下是一个示例：

```
pipeline {
  agent any
  parameters {
    string(name: 'PARAMETER_NAME', defaultValue: '', description: 'Description of parameter')
  }
  stages {
    stage('Example') {
      steps {
        script {
          def parameterValue = params.PARAMETER_NAME ?: 'default_value'
          echo "Parameter value: ${parameterValue}"
        }
      }
    }
  }
}
```

在这个示例中，我们定义了一个
名为`PARAMETER_NAME`的字符串参数，并将其默认值设置为空字符串。
在`Example`阶段中，我们使用Groovy的null合并运算符`?:`来检查参数是否为空。如果参数为空，则将其替换为`default_value`。
最后，我们将参数值打印到控制台。

您可以根据需要修改示例中的变量名和默认值。


可以使用 Groovy 的三元运算符来实现这个功能。示例如下：

def myValue = null
def result = myValue ? myValue : "default value"

println result // 输出: default value
上述代码中，myValue 被赋值为 null，
则三元运算符会判断 myValue 是否为真。由于 myValue 是空值，
所以表达式的值为假，因此返回默认值 "default value"。
你可以根据需要修改变量名、默认值和输出方式等细节


pipeline传入参数 groovy
判断不为空则赋值给A
判断为空则赋值给B
在Jenkins Pipeline中，您可以使用Groovy语言来判断参数是否为空，并将其赋值给不同的变量。以下是一个示例：


```
pipeline {
  agent any
  parameters {
    string(name: 'PARAMETER_NAME', defaultValue: '', description: 'Description of parameter')
  }
  stages {
    stage('Example') {
      steps {
        script {
          def variableA = ''
          def variableB = ''
          if (params.PARAMETER_NAME) {
            variableA = params.PARAMETER_NAME
          } else {
            variableB = 'default_value'
          }
          echo "Variable A: ${variableA}"
          echo "Variable B: ${variableB}"
        }
      }
    }
  }
}
```

在这个示例中，我们定义了一个名为`PARAMETER_NAME`的字符串参数，并将其默认值设置为空字符串。
在`Example`阶段中，我们使用Groovy的if-else语句来检查参数是否为空。
如果参数不为空，则将其赋值给`variableA`。
如果参数为空，则将`'default_value'`赋值给`variableB`。最后，我们将变量值打印到控制台。

您可以根据需要修改示例中的变量名和默认值。
