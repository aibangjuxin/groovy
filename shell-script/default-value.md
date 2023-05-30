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


			
