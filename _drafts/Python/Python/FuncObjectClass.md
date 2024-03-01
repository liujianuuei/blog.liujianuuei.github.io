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

这种类似 Java 的参数传递方式，称作**位置参数**（Positional Arguments），也就是按位置传参。除此之外，Python 还支持**名字参数**（Keyword Arguments），也就是指定参数名传参，这时候参数位置就无关紧要了。例如：

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

`*args`、`**kwargs` 称作**可变参数**（Variable Length Arguments 或者 Arbitrary Arguments），也就是可以传入任意多的参数，`*args` 是可变位置参数，`**kwargs` 是可变名字参数。同样，Python 并没有定义这些关键字，只是约定俗成叫 `args` 和 `kwargs`。当你的函数设计为参数个数不定，可变参数就是很好的实现方式。当这些参数不需要区别对待的时候，`*args` 就够了，当这些参数需要加以区分并针对性处理的时候，`**kwargs` 就派上用场了。

另外，Python 函数不支持重载（Overloading），通过可变参数一定程度可以模拟函数重载。

### 内置函数

请参考：[Python Built-in Functions](https://www.programiz.com/python-programming/methods)。

## 类

我们先来看对象是什么，对象是一个实体，该实体包含一些属性和行为。类就是对象的定义。

我们先来看一个简单的 Java 的对象或类的例子：

```java
public class Parrot {

    // 属性
    private String name = "";

    public Parrot(String name) {
        this.name = name;
    }

    // 行为
    public void say() {
        System.out.print("Hi I'm " + this.name);
    }
}
```

### 类创建

我们现在把上面这个 Java 对象或类改造成一个 Python 对象或类。Python 没有权限修饰符的概念，也不需要指定属性的类型，和函数一样，不是通过花括号来标识类体，而是通过冒号和缩进。最终看起来是这样：

```python
class Parrot:

    # 属性
    name = ""
    
    def __init__(this, name):
        super(Parrot, this).__init__()
        this.name = name

    # 行为
    def say(this):
        print(f"Hi I'm {this.name}")
```

我们可以看到有几点不同：

一、`__init__` 是 Python 的构造器，而不是像 Java 类名作为构造器。

二、`super()` 必须显式调用，而在 Java 里无参构造器是默认调用。

三、`this` 必须作为形参（Parameters），而在 Java 里不需要，Java 里是系统级的关键字。

### 类实例化

### 函数作为类的方法

和普通函数唯一的不同是，入参列表里第一个参数必须是 `self` 或者其它名字，Python 并没有把 `self` 作为关键字，只是大家约定俗成，如果你愿意可以指定为 `this`，事实上，`self` 的含义 就相当于 Java 里的 `this`。

### 非常重要的 `self`

## 模块与包

在 Python 中，模块（Modules）的概念是指独立的以 `.py` 结尾的文件。一个文件就可以是一个模块，用来组织代码。

在面向对象编程语境下，最好同样保持 Java 的好习惯，即一个文件只存放一个类。这样，模块的概念就更多的与 Java 的以 `.java` 结尾的类文件对应。

在 Python 中，包（Package）的概念非常类似 Java 的包的概念，也是指组织多个 `.py` 文件的一个文件目录，该文件目录必须包含一个名字为 `__init__.py` 的空文件，该文件告诉 Python 该文件目录是 Python 包，而不是普通的文件目录。

### 模块使用

通过 `import` 关键字，导入模块来使用。例如：

```python
import math as math  # 推荐；第二个 math 是别名
import modules.http.http_client as http  # 推荐；http 是别名
from modules.http import http_client as http  # 推荐；http 是别名
```

然后，通过模块名来调用。例如：

```python
print(math.pi)
print(http.HttpClient())
```

如果不想通过模块名调用，想直接使用模块里具体定义，就像使用本地函数一样，可以使用如下的导入方式：

```python
from math import pi  # 推荐
from modules.http.http_client import *  # 不推荐
```

然后，可以直接使用。例如：

```python
print(pi)
print(HttpClient())
```

建议保留模块名的方式，这样更清晰避免混淆，可以区分某个定义是本地的还是属于某个外部模块，这也释放了本地命名空间。如果确实不想要模块名，也不要采用 `from-import *` 的方式，把外部模块里的所有定义全部导入当前模块，这不是一个好的编程习惯。

### 内置模块

请参考：[Python Module Index](https://docs.python.org/3/py-modindex.html)。

## 面向对象编程

Python 面向对象编程的思路和 Java 面向对象编程的思路是一致的。

### 继承

### 封装

### 多态

