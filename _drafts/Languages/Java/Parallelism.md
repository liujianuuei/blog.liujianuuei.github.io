# 并行

这一主题我们来探讨 Java 对并行的支持。要提高应用程序（任务）的执行效率，一种方案就是使用多线程，让更多的任务同时处理，或者让一部分操作异步执行，即并发（Concurrency），另外一种方案就是把一个任务拆分为多个单元，每个单元分别执行最后合并每个单元的结果，即并行（Parallelism）。

并发的限制在于，同时处理的任务之间不能有直接依赖关系，而且并发并不是真正的同时执行多个任务，而是 JVM 在不同任务之间快速切换。并行依靠多处理器同时处理多个任务，最后把结果合并返回，并行是真正的同时执行多个任务。并发和并行的区别就是一个处理器同时处理多个任务和多个处理器或者是多核的处理器同时处理多个任务。前者是逻辑上的同时发生，而后者是物理上的同时发生。

## Fork/Join

Fork/Join 就是 Java 提供的并行框架。其核心就是 `ForkJoinPool` 和 `ForkJoinTask` 两个概念，ForkJoinPool 负责执行 ForkJoinTask。ForkJoinTask 可以拆分成更小的子任务，分别执行最后汇总结果。如下图示：

![Fork Join Framework](theForkJoinModel.jpg)

Java [文档](http://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html)给我们总结了一个代码结构模板：

```Java
if (my portion of the work is small enough)
    do the work directly
else
    split my work into two pieces
    invoke the two pieces and wait for the results
```

我们可以套用这个模板来构造自己的代码。我们以求和计算为例，代码如下：

```Java
ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors()); // ForkJoinPool.commonPool();
pool.invoke(new SumTask(0, 10000));

// shutdown pool
pool.shutdown();
pool.awaitTermination(100, TimeUnit.SECONDS);
pool.isTerminated();
//pool().awaitQuiescence(100, TimeUnit.SECONDS);
```

```Java
public class SumTask extends RecursiveTask<Integer> { // 如果不需要返回值，则可以用 RecursiveAction。

    private static final int THRESHOLD = 100;

    private int start;
    private int end;

    public SumTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        if ((end - start) < THRESHOLD) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            int middle = (start + end) / 2;
            SomeTask left = new SumTask(start, middle); // 拆分成子任务
            SomeTask right = new SumTask(middle + 1, end); // 拆分成子任务
            left.fork(); // 执行子任务
            right.fork(); // 执行子任务
            sum = left.join() + right.join(); // 等待子任务执行完毕，计算结果并返回；如果不需要返回值则可以不等待。
        }
        return sum;
    }

}
```

**注意**：目录 {JAVA_HOME}/sample/forkjoin 包含了 Fork/Join 框架的演示程序。

ForkJoinPool 一共有三种执行任务的方法，列出如下（来自于官方文档）：

![](theSumOfTaskExecMethods.png)

另外需要说明的是，ForkJoinPool 内部维护着足够多的工作者线程，来处理提交的任务，而这些线程根据处理器的数目（parallelism level）并行也就是真正的同时工作着。
