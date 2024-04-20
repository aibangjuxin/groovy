`jq` is a powerful command-line JSON processor that allows you to parse, filter, and manipulate JSON data. Here are some examples to get you started:

**Example 1: Filtering JSON data**

Suppose we have a JSON file `data.json` with the following content:
```json
[
  {
    "name": "John",
    "age": 30,
    "city": "New York"
  },
  {
    "name": "Jane",
    "age": 25,
    "city": "London"
  },
  {
    "name": "Bob",
    "age": 40,
    "city": "Paris"
  }
]
```
We can use `jq` to filter out only the objects with `age` greater than 30:
```bash
jq '.[] | select(.age > 30)' data.json
```
Output:
```json
{
  "name": "Bob",
  "age": 40,
  "city": "Paris"
}
```
**Example 2: Extracting specific fields**

Suppose we want to extract only the `name` and `city` fields from the JSON data:
```bash
jq '.[] | {name, city}' data.json
```
Output:
```json
{
  "name": "John",
  "city": "New York"
}
{
  "name": "Jane",
  "city": "London"
}
{
  "name": "Bob",
  "city": "Paris"
}
```
**Example 3: Grouping data**

Suppose we want to group the data by `city`:
```bash
jq '.[] | {city} + {people: [.name]}' data.json | jq -s 'group_by(.city)'
```
Output:
```json
[
  {
    "city": "London",
    "people": ["Jane"]
  },
  {
    "city": "New York",
    "people": ["John"]
  },
  {
    "city": "Paris",
    "people": ["Bob"]
  }
]
```
**Outputting Markdown format**

To output Markdown format, you can use the `-r` option to output raw strings, and then use Markdown syntax to format the output. Here's an example:
```bash
jq -r '.[] | "### " +.name + "\n* Age: " +.age + "\n* City: " +.city' data.json
```
Output:
```
### John
* Age: 30
* City: New York
### Jane
* Age: 25
* City: London
### Bob
* Age: 40
* City: Paris
```
**More advanced examples**

Here are a few more advanced examples to get you started:

* **Filtering with multiple conditions**: `jq '.[] | select(.age > 30 and.city == "Paris")' data.json`
* **Sorting data**: `jq '.[] | sort_by(.age)' data.json`
* **Aggregating data**: `jq '.[] | {sum_age:.age} | add' data.json`
* **Merging data**: `jq '.[] + {country: "USA"}' data.json`

These are just a few examples of what you can do with `jq`. For more information, I recommend checking out the `jq` manual and the official documentation.

I hope this helps! Let me know if you have any more questions.



**jq: A Lightweight and Flexible JSON Processor**

jq is a lightweight and flexible command-line JSON processor that allows you to parse, transform, and manipulate JSON data. It's a powerful tool for working with JSON data, and it's widely used in the Linux and Unix communities.

**Example 1: Filtering JSON Data**

Suppose we have a JSON file named `data.json` containing the following data:
```json
[
  {
    "name": "John",
    "age": 30,
    "city": "New York"
  },
  {
    "name": "Jane",
    "age": 25,
    "city": "London"
  },
  {
    "name": "Bob",
    "age": 40,
    "city": "Paris"
  }
]
```
To filter this data to only include people who are 30 years old or older, we can use the following jq command:
```bash
jq '.[] | select(.age >= 30) | .name' data.json
```
This will output:
```
"John"
"Bob"
```
**Explanation:**

* `.[]` selects each element in the array.
* `select(.age >= 30)` filters the elements to only include those where the `age` is 30 or older.
* `.name` extracts the `name` field from each filtered element.

**Example 2: Extracting Specific Fields**

Suppose we want to extract only the `name` and `city` fields from the same JSON data. We can use the following jq command:
```bash
jq -r '.[] | {name, city}' data.json
```
This will output:
```
{
  "name": "John",
  "city": "New York"
}
{
  "name": "Jane",
  "city": "London"
}
{
  "name": "Bob",
  "city": "Paris"
}
```
**Explanation:**

* `.[]` selects each element in the array.
* `{name, city}` creates a new object with only the `name` and `city` fields.

**Example 3: Merging Arrays**

Suppose we have two JSON files, `data1.json` and `data2.json`, containing the following data:
```json
// data1.json
[
  {
    "name": "John",
    "age": 30
  },
  {
    "name": "Jane",
    "age": 25
  }
]

// data2.json
[
  {
    "name": "Bob",
    "age": 40
  },
  {
    "name": "Alice",
    "age": 35
  }
]
```
To merge these two arrays into a single array, we can use the following jq command:
```bash
jq -s '.[]' data1.json data2.json
```
This will output:
```
[
  {
    "name": "John",
    "age": 30
  },
  {
    "name": "Jane",
    "age": 25
  },
  {
    "name": "Bob",
    "age": 40
  },
  {
    "name": "Alice",
    "age": 35
  }
]
```
**Explanation:**

* `-s` tells jq to concatenate the input files.
* `.[]` selects each element in the concatenated array.

**Outputting Markdown Format**

To output the filtered data in Markdown format, we can use the following jq command:
```bash
jq -r '.[] | "### \(.name)"' data.json
```
This will output:
```
### John
### Jane
### Bob
```
**Explanation:**

* `.[]` selects each element in the array.
* `"### \(.name)"` creates a Markdown heading with the `name` field.

I hope these examples help you get started with using jq to filter and manipulate JSON data!

https://hf.co/chat/r/xTZajsN
