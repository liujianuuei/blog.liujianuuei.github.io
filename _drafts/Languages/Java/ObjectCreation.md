# 对象创建

## 简单（静态）工厂

对于类而言，除了显式的`new`一个实例，还应该考虑：**用静态工厂方法代替构造器**，更多细节请参考[设计模式之工厂模式](设计模式之工厂模式)。这样做的好处主要在于可以规避方法签名的冲突，和对于**不可变类**不必每次都创建一个新对象，可以重复使用缓存起来的实例，比如`Boolean.valueOf(boolean)`。

 > The ability of static factory methods to return the same object from repeated invocations allows classes to maintain strict control over what instances exist at any time. Classes that do this are said to be instance-controlled. There are several reasons to write instance-controlled classes. Instance control allows a class to guarantee that it is a singleton (Item 3) or noninstantiable (Item 4). Also, it allows an immutable class (Item 15) to make the guarantee that no two equal instances exist: a.equals(b) if and only if a==b. If a class makes this guarantee, then its clients can use the == operator instead of the equals(Object) method, which may result in improved performance. Enum types (Item 30) provide this guarantee.

静态工厂方法的另一优势在于，可以返回返回类型的任何子类型的对象，即便这个子类型是私有的。这就达到了完全的解耦，这项技术适用于面向接口编程。

> This technique lends itself to *interface-based frameworks* (Item 18), where interfaces provide natural return types for static factory methods. Interfaces can't have static methods, so by convention, static factory methods for an interface named Type are put in a noninstantiable class (Item 4) named Types.

> For example, the Java Collections Framework has thirty-two convenience implementations of its collection interfaces, providing unmodifiable collections, synchronized collections, and the like. Nearly all of these implementations are exported via static factory methods in one noninstantiable class (java.util.Collections). The classes of the returned objects are all nonpublic.

> The class of the object returned by a static factory method need not even exist at the time the class containing the method is written. Such flexible static factory methods form the basis of *service provider frameworks*, such as the Java Database Connectivity API (JDBC). A service provider framework is a system in which multiple service providers implement a service, and the system makes the implementations available to its clients, decoupling them from the implementations.

另外，关于类或接口本身的实例则是由`Class`这个类来表示，`Class`不能被程序创建，而是由JVM负责创建和加载。

> Instances of the class Class represent classes and interfaces in a running Java application. An enum is a kind of class and an annotation is a kind of interface. Every array also belongs to a class that is reflected as a Class object that is shared by all arrays with the same element type and number of dimensions. The primitive Java types (boolean, byte, char, short, int, long, float, and double), and the keyword void are also represented as Class objects.

> Class has no public constructor. Instead Class objects are constructed automatically by the Java Virtual Machine as classes are loaded and by calls to the defineClass method in the class loader.

静态工厂方法的缺点在于，他们与其它的静态方法没有区别，所以很难找到这类方法。但可以通过遵守标准的命名习惯弥补这一劣势。下面是一些管用命名方法：

+ valueOf
+ of
+ getInstance 返回的实例是通过方法的参数来定义的。
+ newInstance 返回的实例与其它所有实例不同。

总之，对于那些创建对象比较复杂，开销比较大的类，都应该提供静态工厂方法来简化实例创建。

## 构建器（Builder）

静态工厂和构造器有个共同的局限：他们不适用于大量的可选参数。一个替代办法就是JavaBean模式，调用一个无参构造器创建对象，然后用`setter`方法设置参数。这样的一个问题就是，对象创建分散到多步骤完成，而且无法做成不可变类。**构建器（Builder）**很好的同时解决了这两个问题，代码示例：

```Java
// Builder Pattern
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // Required parameters
        private final int servingSize;
        private final int servings;
        // Optional parameters - initialized to default values
        private int calories = 0;
        private int fat = 0;
        private int carbohydrate = 0;
        private int sodium = 0;

        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public NutritionFacts build() {
            return new NutritionFacts(this);
        }
    }

    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.carbohydrate;
    }
}
```
可以看到，创建的对象是不可变的且创建过程是集中在一处，而且大量的参数是分散维护的且是可选的。客户端调用示例：

```Java
NutritionFacts cocaCola = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).carbohydrate(27).build();
```

注意：构建器要定义成static的，否则报*No enclosing instance of type NutritionFacts is accessible. Must qualify the allocation with an enclosing instance of type NutritionFacts (e.g. x.new A() where x is an instance of NutritionFacts).*异常。

如果为Builder指定一个要构建的对象的类型参数T，则其就成为了一个抽象工厂：

```Java
// A builder for objects of type T
public interface Builder<T> {
    public T build();
}
```

带有Builder参数的方法通常用bounded wildcard type来约束构建器的类型参数。如：

```Java
Tree buildTree(Builder<? extends Node> nodeBuilder) { ... }
```

总之，如果累的构造器或者静态工厂方法中具有多个参数，尤其是大多数参数都是可选的时候，就应该考虑用Builder来创建对象。

## 依赖注入（DI）

除了上述显式的创建对象外，我们还可以使用**依赖注入（DI）**框架来帮助我们创建对象，我们完全不需要写任何`new`语句。接下来，Guice作为轻量级DI框架将被用于说明依赖注入的机制。

Guice的核心就是`com.google.inject.Injector`的配置：

```
Injector injector = Guice.createInjector(new BindingModule());
```

在`BindingModule`里指定具体类的实现绑定（我们以发送twitter消息为例）：

```
public class BindingModule extends AbstractModule {

    protected void configure() {

        bind(Tweeter.class).to(SmsTweeter.class);
        bind(Shortener.class).to(TinyUrlShortener.class);

        bind(TweetClient.class);

        /*
         * Instance Bindings: Best suited for value objects such as a database
         * name, or webserver port
         */
        bind(Integer.class).annotatedWith(Port.class).toInstance(8080);

        bind(ConnectionPool.class).to(ExecutorServicePool.class).in(Singleton.class);
        // bind(ConnectionPool.class).to(ExecutorServicePool.class).in(Scopes.SINGLETON);
    }

}
```

TweetClient的实现：

```Java
@Singleton
public class TweetClient {

	@Inject
	Provider<Shortener> shortenerProvider;
	private final Tweeter tweeter;

	@Inject
	public TweetClient(Tweeter tweeter) {
		this.tweeter = tweeter;
	}

	public void startup() {
		System.out.println("startup...");
	}

	public void postButtonClicked() {
		String text = "Tweet!";
		if (text.length() > 140) {
			Shortener shortener = shortenerProvider.get();
			text = shortener.shorten(text);
		}
		this.tweeter.send(text);
	}

	public Tweeter getTweeter() {
		return tweeter;
	}
}
```

其它代码:

```Java
public abstract class Tweeter {
	public abstract void send(String text);
}

public class SmsTweeter extends Tweeter {
	@Override
	public void send(String text) {
        System.out.println("Sending \"" + text + "\"");
	}
}

public abstract class Shortener {
	public abstract String shorten(String text);
}

public class TinyUrlShortener extends Shortener {
	@Override
	public String shorten(String text) {
		if (text == null) {
			return text;
		} else {
			return text.substring(0, 140);
		}
	}
}

@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
public @interface Port {
	String value() default "80";
}

public class ConnectionPool {

}

public class ExecutorServicePool extends ConnectionPool {

}
```

客户端代码：

```Java
Injector injector = Guice.createInjector(new BindingModule());
TweetClient tweetClient = injector.getInstance(TweetClient.class);
tweetClient.startup();
tweetClient.postButtonClicked();
```

在TweetClient里，我们分别就属性声明和方法传参初用**Inject**方式注入了对象实例，而不需要我们显式的new一个对象，在不能用annotation的地方我们可以直接通过`injector.getInstance`的方式get到对象实例。唯一需要new的实例，就是在创建Binding Module的时候，除此之外对象实例都是在用的时候被注入。

## 其它

除了这些*正常*的对象创建之外，还有一些*不那么正常*的对象创建机制，比如**反射**、**克隆**，以及**语言之外**（即不通过构造器创建对象）的创建形式，比如**反序列化**等。这些将在后文作为单独章节介绍。

对象仅仅被创建是不够的，有时候我们还需要控制可以被创建的对象实例的个数，即实例受控。
