# 字符串

### String

String 类是[不可变类](ImmutableClass.md)（immutable），也就是说每个 string 字面量（下文不加特别说明，都指string字面量）都是一个不同的对象。如果 `a.equals(b)`，得不出 `a == b`为真。事实上，只要是string地址的比较，即 `a == b`，永远不为真，因为每个字面量都是一个不同的对象。

除却字面量，通过 `new` 操作符创建的 string，更不用说，是另一个对象，所以，永远不要这样做：

```Java
new String("some string");
```

这就创建了两个对象，而他们却有相同的字面量。

到现在，似乎一切都很好，直到我们用代码去验证上面所说的，会发现并不是那样。问题出在两个具有相同字面量的 string 进行 `==` 比较，返回值为真，即下面的代码打印出 true：

```Java
String a = "a string";
String b = "a string";

System.out.println(a == b); // true
```

这是为什么，原因是Java类库对 String 类的实现，采用了一种叫**享元模式**的设计模式，即每当生成一个新**字面量（只限于字面量）**的 string 时，都被添加到一 个共享池中，当第二次再次生成同字面量的 string 时，就共享先前的对象，而不是创建一个新对象。所以，上面的代码才打印出 true。

总结，String 是不可变类，每个字面量都是一个不同的对象，但享元模式的应用，使具有相同字面量的 String 保持为一个相同的对象。

### StringBuffer

由于 String 是不可变的，当要对 string 进行修改操作（比如连接、分拆等）的时候，我们就不能仍然使用 String 对象，而要用一个可变的（mutable）对象类型，这样可以避免创建无谓的对象。

`StringBuffer` 是一个不错的选择，尤其是在需要保证线程安全的环境下使用。StringBuffer 是线程安全的，并且是可变的，我们可以尽情地进行修改操作，而始终只有当前这一个对象被创建出来。这就很大程度上节省了内存的消耗，不要小看 string 引起的内存占用，来自企业实例警告我们，当一个 string 存储的字面值够大的时候，再基于它做修改操作所占用的内存是非常可观的，引起OOM也不足为怪。

总结，StringBuffer 给我们提供的是空间上的优化举措。

### StringBuilder

StringBuffer 是线程安全的，但是在不需要考虑线程的情况下，这就显得很浪费，无谓的同步降低了性能。而自Java 5起开始提供的 `StringBuilder` 解决了这一问题。StringBuilder 不保证线程安全，只能在单线程环境中使用，但比起 StringBuffer 速度更快。

另外，它有着和 StringBuffer 完全兼容的接口（API compatible），也就是说可以在不改变客户端代码的情况下，进行切换。

总结，StringBuilder 在时间上，给我们提供了优化举措，它是更快的 StringBuffer。

## 最后

如果你要对 string 进行修改操作，则必须要用 StringBuilder，或者 StringBuffer，再看程序环境，如果是单线程则 StringBuilder，如果是多线程且需保证线程安全则 StringBuffer。
