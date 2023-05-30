你可以这么写: shell
```bash
#!/bin/bash

disk_size=${disk_size:-100}

echo $disk_size
```

这段脚本的逻辑是:

- 如果disk_size没有设置,那么disk_size会默认为100
- 如果你在运行脚本之前设置了disk_size,比如:

shell
disk_size=120
./script.sh 


那么脚本中disk_size会是120

- 无论如何,脚本最后都会打印disk_size的值

所以如果你没有设置disk_size,脚本会打印100;如果你设置成120,脚本会打印120。

这实现了你想要的逻辑 
- 如果没有设置disk_size,默认值为100;
- 如果设置了,就使用设置的值。

这里是一些 Shell 变量赋值的技巧:

## 参数替换 - 给没有设置的变量一个默认值

shell
PARAM=${PARAM:-default value}


这会给没有设置的PARAM变量一个默认值default value。

## 参数替换 - 设置可选参数

shell
PARAM=${PARAM:+"value"}


这会检查PARAM是否设置,如果设置了则使用value,如果没有设置则为空。这适用于可选参数。

## 参数替换 - 设置必选参数  

shell 
PARAM=${PARAM:?"error message"}


这会检查PARAM是否设置,如果没有设置则打印错误信息error message并退出脚本。这用于确保必选参数被设置。

## 从文件读取值

shell
VALUE=$(cat file.txt)


这会把file.txt文件的内容读取到VALUE变量。

## 命令替换 - 从命令输出读取值

shell
VALUE=$(ls -l | grep "foo")


这会运行ls -l | grep "foo"命令,并把输出保存到VALUE变量。

## 数组赋值

shell
arr=(a b c d)


这会创建一个数组arr并赋值多个值。

## 只读变量

shell
readonly VARIABLE=value


这会创建一个只读变量VARIABLE,VALUE不能再被改变。

## 给变量加引号

shell
MESSAGE="Hello world!" 


这会创建一个MESSAGE变量,并且加上双引号。单引号和双引号有不同的意义。

Hope this helps! Let me know if you have any other questions.

当在shell脚本中定义变量时，有许多技巧可以使您的代码更加优雅和灵活。下面是一些常用的技巧和示例：

## 给变量设置默认值

您可以使用`${VARNAME:-DEFAULT}`语法为变量设置默认值。如果变量`VARNAME`未定义或为空，则使用`DEFAULT`作为默认值。例如：

```bash
#!/bin/bash

MY_VAR=${1:-default_value}

echo $MY_VAR
```

在运行该脚本时，如果未传递任何参数，则`MY_VAR`将被设置为`default_value`。

## 使用命令输出作为变量值

您可以使用反引号或 `$()` 来将命令的输出分配给变量。例如：

```bash
#!/bin/bash

CURRENT_TIME=`date`
echo "Current time: $CURRENT_TIME"

UPTIME=$(uptime)
echo "Uptime: $UPTIME"
```

## 合并变量值

您可以使用`${VARNAME1}${VARNAME2}`语法将两个变量的值合并为一个。例如：

```bash
#!/bin/bash

FIRST_NAME="John"
LAST_NAME="Doe"

FULL_NAME="${FIRST_NAME} ${LAST_NAME}"
echo "Full name: $FULL_NAME"
```

## 将命令输出分配给多个变量

您可以使用`read`命令将命令输出分配给多个变量。例如：

```bash
#!/bin/bash

read CPU RAM <<< $(top -n1 | awk '/^%Cpu/{print $2} /^KiB Mem/{print $3}')
echo "CPU usage: $CPU"
echo "RAM usage: $RAM"
```

在上面的示例中，`top`命令的输出被`awk`处理后，使用`read`命令分配给`CPU`和`RAM`两个变量。

## 使用数组

您可以使用数组来存储多个值并在需要时访问它们。例如：

```bash
#!/bin/bash

FRUITS=("apple" "banana" "orange")

echo "First fruit: ${FRUITS[0]}"
echo "All fruits: ${FRUITS[@]}"
echo "Number of fruits: ${#FRUITS[@]}"
```

在上面的示例中，`FRUITS`数组包含三个字符串元素。`${FRUITS[0]}`用于访问第一个元素，`${FRUITS[@]}`用于访问所有元素，并且`${#FRUITS[@]}`用于获取数组元素的数量。

## 使用关联数组

关联数组允许您将键值对存储为元素。例如：

```bash
#!/bin/bash

declare -A COLORS
COLORS["red"]="#FF0000"
COLORS["green"]="#00FF00"
COLORS["blue"]="#0000FF"

echo "Red color code: ${COLORS["red"]}"
echo "All colors: ${COLORS[@]}"
```

在上面的示例中，`COLORS`是一个关联数组，其中每个键都与一个颜色代码相关联。`${COLORS["red"]}`用于访问与`red`键相关联的值，并且`${COLORS[@]}`用于访问所有键值对。

## 使用环境变量

您可以使用环境变量来在不同的shell脚本之间共享变量。要将变量设置为环境变量，请使用`export`命令。例如：

```bash
#!/bin/bash

export MY_VAR="Hello, world!"
./another_script.sh
```

在上面的示例中，`MY_VAR`变量被设置为`Hello, world!`并导出为环境变量。在运行`another_script.sh`时，该变量也将可用。

希望这些技巧可以帮助您更好地处理和管理shell脚本中的变量。
			
