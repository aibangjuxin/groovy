# time count
## script 2 function
```bash
#!/usr/local/bin/bash

# Function to record the script execution start time
start_script_execution() {
  script_start_time=$(date +%s)
}

# Function to record the script execution end time and display total time
end_script_execution() {
  script_end_time=$(date +%s)
  local execution_time=$((script_end_time - script_start_time))
  echo "Script execution time: $execution_time seconds"
}

# Call start_script_execution at the beginning of your script
start_script_execution

# Your script logic goes here
echo "Begin my script"
sleep 5
echo "Done"

# Call end_script_execution at any point to display the total execution time
end_script_execution
```




## script 2

```bash

# 记录开始时间
start_time=$(date +%s)
# 记录结束时间
end_time=$(date +%s)

# 计算脚本执行时间
execution_time=$((end_time - start_time))
echo "脚本执行时间: $execution_time 秒"
```
