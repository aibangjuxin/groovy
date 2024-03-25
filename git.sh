#!/bin/bash
os_type=""

# 尝试获取操作系统类型
os_name=$(uname -s)

case $os_name in
Linux)
  os_type="Linux"
  ;;
Darwin)
  os_type="macOS"
  ;;
*)
  # 其他未知系统类型默认为iPhone
  os_type="iPhone"
  ;;
esac

# 输出操作系统类型
echo "OS Type: $os_type"

# Define the directory
dir=$(pwd)

# Check if the directory exists
if [ -d "$dir" ]; then
  cd "$dir"
else
  echo "Directory $dir does not exist."
  exit 1
fi

# Get the current date
riqi=$(date)

# Check if there are any changes
if [ -n "$(git status --porcelain)" ]; then
  # Add all changes
  # Get the latest changed filename
  #filename=$(git diff --name-only HEAD | tail -n 1)

  # 获取文件的完整路径
  #full_path=$(pwd)/$filename

  # 调用替换脚本
  #/Users/lex/shell/replace.sh "$full_path"

  # 获取所有改变的文件列表
changed_files=$(git diff --name-only HEAD)

# 遍历每个改变的文件并调用替换脚本
for filename in $changed_files; do
  # 获取文件的完整路径
  full_path=$(pwd)/$filename
  
  # 调用替换脚本
  /Users/lex/shell/replace.sh "$full_path"
  
  # 检查替换脚本是否执行成功
  if [ $? -eq 0 ]; then
    echo "Replace script executed successfully for $filename."
  else
    echo "Failed to execute replace script for $filename."
    exit 1
  fi
done



  git add .
  if [ $? -eq 0 ]; then
    echo "Changes added successfully."
  else
    echo "Failed to add changes."
    exit 1
  fi

  # Get the latest changed filename
  filename=$(git diff --name-only HEAD | tail -n 1)

  # Define a commit message
  commit_message="This is for my ${os_type} git push or pull at $riqi. Last changed file: $filename"

  # Commit the changes
  git commit -m "$commit_message"
  if [ $? -eq 0 ]; then
    echo "Changes committed successfully."
  else
    echo "Failed to commit changes."
    exit 1
  fi

  # Push the changes
  git push
  if [ $? -eq 0 ]; then
    echo "Changes pushed successfully."
  else
    echo "Failed to push changes."
    exit 1
  fi
else
  echo "No changes to commit."
fi
