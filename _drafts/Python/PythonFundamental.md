# Python 基础

## 关键字

主要介绍常用到的关键字：

+ **True** - 逻辑真。
+ **False** - 逻辑假。
+ **None** - 表示空，类似 Java 的 `null`。
+ **object** - 表示对象。
+ **self** - 类似 Java 的 `this`。
+ **def** - 用来定义函数。

Python 的这些基本关键字和 Java 不太一样，可以使用 [snowpaw](https://pypi.org/project/snowpaw) 来近似达到和 Java 一样的效果，比如 `true`、`false`、`null` 等的支持。

## 注释

`#` 单行注释

`"""`

多行注释

多行注释

多行注释

`"""`

注意，Python 在语法层面，只有单行注释，多行注释其实是借用"多行字符串"来模拟注释的效果。

## 常量

Python 没有类似 Java 的 `final` 或 `static` 机制，所以常量的实现更多是一种**约定**。可以创建一个以`constants.py`作为名字的文件（名字暗含着存放的是常量），在这个文件里定义需要用到的常量。比如：`PI=3.14`。

## 基本数据类型

Python 是一种**强类型**语言，同时也是**动态类型**语言。动态类型语言意味着变量的类型是在运行时确定的，也就是按照**实际的值**来确定类型，在变量声明的时候，类型不需要提前指定，这个特性和 Java 有根本的不同。

|Data Type|Classes |Description|
|---------|------------|---------------------------|
|boolean  |bool        |存 `True` 或者 `False`      |
|numeric  |int/整型、float/浮点型  |                |
|string   |str         |                           |

更多关于集合类型的内容，请查看[集合框架](Collection.md)。

可以通过 `type()` 函数来检查变量或值的数据类型；可以通过 `int()`、`float()`、`str()` 等函数进行显式的类型转换。

## 进制

Python 支持二进制、八进制、十进制和十六机制的数值表示：

+ 二进制：数字前加`0b`前缀，如`0b101`。
+ 八进制：数字前加`0o`前缀，如`0107`。
+ 十进制：数字前不加前缀，如`109`。
+ 十六机制：数字前加`0x`前缀，如`0x10F`。

## 运算符

主要介绍常用到的运算符：

+ 逻辑运算符：`and`、`or`、`not`
+ 比较运算符：`==`、`!=`、`>`、`<`、`>=`、`<=`~~、`is`、`is not`~~
+ 集合运算符：`in`、`not in`
+ 算术运算符：`+`、`-`、`*`、`/`、`//`、`%`、`**`
+ 赋值运算符：`=`、`+=`、`-=`、`*=`、`/=`、`%=`、`**=`
+ 位运算符：`&`、`|`、`~`、`^`、`>>`、`<<`

需要特别说明的是，和 Java 相反，判断"两个值是否相等"即内容是否相等（Java 通过重写 `equals` 来实现，更多内容请参考[对象比较](../JavaSE/Java/ObjectComparison.md)），在 Python 里直接用 `==` 来判断，而类似 Java 的 `==` 运算符的效果（即内存地址相同的同一个对象），在 Python 里则是通过 `is`、`is not` 来实现。

Python 语法建议当和 `True`、`False`、`None` 等单例对象进行比较的时候——虽然我们肯定永远是想做值比较——要用 `is`、`is not` 运算符（具体提示信息如下），这篇文章认为大可不必。采用和一般值比较不一致的做法，除了带来一些微乎其微的心理作用般的性能提升之外，没有任何好处，还会带来额外的记忆成本。也就是说，忘掉 `is`、`is not` 就好了。

> PEP 8: comparison to True should be 'if cond is True:' or 'if cond:'

另外注意，逻辑运算和 Java 的逻辑运算符 `&&`、`||`、`!` 字面表示也不同。

运算符还可以重载，一般用不到，更多内容参考[运算符重载](https://www.programiz.com/python-programming/operator-overloading)。

## 流程控制

Python 通过 `if`、`elif`、`else` 来进行条件控制。举例如下：

```python
grade = 40

if grade < 0:
  result = 'error'
elif grade >= 60:
  result = 'pass' 
else:
  result = 'fail'

print(result)
```

Python 语法不支持三元运算符，但可以通过 `if`、`else` 来模拟类似 Java 三元运算符的效果，这样可以使代码更清晰。

```python
grade = 40

result = 'pass' if grade >= 60 else 'fail'

print(result)
```

Python 通过 `for`、`while` 来进行循环控制。举例如下：

```python
languages = ['Swift', 'Python', 'Go']

for i in languages:
    print(i)
```

需要注意，类似 Java 的 `for(int i=0; i<100; i++)` 这种指定次数的循环控制，在 Python 里，可以通过 `range` 函数实现。

```python
# range(100) returns a sequence of 0, 1, 2, till 99.
for i in range(100):
    print(i)
```

```python
while True:
    user_input = input('Enter your name: ')

    # terminate the loop when user enters end
    if user_input == 'end':
        print(f'The loop is ended')
        break
        
    # terminate the loop when user enters end
    if user_input == 'skip':
        print(f'The iteration is skipped')
        continue

    print(f'Hi {user_input}')
```

## 最佳实践

一、Python 文件开头指定执行程序以及编码类型。

```python
#!/usr/bin/env python
# -*- coding: utf-8 -*-
```

二、使用双引号而不是单引号，这样和 Java 保持一致，降低记忆成本。

三、带括号而不是不带括号，这样统一按函数看待，减少不必要的概念。比如下面的例子：

```python
print('print something') # 而不是 print 'print something'
```

四、不要句末分号，和 Server-side JavaScript 保持一致，降低记忆成本。
