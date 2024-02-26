# Stream

Java 8 中的 Stream 是对集合（Collection）对象进行各种高效的聚合操作（aggregate operation），或者大批量数据操作 (bulk data operation)。Stream API 借助于 Lambda 表达式，提高了程序可读性，同时它提供串行和并行两种模式进行聚合操作。JDK 对 Stream 的定义是：

```
A sequence of elements supporting sequential and parallel aggregate operations.
```

我们可以通过 `stream()` 方法得到数据源（Source）上的一个 Stream，来看如下代码示例：

```Java
collectionOfElements.stream().filter(predicate).sorted(comparator).map(function).collect(collector);
```

先抛开函数式编程的诸多细节（诸如 predicate、function等），我们可以看出 Stream API 极大的简化了集合的操作。Java 8 之前，我们要完成上面的功能，我们怎么做？对于 filter 操作， 要用循环遍历集合里的每个元素，从而才能筛选出特定的元素，对于 sort(ed) 操作，我们可以用 Collections 的工具方法代替，虽然不是很复杂但也是单独调用，没有这种序列式调用便利，对于 collect 操作（注意这里收集的是集合元素的某个域而不是元素本身），我们仍然需要循环遍历整个集合，取出我们需要的域，最后还要再放入另一个集合里，诸如此类，非常繁琐。而这就是 Stream API 派上用场的时候，凡是需要循环遍历集合类，以至其它任何一系列的元素（A sequence of elements），我们都应该考虑下 Stream API 是不是能帮助我们简化代码。

## Parallel Stream

不止于简化代码，Parallel Stream 还极大的提高了性能。可以通过 `parallelStream()` 方法得到一个 parallel stream。JDK 文档对该方法的描述是：

```
Returns a possibly parallel Stream with this collection as its source. It is allowable for this method to return a sequential stream.
```

看来，不是一定返回 parallel stream，如果操作无法通过并行方式完成，则该方法等同于普通的 stream 方法，返回一个 sequential stream。我测试的结果是 parallel stream 的性能是使用循环遍历（Java 8 之前的方法）或普通 sequential stream 的两倍，就是执行相同的任务，时间是一半。

## MapReduce

https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/

http://www.techug.com/java-8-no-more-loops
