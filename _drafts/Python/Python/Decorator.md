# 装饰器

先来看一段代码（来源于 https://pypi.org/project/snowpaw）：

```python
def logaround(func):
    """log around"""

    @wraps(func)
    def wrapper(*args, **kwargs):
        logging.info("function %s start", func.__name__)
        result = func(*args, **kwargs)
        logging.info("function %s end", func.__name__)
        return result

    return wrapper
```

使用装饰器：

```python
@logaround
def get_name(this):
    return this.__name
```

装饰器（Decorators）的主要用途是，在不侵入（不改动）某个函数的前提下，修改或增强该函数的功能。具体来说，装饰器接收一个函数作为入参，增加一些其它功能，然后返回增加功能后的新函数。装饰器也是基于函数式编程范式的概念，但其能力可以在面向对象编程中被应用。

更多请参考 [Python Decorators](https://www.programiz.com/python-programming/decorator)。
