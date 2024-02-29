# 异常

## Python Error 和 Exception

和 Java 一样，Python 的程序错误也分为两类：

- **Error**，即那些非受检异常，也就是程序不需要也不可能处理和恢复的异常；
- **Exception**，即受检异常，程序应该捕获并处理和恢复。

关于异常类的层级结构，请参考：[Exception hierarchy](https://docs.python.org/2/library/exceptions.html#exception-hierarchy)。

## 异常处理

Python 的异常通过关键字 `try`、`except`、`finally` 来捕获并处理，语义和 Java 一致。举例如下：

```python
try:
    server.sendmail(mail_user, send_to, msg.as_string())
    logging.info("send email success")
except Exception as e:
    logging.error("send email exception: %s", e)
    return False
finally:
    server.quit()
```

```python
try:
    #3/0
    even_numbers = [2,4,6,8]
    print(even_numbers[5])

except (ZeroDivisionError,IndexError) as e:
    print("Error occurred: ", e)
```

## 资源释放

和异常处理结合非常紧密的是资源释放。我们一般在 `finally` 子句里（如果只是为了资源的释放，`except` 子句可能都不需要），执行资源释放。

对于那些实现了 `__exit__` （如下代码示例）函数的类，我们在创建对象时，可以通过 `with` 语法，采用一种类似 Java 的简单写法，这时候我们完全不需要操心资源释放的问题，资源会被自动释放。举例如下：

```python
#!/usr/bin/env python
# -*- coding: utf-8 -*-

class Job(O):
    """A general purpose job"""

    def __init__(self, name):
        self.__name = name
        self.load_config()
        self.init_log()

    def __enter__(self):
        return self

    def __exit__(self, *args):
        logging.debug("releasing resource")

    def run(self):
        logging.info("job running...")

# vim: ts=8 et sw=4 sts=4 tw=100
```

```python
# 选择执行引擎
if engine == 'presto':
    with PrestoBoot(context) as job:
        job.run()
elif engine == 'hive':
    with HiveBoot(context) as job:
        job.run()
elif engine == 'spark':
    with SparkBoot(context) as job:
        job.run()
else:
    raise EngineNotSupport("Engine only support presto or hive or spark!!!")
logging.info('Done.')
```

## 抛出异常

通过 `raise` 关键字抛出一个异常对象。例如：

```python
raise EngineNotSupport("Engine only support presto or hive or spark!!!")
```

## 自定义异常

通过继承 `Exception` 类，可以自定义异常类。例如：

```python

class DataCheckFailed(Exception):
    """数据检查未通过"""

class EngineNotSupport(Exception):
    """Client not exist"""
    
    
class SqlExecutionFailed(Exception):
    """sql execute failed"""
```

自定义异常，作为最佳实践，一般放在一个单独的文件里，比如 `errors.py`。
