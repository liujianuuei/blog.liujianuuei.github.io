# 函数式

## 什么是函数式

函数式的本质是什么？它有哪些优于面向对象的特性？

## 函数式接口

所谓的函数式接口，即有且仅有一个抽象方法的接口。由于默认方法不是抽象的，因此可以在函数式接口里添加任意多个默认方法。为了符合规范，应当在接口前加上 `@FunctionalInterface` 标注。编译器会注意到这个标注，如果接口中定义了第二个抽象方法的话，编译器会报错。

```Java
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
```

```Java
Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("29");
```



Java 8 - http://www.infoq.com/articles/Kicking-Off-Java-EE-8
http://www.infoq.com/articles/javaone2013-roundup

http://www.infoq.com/cn/articles/How-Functional-is-Java-8?utm_campaign=infoq_content&utm_source=infoq&utm_medium=feed&utm_term=global

http://blog.sanaulla.info/2013/03/21/introduction-to-functional-interfaces-a-concept-recreated-in-java-8/

\---

http://www.infoq.com/cn/articles/How-Functional-is-Java-8

## Lambda

Lambda表达式如何匹配 Java 的类型系统？每一个lambda都能够通过一个特定的接口，与一个给定的类型进行匹配。每个与之对应的lambda表达式必须要与抽象方法的声明相匹配。任意只包含一个抽象方法的接口，我们都可以用来做成lambda表达式。

http://www.jb51.net/article/48304.htm

http://www.cnblogs.com/figure9/archive/2014/10/24/4048421.html

## 闭包

http://www.oschina.net/question/82993_74395
