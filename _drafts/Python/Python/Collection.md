# 集合框架

和 Java 一致，在 Python 编程语言中，集合类可以分成两类，一类是 Collection，一类是 Mapping。Collection 又分为 Set、Sequence/List 等。

## 概览

|Data Type|Classes |Description|
|---------|------------|---------------------------|
|Set  |set, frozenset        |    无序且不重         |
|Sequence  |list, tuple      |  有序                |
|Mapping  |dict          |      Key-Value           |

## Set

### 创建

通过"花括号和逗号分隔符"来创建 Set。例如：

```python
somehow = {19, 26, 29}
somehow = set()
somehow = {'Jack', 32, 'Computer Science'}
```

### 访问元素

不适用。

### 添加元素

通过 `add(value)` 给一个 Set 添加元素。

通过 `update(values)` 给一个 Set 添加多个元素。例如：

```python
companies = {'Lacoste', 'Ralph Lauren'}
tech_companies = ['apple', 'google', 'apple']

# using update() method
companies.update(tech_companies)

print(companies)  # Output: {'google', 'apple', 'Lacoste', 'Ralph Lauren'}
```

### 修改元素

不适用。

### 删除元素

通过 `discard(value)`、`remove(value)`、`clear()` 删除元素。例如：

```python
languages = {'Swift', 'Java', 'Python'}

print('Initial Set:',languages)

# remove 'Java' from a set
removedValue = languages.discard('Java')

print('Set after remove():', languages)  # Output: Set after remove(): {'Python', 'Swift'}
```

### 遍历元素

通过 `len()` 得到一个 Set 的长度。

通过 for-loop 遍历一个 Set。例如：

```python
fruits = {'apple', 'banana', 'orange'}

# iterate through the set
for fruit in fruits:
    print(fruit)
```

Set 的其它有用方法，请参考 [Other Python Set Methods](https://www.programiz.com/python-programming/set)。

## List

### 创建

通过"中括号和逗号分隔符"来创建 List。例如：

```python
somehow = [19, 26, 29]
somehow = []
somehow = ['Jack', 32, 'Computer Science']
```

另外，Python 还支持通过"列表推导"创建 List，请参考 [Python List Comprehension](https://www.programiz.com/python-programming/list-comprehension)，很少用到，不详述。

### 访问元素

通过下标访问元素。例如：

```python
somehow[0]
somehow[1:] # list slicing
```

注：list slicing 的语法格式是 [start:stop:step]。另外，Python 还支持"负下标"，也就是反向索引，但一般用的很少，不详述。

### 添加元素

通过 `append(value)` 给一个 List 添加（追加到末尾）元素。

通过 `insert(index,value)` 给一个 List 添加元素到指定位置。

通过 `extend(values)` 给一个 List 添加多个元素。例如：

```python
odd_numbers = [1, 3, 5]
print('Odd Numbers:', odd_numbers)

even_numbers  = [2, 4, 6]
print('Even Numbers:', even_numbers)

# adding elements of one list to another
odd_numbers.extend(even_numbers)

print('Numbers:', odd_numbers)  # Output: Numbers: [1, 3, 5, 2, 4, 6]
```

### 修改元素

通过 `=` 重新赋值，来修改元素。例如：

```python
colors = ['Red', 'Black', 'Green']
print('Original List:', colors)

# changing the third item to 'Blue'
colors[2] = 'Blue'

print('Updated List:', colors)  # Output: Updated List: ['Red', 'Black', 'Blue']
```

### 删除元素

通过 `remove(value)`、`pop(index)`、`clear()` 删除元素。例如：

```python
numbers = [2,4,4,7,9]

# remove 4 from the list
numbers.remove(4)

print(numbers)  # Output: [2, 4, 7, 9]
```

注意，`remove(value)` 删除第一个按值匹配到的元素。还有 `del` 相关的删除用法，不详述。

### 遍历元素

通过 `len()` 得到一个 List 的长度。

通过 for-loop 遍历一个 List。例如：

```python
fruits = ['apple', 'banana', 'orange']

# iterate through the list
for fruit in fruits:
    print(fruit)
```

List 的其它有用方法，请参考 [Python List Methods](https://www.programiz.com/python-programming/list)。

## Dict

### 创建

通过"中括号和逗号分隔符"来创建 Dict。例如：

```python
somehow = {"k1":19, "k2":26, "k3":29}
somehow = {}
somehow = {"k1":"Jack", "k2":32, "k3":"Computer Science"}
```

另外，Python 还支持通过"字典推导"创建 Dict，请参考 [Python Dict Comprehension](https://www.programiz.com/python-programming/dict-comprehension)，很少用到，不详述。

### 访问元素

通过 Key 访问元素。例如：

```python
somehow["k1"] # key 不存在，抛错
somehow.get("k1") # key 不存在，不抛错，返回 None
```

### 添加元素

通过 `=` 给一个 Dict 添加元素（添加一个新的 Key-Value 对）。例如：

```python
country_capitals = {
  "Germany": "Berlin", 
  "Canada": "Ottawa", 
}

# add an item with "Italy" as key and "Rome" as its value
country_capitals["Italy"] = "Rome"

print(country_capitals)  # Output: {'Germany': 'Berlin', 'Canada': 'Ottawa', 'Italy': 'Rome'}
```

通过 `update(key-value-pairs)` 给一个 Dict 添加多个元素。例如：

```python
marks = {'Physics':67, 'Maths':87}
internal_marks = {'Practical':48}

marks.update(internal_marks)

print(marks)  # Output: {'Physics': 67, 'Maths': 87, 'Practical': 48}
```

### 修改元素

通过 `=` 重新赋值，来修改元素。例如：

```python
country_capitals = {
  "Germany": "Berlin", 
  "Italy": "Naples", 
  "England": "London"
}

# change the value of "Italy" key to "Rome"
country_capitals["Italy"] = "Rome"

print(country_capitals)  # Output: {'Germany': 'Berlin', 'Italy': 'Rome', 'England': 'London'}
```

### 删除元素

通过 `pop(key)`、`clear()` 删除元素。例如：

```python
# random sales dictionary
sales = { 'apple': 2, 'orange': 3, 'grapes': 4 }

element = sales.pop('apple')

print('The popped element is:', element)  # Output: The popped element is: 2
print('The dictionary is:', sales)  # Output: The dictionary is: {'orange': 3, 'grapes': 4}

```

### 遍历元素

通过 `len()` 得到一个 Dict 的长度。

通过 for-loop 遍历一个 Dict。例如：

```python
country_capitals = {
  "United States": "Washington D.C.", 
  "Italy": "Rome" 
}

# print dictionary keys one by one
for country in country_capitals:
    print(country)  # Output: United States \n Italy

print()

# print dictionary values one by one
for country in country_capitals:
    capital = country_capitals[country]
    print(capital)  # Output: Washington D.C. \n Rome
```

Dict 的其它有用方法，请参考 [Python Dictionary Methods](https://www.programiz.com/python-programming/dictionary)。
