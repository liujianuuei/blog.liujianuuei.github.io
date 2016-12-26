# JSON

## What Is JSON

[JSON (JavaScript Object Notation)](http://www.json.org/) is a lightweight data-interchange format, also known as data serialization standard. It is completely language and platform independent. It is based on a subset of the [JavaScript Programming Language](http://javascript.crockford.com/).

就数据结构而言，JSON 格式定义如下：

0. 标量：是数据最小单位。标量包括以下类型：null、number（包括整型、浮点数、定点数）、string、boolean。
1. 并列的数据：之间用逗号（,）分隔。
2. 映射：用冒号（:）表示。
3. 并列数据的集合（An ordered list of values，也就是**数组**或称作序列）：用方括号([])表示。
4. 映射的集合（A collection of name/value pairs，也就是**对象**）：用大括号（{}）表示。

其中，标量（0）、并列数据的集合（3）和映射的集合（4）都是有效 JSON 表示。另外，JSON 使用'\'作为转义字符。

Lets see some JSON examples:

```JSON
null
```

```JSON
"string"
```

```JSON
[
    1,
    2,
    3
]
```

```JSON
{
    "array": [
        1,
        2,
        3
    ],
    "boolean": true,
    "null": null,
    "number": 123,
    "object": {
        "a": "b",
        "c": "d",
        "e": "f"
    },
    "string": "Hello World"
}
```

## JSON Schema

## Serialization & Deserialization

### Gson

Gson is a Google provided Java library that can be used to convert Java Objects into their JSON representation, and convert a JSON string to an equivalent Java object.

The advantage of Gson is there is no need to use any annotations to indicate a field is to be included for serialization and deserialization. All fields in the current class (and from all super classes) are included by default.

Gson allows you to register your own custom serializers and deserializers.

Lets see an example as follows:

```Java
Gson gson = new Gson();
POJO model = gson.fromJson("{\"Name\": \"Java\", \"value\": 1995}", POJO.class);

String json = gson.toJson(model); // ==> jons is {"Name":"Java","value":"1995"}
```

```Java
public class POJO {

    private String Name;
    private String value;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
```

More third-party [articles](http://www.javacreed.com/category/gson/) about Gson. 更多第三方类库，请查看 [JSON 官网](http://www.json.org/)。

### Java 9 New Feature
