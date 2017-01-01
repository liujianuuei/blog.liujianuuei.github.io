# 并发/Timer/AtomicReference/AtomicInteger/AtomicIntegerArray/PermGen

## 线程的生命周期

线程的生命周期会经历哪些阶段呢？新建的线程处于New状态，start之后处于Runnable状态，执行完毕处于Terminated状态。其中Runnable状态又在Ready和真正Running之间转换。如果线程被调用了sleep、wait或者join，则进入Waiting状态，如果等待对象锁则会进入Blocked状态。如下图所示：

![The Thread Life Cycle](theThreadLifeCycle.png)

### 启动线程

有两种方法启动线程：

+ 创建Thread类的实例（需要传入实现Runnable接口的类），然后调用其`start()`方法。
+ 创建继承自Thread类的实例，然后调用其`start()`方法。

启动之后，线程的`run()`方法会被自动调用，执行逻辑代码，我们不能显式调用`run()`方法。

### 线程休眠

我们可以调用`sleep()`方法来让线程进入休眠状态，除非被中断（interrupt），否则直到休眠时间结束，线程继续执行。休眠的线程并不会释放ownership of monitor。

### 等待对象锁

我们可以调用`wait()`使一个线程进入等待状态，并释放ownership of monitor，直到收到其他线程的通知（notify）或者到了timeout时间，该线程恢复执行，除非被中断。

> The current thread must own this object's monitor. The thread releases ownership of this monitor and waits until another thread notifies threads waiting on this object's monitor to wake up either through a call to the notify method or the notifyAll method. The thread then waits until it can re-obtain ownership of the monitor and resumes execution.

### 线程串行化

线程是并行执行的，但我们可以通过`join()`方法，让线程串行化执行，也就是直到被调用join方法的线程执行结束，才继续执行当前线程。

### 让步执行

有时候我们并不需要线程完全串行（等待某线程执行结束），也不能完全并行执行，而是在有限时间内交替执行，则就会用到`yield()`方法。yield的正确意思是让步，被让的线程可能执行也可能没有执行，根据调度让步线程可能仍然接着执行。

### 中断线程

当一个线程正在执行，或正在sleep、wait及join的时候，可以调用`interrupt()`方法中断该线程的当前状态，抛出`InterruptedException`，这是唯一从外部**停止**线程的方法。

### 结束线程

有三种方法可以安全的结束一个还在运行的线程：

+ `run()`方法执行完毕，自动结束。
+ 通过标识符（在`run()`的外面设定标识符，在`run()`的里面判断标识符）来指定线程结束，适用于循环执行某一操作的线程。
+ 通过`interrupt()`方法中断正在执行的线程，适用于所有线程。

### 守护（Daemon）线程

Java中存在两种线程：用户（user-thread）线程和守护线程。所谓的守护线程，是指用户程序在运行的时候后台提供的一种通用服务的线程。这类线程并不是用户线程不可或缺的部分，只是用于提供服务的**服务线程**。我们来看JDK官方文档有关Thread的说明：

> When a Java Virtual Machine starts up, there is usually a single non-daemon thread (which typically calls the method named main of some designated class). The Java Virtual Machine continues to execute threads until either of the following occurs:

> + The exit method of class Runtime has been called and the security manager has permitted the exit operation to take place.
> + All threads that are not daemon threads have died, either by returning from the call to the run method or by throwing an exception that propagates beyond the run method.

那么如何设置一个线程是守护线程呢？线程本身的`setDaemon(boolean)`方法可以设置该线程为守护线程还是用户线程。当所有的在运行的线程都是守护线程的时候，JVM就会退出。

## 线程池

线程也有“池”技术，其实任何一般性的资源敏感的对象都可以“池”化。具体来说，线程池的好处是：

1. 重用存在的线程，减少对象创建、消亡的开销，性能更佳。
2. 可有效控制最大并发线程数，提高系统资源的使用率，同时避免过多资源竞争，避免堵塞。
3. 提供定时执行，定期执行，单线程，并发数控制等功能。

Java 原生支持线程池技术。我们可以用 `java.util.concurrent.Executors` 创建不同类型的线程池，返回的线程池都实现自 `java.util.concurrent.Executor` 接口。如下代码：

```Java
ExecutorService pool = Executors.newCachedThreadPool(); // 一共四种线程池可创建

pool.execute(new Runnable() {
    @Override
    public void run() {
        // do something
    }
});

Future<?> future = pool.submit(new Runnable() {
    @Override
    public void run() {
        // do something
    }
});
future.isDone();

pool.shutdown();
pool.awaitTermination(100, TimeUnit.SECONDS);
pool.isTerminated();
```

\#Todo#：writeObject同步，序列化同步。。《Effective Java》P764
https://dzone.com/refcardz/core-java-concurrency
http://ifeve.com/

## Thread Local

http://www.ibm.com/developerworks/cn/java/j-lo-jta

## 线程安全状态机
