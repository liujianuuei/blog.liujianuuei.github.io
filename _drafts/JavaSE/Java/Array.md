# 数组

## 数组究竟是什么

数组很特殊，所以我们单独一个主题来讨论。数组的很多行为都违反直觉。

**定义数组**

首先，我们来看数组怎么定义。数组可以通过两种方式定义：

```Java
int[] array = new int[1];
array[0] = 1;
```

也可以在定义的时候，直接赋值，但这样就不能显式指定数组初始化大小：

```Java
int[] array = new int[] { 1 };
int[] array = { 1 }; // 可以简写成这样
```

当然，我们也可以以同样的方式，定义多维数组：

```Java
int[][] array = new int[2][3]; // 默认值是`0`，如果是对象数组，元素默认值是 `null`
int[][] array = new int[][] { { 1, 1, 1 }, { 2, 2, 2 } };
int[][] array = { { 1, 1, 1 }, { 2, 2, 2 } }; // 可以简写成这样
```

**粗糙（不规则）数组**

对于不止一维的数组，我们可以在定义的时候，只指定其第一维的大小，然后分别为其他维度指定不同的大小，也就是所谓**粗糙数组**，或称作不规则数组。数组中构成矩阵的每个向量都可以具有任意的长度。比如一个二维数组，其第一行是长度为3的一维数组，第二行是一个长度为6的一维数组。比如，下面的代码：

```Java
int[][] array = new int[2][];

array[0] = new int[3];
array[0][2] = 3;

array[1] = new int[6];
array[1][5] = 6;
```

粗糙数组也可以在定义的时候直接赋值：

```Java
int[][] array = new int[][] { { 0, 0, 3 }, { 0, 0, 0, 0, 6 } };
int[][] array = { { 0, 0, 3 }, { 0, 0, 0, 0, 6 } }; // 可以简写成这样
```

**数组是什么**

现在，再回到数组是什么这个问题上来。通过 `.class` 检测，可以得出结论，**数组是类**：

```Java
int[].class // 返回 class [I
Object[].class // 返回 class [Ljava.lang.Object;
```

通过 `instanceof` 检测，可以得出结论，**数组实例是对象**：

```Java
new int[1] instanceof Object // 返回 true
```

其实，非常奇异，数组居然也是对象，且有一个与其对应的类，这个类就表示为`[Ljava.lang.Object;`（对于对象数组而言）。

#### 类型推断

虽然数组是对象，且有与之对应的类，但是基本类型数组和类类型数组却还有区别，先看下面代码：

```Java
Arrays.asList(new int[] { 9 }); // 类型推断结果是 <int[]> List<int[]> java.util.Arrays.asList(int[]... a)
Arrays.asList(new Integer[] { 9 }); // 类型推断结果是 <Integer> List<Integer> java.util.Arrays.asList(Integer... a)
```

由此，我们可以得出结论，基本类型的数组是整个被当作一个独立的对象来看待的，而类类型的数组是被当作对象的集合来看待的。这就非常奇怪。唯一可能的解释就是，基本类型不是对象所以基本类型数组整个被当作对象，但是在类型推断的时候，为什么不应用*自动装箱和自动拆箱（Autoboxing and Auto-unboxing）*机制呢？

但是，下面这样却能正确推断：

```Java
Arrays.asList(9); // 类型推断结果是 <Integer> List<Integer> java.util.Arrays.asList(Integer... a)
```

多说一点，面对这种情况，我们只能自己实现一个工具方法来完成基本类型数组到包装类型数组的转换：

```Java
public static List<Integer> asList(final int[] array) {
    if (array == null) {
        return null;
    } else if (array.length == 0) {
        return ListUtil.list();
    }

    final Integer[] result = new Integer[array.length];
    for (int i = 0; i < array.length; i++) {
        result[i] = Integer.valueOf(array[i]);
    }

    return Arrays.asList(result);
}
```

另外，Java 8 以后，可以运用 [Stream](Stream) 特性简化代码：

```Java
List<Integer> list = Arrays.stream(new int[] { 9 }).boxed().collect(Collectors.toList()); // 或者下面
List<Integer> list = IntStream.of(new int[] { 9 }).boxed().collect(Collectors.toList()); // 或者上面
```

## 协变性

数组还是协变的，换句话说，有`GT1 extends GT2`，可以导出`GT1[] extends GT2[]`，所以数组是协变的，下面的代码是可以编译的：

```Java
Object[] array = new String[1];
```

所以，可以看出数组不仅是对象，更像是对象的组合（称作组合是为了区别于集合类）。

## 非线程安全

数组是对象的组合，而且不是线程安全的。比如下面的代码：

```Java
volatile Object[] array = new Object[1];
array[0] = new Object();
... // remainder omitted
```

这里数组本身是volatile的，但是数组元素不是。就是说对数组元素的赋值操作不会具有等同于volatile施加于数组本身的那种作用（即线程可见性）。在并发环境下，其它线程可以一直看到那里有一个数组，但是并不能及时看到数组元素的变化，即非线程安全。

## 泛型数组

另外，我们不能创建一个[泛型](泛型)数组，当数组定义的时候数组元素的类型就已经确定了。之所以不能实例化具有参数化（泛型）类型的数组，是由于Java的泛型是后来才引入的，为了向后兼容，参数类型在运行时会被**擦除**（都作为Object来对待），而数组在创建的时候，必须知道它所要持有的具体是哪种类型（这又是因为，数组出现在先，如果创造数组的时候就原生支持参数化，那泛型也就不是后来才引入的了。这种类似逆推法的证明，勉强解释为什么*数组必须知道它所要持有的具体是哪种类型*。），这就导致了数组不能被泛型化。但是，我们可以声明泛型数组。比如下面的代码示例：

```Java
public <T> T[] toArray(final T[] array) { // 允许声明泛型数组。
    return new T[10]; // 提示编译错误，不允许创建泛型数组。
}
```

## 最后

由于上述总总易于出错的问题，以及只能通过索引访问的不便利问题，所以编程中几乎很少用到数组，一般都以集合类代之。
