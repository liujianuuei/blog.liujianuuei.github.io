# 精密计算

用于数值计算的原生支持包括：`int`、`long`、`fload`、`double`，对于带小数的计算则有fload和double，问题是这两个类型设计为不能提供精确的结果，比如下面的程序：

```Java
System.out.println(1.00 - (9 * 0.10));
```

输出结果是0.09999999999999998。

如果是高精度的业务计算（比如货币计算），而且是带小数的，则必须使用`BigDecimal`。

BigDecimal帮我们做了两件事：

+ 精度（Scale）
+ 舍入（Rounding）

这也正是精密计算所必须考虑的两件事。

BigDecimal可以通过**数字**来构造，也可以通过**数字的字面值**数值字符串来构造。为BigDecimal指定精度和舍入的策略可以通过两种方式：

+ 通过`public BigDecimal setScale(int newScale, RoundingMode roundingMode)`设置精度和舍入，或者在计算（add、subtract、multiply、divide等等）的时候，即时指定精度和舍入。
+ 通过`MathContext`，可以在构造的时候或者计算的时候即时带入；MathContext设置的也是精度和舍入两个参数，单更倾向于是业内形成的out-of-box的标准配置，比如IEEE 754R Decimal128。

需要特别注意的是，BigDecimal是不可变类，因此对其的任何改变都将反应在一个新的返回的实例上。比如下面的代码：

```Java
BigDecimal bd = new BigDecimal(1.222);
bd = bd.setScale(1, RoundingMode.HALF_UP); // 必须把返回值重新赋予变量，得到新对象的引用
System.out.println(bd);
```

输出结果是1.2。

## 数值比较

不要用`equals()`方法来比较BigDecimal，因为它会把精度也算进去，如果精度不同，结果也不同，即便数值本身是一样的，如：

```Java
BigDecimal a = new BigDecimal("2.00");
BigDecimal b = new BigDecimal("2.0");
System.out.println(a.equals(b)); // false
```

我们应该用`compareTo(BigDecimal val)`和`signum()`方法：

```Java
a.compareTo(b);  // returns (-1 if a < b), (0 if a == b), (1 if a > b)
a.signum(); // returns (-1 if a < 0), (0 if a == 0), (1 if a > 0)
```

compareTo比较的是真正的数值，比如：

```Java
BigDecimal a = new BigDecimal("2.00");
BigDecimal b = new BigDecimal("2.0");
System.out.println(a.compareTo(b)); // return 0
```

## 什么时候舍入

应该推迟到最后的结果出来的时候进行舍入操作，以保持最精确的结果。

## 最后

如果你操作的全是整数（结果也期望是整数），则int和long就可以很好的完成你的工作，如果你操作的数值非常大甚至超出了long的范围，则这时候BigInteger可以帮你处理这样大的数值，BigInteger非常类似BigDecimal，只是不需要考虑精度和舍入的概念，其可以存储任意长度的整型数值；如果是带有精度的十进制精密计算则必须用BigDecimal。