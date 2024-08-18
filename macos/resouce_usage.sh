#!/bin/bash

# 获取CPU使用率最高的进程
echo "CPU使用率最高的进程:"
ps -Ao %cpu,comm | sort -nr | head -n 5 | while read cpu comm; do
  app=$(echo "$comm" | sed 's/\.app.*/.app/')
  echo "$cpu% - $app"
done

echo

# 获取内存使用最多的进程
echo "内存使用最多的进程:"
ps -Ao rss,comm | sort -nr | head -n 5 | awk '{print $1/1024 "MB - " $2}' | while read mem comm; do
  app=$(echo "$comm" | sed 's/\.app.*/.app/')
  printf "%.2f MB - %s\n" $mem $app
done

echo

# 按应用统计CPU使用率
echo "按应用统计的CPU使用率:"
ps -Ao %cpu,comm | awk '{app = $2; sub(/\.app.*/, ".app", app); cpu[$2] += $1} END {for (a in cpu) print cpu[a] "% - " a}' | sort -nr | head -n 5

echo

# 按应用统计内存使用
echo "按应用统计的内存使用:"
ps -Ao rss,comm | awk '{app = $2; sub(/\.app.*/, ".app", app); mem[$2] += $1} END {for (a in mem) printf "%.2f MB - %s\n", mem[a]/1024, a}' | sort -nr | head -n 5
