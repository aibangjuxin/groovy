Google BigQuery 是一个强大的数据分析服务，它允许用户通过 SQL 查询语言对存储在 BigQuery 中的大型数据集进行分析。BigQuery 支持两种 SQL 方言：标准 SQL 和旧式 SQL。标准 SQL 是查询 BigQuery 中存储的数据的首选，因为它符合 ANSI SQL 2011 标准。

以下是一些在 Google BigQuery 中插入数据和执行常用操作的命令：

### 插入数据到表中

要将数据插入到 BigQuery 表中，你可以使用 `INSERT INTO` 语句。以下是一个示例，它将数据插入到名为 `MyDataset.MyTable` 的表中：

```sql
INSERT INTO `MyDataset.MyTable` (column1, column2, column3)
VALUES ('value1', 'value2', 'value3'),
       ('value4', 'value5', 'value6');
```

如果你想要从查询结果中插入数据到另一个表，可以使用 `SELECT` 语句与 `INSERT` 语句结合：

```sql
INSERT INTO `MyDataset.MyTableB` (columnA, columnB)
SELECT column1, column2
FROM `MyDataset.MyTableA`
WHERE condition;
```

### 从表中查询数据

查询表中的数据可以使用 `SELECT` 语句。以下是一个简单的查询示例：

```sql
SELECT column1, column2
FROM `MyDataset.MyTable`
WHERE condition;
```

### 创建新表

使用 `CREATE TABLE` 语句可以创建新表：

```sql
CREATE TABLE `MyDataset.NewTable` (
  column1 datatype,
  column2 datatype,
  column3 datatype
);
```

### 删除表中的数据

使用 `DELETE` 语句可以从表中删除数据：

```sql
DELETE FROM `MyDataset.MyTable`
WHERE condition;
```

### 更新表中的数据

使用 `UPDATE` 语句可以更新表中的数据：

```sql
UPDATE `MyDataset.MyTable`
SET column1 = 'new_value'
WHERE condition;
```

### 复制表

BigQuery 没有直接的复制表的 SQL 命令，但你可以使用 `SELECT ... INTO` 语句来创建一个新表，该表包含另一个表的数据：

```sql
SELECT *
INTO `MyDataset.CopyOfMyTable`
FROM `MyDataset.MyTable`;
```

### 导出数据

BigQuery 允许你将数据导出到 Google Cloud Storage。你可以使用 BigQuery UI 或者 `bq` 命令行工具来执行导出操作。

### 导入数据

你可以使用 `bq` 命令行工具来导入数据到 BigQuery。以下是一个导入 CSV 文件的示例：

```bash
bq --location=US load \
--source_format=CSV \
--skip_leading_rows=1 \
MyProject:MyDataset.MyTable \
gs://MyBucket/MyFile.csv
```

在这个命令中，`--location` 指定了 BigQuery 集群的位置，`--source_format` 指定了文件格式，`--skip_leading_rows` 用于跳过 CSV 文件中的行（通常用于跳过标题行），`MyProject:MyDataset.MyTable` 是目标表的完整 ID，而 `gs://MyBucket/MyFile.csv` 是 Google Cloud Storage 中的源文件路径。

请注意，上述命令和操作可能需要相应的权限和配置。确保你的 Google Cloud 项目已经设置好，并且你有足够的权限来执行这些操作。此外，对于大型数据集或复杂的操作，建议使用 BigQuery 的官方文档和工具来获取更详细的指导。