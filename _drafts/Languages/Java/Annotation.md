# 注解（Annotation）

## 什么是注解

注解是以程序之外的方式描述程序信息的元数据，它本身并不是程序的一部分。我们看下Java官方技术文档对注解的描述：

```
Annotations, a form of metadata, provide data about a program that is not part of the program itself. Annotations have no direct effect on the operation of the code they annotate.
```

注解也属于修饰符（modifier），可以和其它修饰符搭配使用，用以修饰包、类 型、构造方法、普通方法、成员变量、参数、本地变量的声明中。下面的写法是允许的：

```Java
public @Override void methodName() {...} // 通过@Override注解让编译器提供关于代码的更多的检查和验证
```

另外注解还可以使用参数：

```Java
@SuppressWarnings("rawtypes")
public void methodName() {   
    ...
} 
```

注解也可以用来修饰注解，即meta-annotation：

```Java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target {
    ElementType[] value();
}
```

其中关键起作用的是`@Target(ElementType.ANNOTATION_TYPE)`注解，凡是被`@Target(ElementType.ANNOTATION_TYPE)`标记的注解都是meta-annotation。

## 怎么运作的

注解是代码的元数据，首先我们需要解析出来这些注解。根据Retention的不同，需要用到不同的方法解析。我们先来看下Java对Retention的定义：

```Java
/**
 * Annotation retention policy.  The constants of this enumerated type
 * describe the various policies for retaining annotations.  They are used
 * in conjunction with the {@link Retention} meta-annotation type to specify
 * how long annotations are to be retained.
 *
 * @author  Joshua Bloch
 * @since 1.5
 */
public enum RetentionPolicy {
    /**
     * Annotations are to be discarded by the compiler.
     */
    SOURCE,

    /**
     * Annotations are to be recorded in the class file by the compiler
     * but need not be retained by the VM at run time.  This is the default
     * behavior.
     */
    CLASS,

    /**
     * Annotations are to be recorded in the class file by the compiler and
     * retained by the VM at run time, so they may be read reflectively.
     *
     * @see java.lang.reflect.AnnotatedElement
     */
    RUNTIME
}
```

+ **SOURCE**：只存在于源代码里，所以需要解析源代码即文本文件。一般多用于代码编译器进行错误检查；代码生成工具也可以依据此类注解信息生成Java代码，详情可查看`AbstractProcessor`和`JavaFileObject`等类和相关知识。（*Information for the compiler* - Annotations can be used by the compiler to detect errors or suppress warnings.）
+ **CLASS**：还存在于字节码文件里，所以需要解析字节码来得到注解信息。常见的字节码操作库有Apache Commons BCEL、 ASM、 [cglib](https://github.com/cglib/cglib)和Javassist。（*Compile-time and deployment-time processing* - Software tools can process annotation information to generate code, XML files, and so forth.）
+ **RUNTIME**：还存在于运行时，JVM负责加载。可以通过[反射](Reflection.md)API来得到注解相关信息。一个应用RUNTIME注解的例子就是**AOP**的核心——**动态代理**，详情请查看[设计模式之代理模式](设计模式之代理模式)。

一般意义上，等我们解析得到注解之后，说明我们知道了这个实体（方法或类或变量等），也知道了这个实体被什么注解标记，接下来我们就可以做任何我们想做的事情。

## 自定义注解

就像定义类或接口一样，我们也可以自定义注解，比如下面的例子：

```Java
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserDefinedAnnotation {
    String value(); // we can specify only primitive type, String, Class, annotation, enumeration are permitted or 1-dimensional arrays thereof.
}
```

需要注意的就是为其指定@Target和@Retention，应用于什么类型的Java元素和保留策略，以及@Inherited，即是否该注解要被该注解标记的类（只针对类有效，接口都无效）的子类继承，这个机制并不是注解本身的继承，注解是不支持显式继承和接口实现的，而是注解在实体类继承层面上的作用延伸：

```
@Annotation
class A {}

class B extends A {}

如果@Annotation被@Inherited标记，则A的子类B也相当于被@Annotation标记。
```

最后，如果需要参数化（为注解提供属性）则为其声明方法，方法的返回类型只可以是primitive type, String, Class, annotation, enumeration are permitted or 1-dimensional arrays thereof，还可以为方法返回默认值：

```Java
public @interface UserDefinedAnnotation {
    String value() default "default";
}
```

当然，也可以声明多个方法。

就这样。
