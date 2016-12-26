# 动态类型

## 什么是动态类型

众所周知，Java是静态类型语言，我们先来看静态类型和动态类型的定义：

+ 静态类型语言：类型检查是在编译期进行；
+ 动态类型语言：类型检查是在运行期进行。

Java 7 以前，字节码指令集中的四条方法调用指令（invokevirtual、invokespecial、invokestatic、invokeinterface）的第一个参数都是被调用的方法的**符号引用**（CONSTANT_Methodref_info或者CONSTANT_InterfaceMethodref_info常量），方法的符号引用在编译期产生，而对于动态类型，只有在运行期才能确定接收者类型，也即确定方法引用。

Java 7 为了更好地支持动态类型，引入了第五条方法调用的字节码指令，即动态调用指令 invokedynamic，解决原有四条方法调用指令的方法分派规则固化在虚拟机之中的问题，把决定方法接收者的过程，从虚拟机转移到用户代码之中。其中关键在于，这条指令的第一个参数不再是方法的符号引用，而是变为 JDK 7 新加入的 CONSTANT_InvokeDynamic_info 常量。

关于[如何使用动态调用指令](http://www.infoq.com/cn/articles/jdk-dynamically-typed-language)进行方法接收者的动态指定，这里就不详述了，我们来看 JDK 7 提供的语言层面的对动态类型的支持。

## 方法句柄（Method Handle）

Java 7 引入了java.lang.invoke包，主要目的就是在之前单纯依靠符号引用来确定方法的接收者之外，提供一种新的动态确定方法的接收者类型的机制，即 **Method Handle**。下面代码演示了 Method Handle 的基本用法，我们借用C#的Delegate概念，对 Method Handle 机制进行封装，从而方便客户端代码的调用：

```Java
public class ObjectDelegate {

    private Object receiver;
    private boolean forStatic = false;
    private String methodName;
    private Class<?> returnType = void.class;
    private List<Class<?>> paramTypes = new ArrayList<>();
    private List<Object> params = new ArrayList<>();

    public ObjectDelegate() {

    }

    public ObjectDelegate(final Object receiver) {
        this.receiver = receiver;
    }

    public ObjectDelegate receiver(Object receiver) {
        this.receiver = receiver;
        return this;
    }

    public ObjectDelegate forStatic() {
        this.forStatic = true;
        return this;
    }

    public ObjectDelegate method(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public ObjectDelegate returnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }

    public ObjectDelegate paramType(Class<?> paramType) {
        this.paramTypes.add(paramType);
        return this;
    }

    public ObjectDelegate param(Object arg) {
        this.params.add(arg);
        return this;
    }

    public ObjectDelegate paramTypes(List<Class<?>> paramTypes) {
        this.paramTypes = paramTypes;
        return this;
    }

    public ObjectDelegate paramTypes(Class<?>... paramTypes) {
        this.paramTypes = Arrays.asList(paramTypes);
        return this;
    }

    public ObjectDelegate params(List<Object> args) {
        this.params = args;
        return this;
    }

    public ObjectDelegate params(Object... args) {
        this.params = Arrays.asList(args);
        return this;
    }

    public final Object invoke() throws Throwable {
        if (paramTypes.size() == 0 || params.size() == 0) {
            if (forStatic) {
                return MethodHandles.lookup().findStatic(receiver.getClass(), methodName, MethodType.methodType(returnType)).invokeWithArguments();
            } else {
                return MethodHandles.lookup().findVirtual(receiver.getClass(), methodName, MethodType.methodType(returnType)).bindTo(receiver).invokeWithArguments();
            }
        } else {
            if (forStatic) {
                return MethodHandles.lookup().findStatic(receiver.getClass(), methodName, MethodType.methodType(returnType, paramTypes)).invokeWithArguments(params);
            } else {
                return MethodHandles.lookup().findVirtual(receiver.getClass(), methodName, MethodType.methodType(returnType, paramTypes)).bindTo(receiver).invokeWithArguments(params);
            }
        }
    }

}
```

客户端代码：

```Java
new ObjectDelegate(dynamicReceiver/*方法的接受者，可能动态变化*/).method("someMethodName"/*方法名称*/).returnType(void.class/*方法返回类型*/).paramType(Object.class/*方法参数类型*/).param(someObject/*方法参数*/).invoke(); // 最后一步完成调用
```

这样就实现了动态指定方法接收者。

一个问题，这和[反射](Reflection.md)机制有什么本质的区别呢？用反射机制完全可以实现相同的功能。区别就是反射是代码层次的映像和方法调用，而**动态类型是字节码层次的方法调用**，正因为动态类型调用是字节码层面的，所以可以极大的优化（直接编译成本地代码），从而提高性能，类似直接调用一样，这是反射所没法比的。

## 最后

在1997年出版的《Java虚拟机规范》第一版中提到了这样一个愿景：

> 在未来，我们会对Java虚拟机进行适当的扩展，以便更好的支持其他语言运行于Java虚拟机之上。

显然，动态类型语言甚至动态语言都是这一愿景的一部分，而从[JVM底层直接支持动态类型](http://docs.oracle.com/javase/7/docs/technotes/guides/vm/multiple-language-support.html)调用，让我们对JVM的未来有了更多期待。
