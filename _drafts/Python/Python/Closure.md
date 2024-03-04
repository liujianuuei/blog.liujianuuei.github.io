# 闭包

先来看一段代码：

```python
def greet():
    # variable defined outside the inner function
    name = "John"
    # return a nested anonymous function
    return lambda: "Hi " + name

# call the outer function
message = greet()

# call the inner function
print(message())  # Output: Hi John
```

闭包（Closures）更多的应用于函数式编程范式中，用于函数式编程中的数据封装。

闭包是指一个函数内部的函数（或称为内嵌函数），其可以引用外部函数的变量，并且即使外部函数已经执行完毕，内部函数仍然可以访问并操作这些变量。

更多请参考 [Python Closures](https://www.programiz.com/python-programming/closure)。
