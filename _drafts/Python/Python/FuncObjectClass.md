# 代码如何组织，或面向对象编程

Python 是一种多范式编程语言，这意味着它支持多种编程风格，包括函数式编程和面向对象编程。

这里我们重点讨论面向对象编程。可以参考 `functools` 模块和 `lambda` 表达式，学习更多关于函数式编程的知识。

## 函数

函数是 Python 代码组织的最小单元。和 Java 不同，Python 的函数（或叫方法）可以独立存在，不必放在一个类里。在面向对象编程的语境下，函数单独存在的场景有两种：

一、入口函数即 `main()` 函数。注意，Python 语法本身并没有 `main` 关键字，出于可读性考虑，人为指定为 `main`。

```python
def main():
    pass

if __name__ == '__main__':
    sys.exit(main())
```

二、作为 utils 使用的工具函数，一般放在一个独立文件里。比如下面的 file_utils.py：

```python
#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
A general purpose File implementation.
"""

import os
from io import open
import modules.common.string_utils as string_utils


def find_files(path, suffix):
    files = []
    for root, dirs, filenames in os.walk(path):
        for filename in filenames:
            if string_utils.is_blank(suffix) or filename.endswith(suffix):
                files.append(os.path.join(root, filename))
    return files


def read_file(path):
    with open(path, encoding='utf-8') as myfile:
        # 读取文件内容
        content = myfile.read()
    return content.encode(encoding='utf-8')


def get_real_file_name(file_path):
    if not os.path.isfile(file_path):
        return None
    return os.path.basename(file_path)
```

除此之外，函数都应放在类里。类里的函数和 Java 的方法是相同的概念。

### 函数创建

函数通过 `def` 关键字来定义：

```python
def greet(name="You"): # You 是默认参数，和 Java 不同，Python 支持默认参数。
    return 'Hello {}'.format(name)
```

![](create-function-python.png)

注意，Python 和 Java 不同，不是通过花括号来定义代码块，而是通过冒号和缩进。

### 函数调用

和 Java 一样，通过函数的名字来调用函数，比如：

```python
print(greet("Liu Jianwei"))
```

再次提醒，Python 不需要句末分号。

### 函数入参和出参

一般情况下，Python 函数的入参和出参和 Java 没有什么不同，如果理解 Java 的使用方式，那 Python 也一样，就如上面例子所展示的那样。

需要强调的是，Python 还支持指定名字传参（Keyword Arguments），这时候参数位置就无关紧要了。例如：

```python
def somehow(arg1, arg2):
    print(f"arg1={arg1},arg2={arg2}")

somehow(1,2) # 位置参数传递
somehow(arg2=2, arg1=1) # 名字参数传递
```

上面最后两行调用最终输出一样的结果。如果你接受了 Java 的参数传递方式（Java 是位置传参），名字传参对你来说可能没什么用。但是有一种场景需要稍加注意，即函数在实现的时候，参数个数还不确定或者不可确定。

如果多看些 Python 代码，会发现有这种写法：

```python
def somehow(arg1, arg2, *args, **kwargs):
    print(f"{args}\n")
    
    for key in kwargs:
        print(f"{key}: {kwargs[key]}")  # 本例仅仅打印出所有元素
    #for key, value in kwargs.items():
    #    print(f"{key}: {value}")

somehow(1,2,  3,4,  name="Liu Jianwei",greeting="Hello")
```

`*args`、`**kwargs` 称作**可变参数**，也就是可以传入任意多的参数，`*args` 是可变位置参数，`**kwargs` 是可变名字参数。同样，Python 并没有定义这些关键字，只是约定俗成叫 `args` 和 `kwargs`。当你的函数设计为参数个数不定，可变参数就是很好的实现方式。当这些参数不需要区别对待的时候，`*args` 就够了，当这些参数需要加以区分并针对性处理的时候，`**kwargs` 就派上用场了。

另外，Python 函数不支持重载（Overloading），通过可变参数一定程度可以模拟函数重载。

### 内置函数

请参考：[Python Built-in Functions](https://www.programiz.com/python-programming/methods)。

## 类

### 类创建

### 类实例化

### 函数作为类的方法

和普通函数唯一的不同是，入参列表里第一个参数必须是 `self` 或者其它名字，Python 并没有把 `self` 作为关键字，只是大家约定俗成，如果你愿意可以指定为 `this`，事实上，`self` 的含义 就相当于 Java 里的 `this`。

## 面向对象编程

## 模块/包

