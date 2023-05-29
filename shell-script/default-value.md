你可以这么写:

shell
#!/bin/bash

disk_size=${disk_size:-100}

echo $disk_size


这段脚本的逻辑是:

- 如果disk_size没有设置,那么disk_size会默认为100
- 如果你在运行脚本之前设置了disk_size,比如:

shell
disk_size=120
./script.sh 


那么脚本中disk_size会是120

- 无论如何,脚本最后都会打印disk_size的值

所以如果你没有设置disk_size,脚本会打印100;如果你设置成120,脚本会打印120。

这实现了你想要的逻辑 - 如果没有设置disk_size,默认值为100;如果设置了,就使用设置的值。


			
