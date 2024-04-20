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