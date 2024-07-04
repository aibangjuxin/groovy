我已经读取了图片中的代码，并整理如下：

```python
#!/usr/bin/python
# -*- coding: UTF-8 -*-

import time
import sys
from google.cloud import bigquery

# Function to create a connection with Google BigQuery
def bq_init_connection():
    return bigquery.Client()

# Function to execute a SQL query and write results to a CSV file
def execute_sql(sql):
    client = bq_init_connection()
    print("start to execute sql-->{}".format(sql))
    bq_job = client.query(sql)  # Execute the SQL query
    time.sleep(3)  # Wait for 3 seconds for the query to execute
    bq_list = list(bq_job.result())  # Convert query results to a list
    print(bq_list)

    # Open data1.csv in append mode and write query results
    with open("data1.csv", "a", encoding='utf-8') as database:
        for row in bq_list:
            # Convert each row to a list of strings
            row_data = [str(value) for value in row.values()]
            # Write the CSV-formatted string to the file
            database.write(",".join(row_data) + "\n")

    return bq_list

if __name__ == '__main__':
    # Check if a SQL query is provided as a command-line argument
    if len(sys.argv) < 2:
        print("Passed parameters: [{}],\nMissing some required parameters!".format(len(sys.argv)))
        sys.exit(1)
    sql = sys.argv[1]
    result = execute_sql(sql)  # Execute the provided SQL query
    print("2222")

    # Open data2.csv in append mode and write query results
    with open("data2.csv", "a", encoding='utf-8') as database2:
        for row in result:
            # Convert each row to a list of strings
            row_data = [str(value) for value in row.values()]
            # Write the CSV-formatted string to the file
            database2.write(",".join(row_data) + "\n")
```

这个Python脚本用于连接到Google BigQuery，执行SQL查询，并将查询结果写入CSV文件。代码的主要功能如下：

1. `bq_init_connection()`：创建与Google BigQuery的连接。
2. `execute_sql(sql)`：执行SQL查询并将结果写入`data1.csv`文件。
3. 在脚本的主函数中，检查是否提供了SQL查询作为命令行参数，执行查询并将结果写入`data2.csv`文件。

如果有其他问题或需要进一步解释，请告诉我！
