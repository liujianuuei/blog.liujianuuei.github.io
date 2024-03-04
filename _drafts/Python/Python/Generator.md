# 生成器

先来看一段代码：

```python
def some_generator_function(n):
    value = 0
    while value < n:
        # produce the current value of the counter
        yield value
        value += 1

# iterate over the generator object produced by my_generator
for value in some_generator_function(3):
    # print each value produced by generator
    print(value)
```

生成器（Generators）的定义和普通函数类似，关键不同在于没有 `return` 语句，而是用 `yield` 返回值。

`yield` 的含义包含两层意思：

一、产生一个要返回的值，并返回该值；

二、暂停函数执行，直到需要下一个值的时候。

更多请参考 [Python Generators](https://www.programiz.com/python-programming/generator)。