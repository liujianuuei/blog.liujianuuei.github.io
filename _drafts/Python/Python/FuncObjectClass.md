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

我们先来看一个简单的 Java 的对象或类（类是对象的说明或定义）的例子：

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

Python 和 Java 一样都是通过关键字 `class` 定义类。我们可以看到有几点不同：

一、`__init__()` 是 Python 的构造器，而不是像 Java 类名作为构造器。

二、`super()` 必须显式调用，而在 Java 里无参构造器是默认调用。注意写法 `super(本类名, this).__init__()`。如果调用有参构造器，参数通过 `super()` 后面的 `__init__(这里给父类传入参数)` 传入。

三、`this` 必须作为形参（Parameters），而在 Java 里不需要，Java 里是关键字。

Python 是动态类型语言，因此属性可以不用事先声明，可以在构造器（再次注意 Python 的构造器是 `__init__` 方法）里直接初始化。因此上面的类创建代码可以简化为：

```python
class Parrot:

    def __init__(this, name):
        super(Parrot, this).__init__()  # 调用父类构造器
        this.name = name  # 属性初始化

    # 行为
    def say(this):
        print(f"Hi I'm {this.name}")
```

### 类实例化

和 Java 不同，Python 创建对象即实例化类，不需要 `new` 关键字。例如：

```python
parrot1 = Parrot("Polly")
```

### 函数作为类的方法

作为类的函数，也叫做方法，作为类的变量也叫做属性。

和普通函数唯一的不同是，入参列表里第一个参数必须是 `this` 或者其它名字，Python 并没有把 `this` 作为关键字，相反大家约定俗成用 `self`（后续我们都按照 Python 的惯例，采用 `self`）。但如果你愿意可以指定为 `this`，事实上，`self` 的含义 就相当于 Java 里的 `this`。

#### 方法使用

和 Java 一样，通过 `.` 访问对象的方法。

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

另外，本质上 `import` 和 `from-import` 没有区别，只是写法不同。

### 内置模块

请参考：[Python Module Index](https://docs.python.org/3/py-modindex.html)。

## 面向对象编程

Python 面向对象编程的思路和 Java 面向对象编程的思路是一致的。

### 继承

Python 的继承（Inheritance）和 Java 在概念上没有什么不同。

```python
class Bird:

    def __init__(self, name):
        super(Parrot, self).__init__()  # 调用父类构造器
        self.name = name  # 父类属性初始化

    # 父类行为
    def say(self):
        print(f"Hi I'm {self.name}")


# Parrot 类继承自 Bird 类，Parrot 叫做子类，Bird 叫做父类。
class Parrot(Bird):
    def __init__(self, name):
            super(Parrot, self).__init__(name)  # 调用父类构造器

    # 子类行为
    def sing(self):
        print(f"Hi I'm singing")
```

属性 `name` 和方法 `say()` 被子类继承，子类现在拥有 `name`、`say()`、`sing()` 三个属性或方法。

#### 方法重写

继承的威力就在于方法重写，也就是针对同一个方法，子类可以根据需要修改其行为。

和 Java 一样，Python 也支持**方法重写**（Method Overriding）。

```python
class Parrot(Bird):
    def __init__(self, name):
            super(Parrot, self).__init__(name)  # 调用父类构造器

    # 子类重写的行为
    def say(self):
        print(f"Hi I'm is {self.name}. How are you!")

    # 子类原生行为
    def sing(self):
        print(f"Hi I'm singing")
```

在子类的重写的方法里，如果需要访问父类的同名方法，可以通过 `super()` 函数访问，注意比 Java 多一对括号。

#### 不可重写的方法——私有方法

可以通过在方法名前加两个下划线 `__` 的语法，告诉 Python 这个方法是**私有方法**，也就是外部不可见，也就不能被继承和重写。

根据面向对象编程原则，所有属性都应该对外不可见。所以类的属性，都应该在定义的时候，名字前面加双下划线。

#### 多继承

Java 是单继承，Python 支持多继承，也就是继承多个父类。该篇文章不推荐多继承，因为多继承会破坏对象或实体的逻辑性，且基于这样的哲学思考：任何事物在一个单一语境下必然是且仅是一个事物。

如果想了解关于多继承的知识，可以参考 [Python Multiple Inheritance](https://www.programiz.com/python-programming/multiple-inheritance)，这里不详述。

### 封装

封装（Encapsulation）是面向对象编程思想的核心特性。应该坚持一条这样的原则：尽可能地封装，任何信息（属性、方法等）能不对外暴露就不对外暴露。

Python 没有 类似 Java 的 `private` 关键字，而是通过两个下划线 `__` 来表示私有性。如上我们讨论的私有方法就属此例。理论上，所有的类属性都应该加双下划线，让其变成对外不可见。

### 多态

多态（Polymorphism）也是面向对象编程思想的重要特性。多态的一个主要应用就是，可以不管具体的对象，而统一调用相同的方法。

因为 Python 不像 Java 需要提前声明类型，多态在 Python 中的应用，就更加简单了。只要两个类——即使没有继承关系——有相同签名的方法，就可以利用多态特性，达到统一调用的效果。当然，实际应用中，更多的是基于继承的多态应用，因为继承天然的包含相同签名的方法。

```python
@engine.setter
def engine(self, engine):
    self.__engine_client = engine

self.__engine_client.execute(...)
```