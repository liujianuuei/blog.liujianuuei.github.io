# Javascript

很多年我都以为 Javascript 是 Java 的变体，一种脚本语言的变体，或者Java的低级形式，直到我认识到 Java 和 Javascript 的关系就像**猕猴**和**猕猴桃**的关系。

## 数据类型

**基本数据类型**

+ number
 + 类似Java的double型数值。
 + NaN - 不能产生结果的运算结果，NaN不等于任何值，包括它自己。
 + Infinity - 表示无限大的一个数。
+ string
+ boolean  
下面列出的值被当做假（false），其它所有值都被当做真（true）：
 + false
 + null
 + undefined
 + 空字符串' '
 + 数字0
 + 数字NaN
+ null
+ undefined

**对象类型**

+ object（无类型，即class-free）

## 对象

对象是属性的容器，属性值可以是对象或函数。对象不允许包含独立的函数，函数必须作为对象的属性值存在：

```JavaScript
function isNumber(i) {
    return typeof i === 'number' ? true : false;
}

var myObject = {
    value : 0,
    increment : function(i) {
        this.value += isNumber(i) ? i : 1;
    },
    anotherObject : {
        value : 10,
        increment : function(i) {
            this.value += isNum(i) ? i : 1;
        }
    }
};
```

另外，不同于Java等基于类型的语言，JS的对象的属性可以被动态增加和删除，但仅限于自己的属性，不能增加或删除原型对象的属性：

```JavaScript
myObject.sign = "+";
```

```JavaScript
delete myObject.sign;
```

当然，也包括函数（属性）：

```JavaScript
myObject.double = function() {
	var that = this;
	var compute = function() {
		that.value = add(that.value, that.value);
	};
	compute();
};
```

## 原型

什么是原型？所谓原型可以看做是对象继承自的**父对象**，即**原型对象**。每个对象都连接到一个原型对象，并从中继承属性。所有通过对象字面量创建的对象都连接到`Object.prototype`原型对象。函数（对象）连接到`Function.prototype`（该原型对象本身连接到Object.prototype）。

每个函数（对象）在创建时还会分配一个prototype属性，值是`{constructor: 该函数}`。

## 继承

JS是无类型（class-free）的语言，（属性）继承就是为对象指定原型对象（即要继承的其它对象）的过程：

```JavaScript
function isFunction(f) {
    return typeof f === 'function' ? true : false;
}

if (!isFunction(Object.extends)) {
    Object.extends = function(protoObject) {
        var F = function() { };
        F.prototype = protoObject;
        return new F();
    };
}
```

```JavaScript
var anotherMyObject = Object.extends(myObject);
```

值得注意的是，对某个对象的属性值的改变，不会影响到该对象的原型对象。原型链是一种动态的关系，如果添加一个新的属性到某个（原型）对象中，该属性会立即对所有基于该（原型）对象创建的对象可见。

##  函数

JS中的函数就是对象，函数名只是指向函数对象的引用值，函数只不过是一种引用类型，但由于其是一种特殊的对象，所以这里更强调其函数属性。

**定义**

我们先来看函数的定义方式：

```JavaScript
function add(a, b) {
	return a + b;
}
```

同时，因为函数是对象，所以函数本身可以保存在变量中：

```JavaScript
// Create a variable called add and store a function
// in it that adds two numbers.

var add = function(a, b) {
	return a + b;
};
```

函数名和变量都是函数（对象）的引用，具有等价性。都可以通过`()`来调用。

## 作用域

## 闭包

## 模块

## 柯里化

http://www.cnblogs.com/liuyanlong/archive/2013/05/27/3102161.html
