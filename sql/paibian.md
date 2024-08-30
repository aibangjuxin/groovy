**示例：**

**原始数据：**

| id | name | addresses |
|---|---|---|
| 1 | John Doe | ['123 Main St', 'Apt 456', 'Cityville'] |
| 2 | Jane Smith | ['456 Oak Ave', 'Suite 789'] |

**使用 JSON_TABLE 函数拍平嵌套结构：**

```sql
SELECT
  id,
  name,
  address
FROM
  users,
  JSON_TABLE(
    addresses,
    '$[*]' COLUMNS (
      address VARCHAR(255) PATH '$'
    )
  ) AS addresses;
```

**结果：**

| id | name | address |
|---|---|---|
| 1 | John Doe | 123 Main St |
| 1 | John Doe | Apt 456 |
| 1 | John Doe | Cityville |
| 2 | Jane Smith | 456 Oak Ave |
| 2 | Jane Smith | Suite 789 |

**解释：**

* `JSON_TABLE()` 函数用于将 JSON 数据展开成行。
* `'$[*]'` 表达式表示遍历 `addresses` 字段中的所有元素。
* `COLUMNS()` 子句定义了要提取的列，`address VARCHAR(255) PATH '$'` 表示从 JSON 元素中提取 `address` 字段的值。
* 每个嵌套的地址值都被扩展为单独的行，因此每个用户都有自己的地址列表。

**注意：**

* 确保 `addresses` 字段包含有效的 JSON 数据。
* 该查询仅适用于 MySQL 8.0 及更高版本。
