# XML

## XML Object Model

Java 处理 XML 技术也称作 **Java API for XML Processing (JAXP)**。下面就把各种处理 XML 的模型列出来，以作对比和技术选型时的参考：


| Model | Vendor | Feature | Parse | Write | Validate | Performance | Usability |
|------|--------|---------|-------|-------|----------|-------------|-----------|
| DOM | Java(W3C) | object based model | in-memory tree,<br/>OO to some degree,<br/>javax.xml.parsers.DocumentBuilder, javax.xml.parsers.DocumentBuilderFactory, org.w3c.dom.* | javax.xml.transform | **DTD**: validate while parsing(on-line, locally);<br/>**XSD**: validate while parsing, specify schema language(on-line, locally), javax.xml.validation | Good(validate); Best(write) | Hard |
| JDOM | 3rd-party | object based model, Java-specific, concrete class | in-memory tree,<br/>OO and XPath, Java Collection framework,<br/>org.jdom.input.SAXBuilder, org.jdom.* | org.jdom.output.XMLOutputter | **DTD**: validate while parsing/build(on-line, locally);<br/>**XSD**: validate while parsing/build, specify schema language(on-line, locally) | Poor(validate); So-so(write) | Easy |
| DOM4J | 3rd-party | object based model, Java-specific, abstract class | in-memory tree,<br/>XPath, Java Collection framework,<br/>org.dom4j.io.SAXReader, org.dom4j.* | org.dom4j.io.XMLWriter | **DTD**: validate while parsing/read(on-line, locally);<br/>**XSD**: validate while parsing/read, specify schema language(on-line, locally), validate via org.dom4j.io.SAXValidator | So-so(validate); So-so+(write) | Easy |
| SAX | Java | event based model | event, push-parsing, read-only,<br/>javax.xml.parsers.SAXParser, javax.xml.parsers.SAXParserFactory, org.xml.sax.* | javax.xml.transform | **DTD**: validate while parsing(on-line, locally);<br/>**XSD**: validate while parsing, specify schema thing(on-line, locally) | Best, 25% faster than DOM(validate) | Easy |
| StAX | Java | event based model, streaming | event, pull-parsing, read-only,<br/>javax.xml.stream.XMLEventReader, javax.xml.stream.XMLInputFactory, javax.xml.stream.events.* | javax.xml.stream.XMLOutputFactory, javax.xml.stream.XMLStreamWriter | **DTD**: Not Supported;<br/>**XSD**: javax.xml.validation.Validator | Unknow | Easy |
| XOM | | | | | | | |

## XML Binding

XML 模型允许我们对 XML 进行遍历和读写等。还有一种需求，我们需要实现 XML 文档和 Java 对象的映射。这样程序员可以以面向对象的方式开发以 XML 作为底层数据格式的应用程序，而不需要关注 XML 的具体知识。

### JAXB

Java 自带的支持 XML Binding 的技术，称作 **Java Architecture for XML Binding (JAXB)**，相关代码位于 javax.xml.bind 包下面。JAXB 提供了 Java 和 XML 作用的一整套解决方案。

![JAXB](theJAXB.jpg)

如上图所示，从 schema/XSD 的定义，到 Java 类的生成，再到 Java 对象和 XML 的相互转化。生成的 XML 也是符合 schema 约束的。

接下来，我们来看具体代码怎么写。首先，我们要有一个 schema/XSD 文件，这是一切的基础：

```XML
<?xml version="1.0"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">

	<xs:complexType name="POJO">
		<xs:sequence>
			<xs:element name="name" type="xs:string" minOccurs="1" />
			<xs:element name="value" type="xs:string" minOccurs="0" />
		</xs:sequence>
		<xs:attribute name="ID" type="xs:int" />
	</xs:complexType>

	<xs:element name="pojo" type="POJO" />
</xs:schema>
<!-- schema.xsd -->
```

调用 JAXB XJC schema binding compiler（简称 JAXB compiler）生成 Java 类：

```
xjc schema.xsd -d . -p org.lttpp.jaxb
```

JAXB compiler 通过 schema 生成相应的 Java 类。上面命令执行，生成如下 Java 类（和一个工厂类）：

```Java
@XmlRootElement(namespace = "http://lttpp.org/xsd/TestScheme") // 注意：该注解是手动加上的，为了后面的示例顺利进行
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POJO", propOrder = {
    "name",
    "value"
})
public class POJO {

    @XmlElement(required = true)
    protected String name;
    protected String value;
    @XmlAttribute(name = "ID")
    protected Integer id;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer value) {
        this.id = value;
    }

}
```

生成的 Java 类，除了需要我们手动加上 `@XmlRootElement` 注解，其它什么都不需要修改，就可以用来生成 XML：

```Java
// Java POJO to XML document

POJO pojo = new POJO();

// set some values to fields
pojo.setId(4);
pojo.setName("Eclipse");
pojo.setValue("Mars");

JAXBContext context = JAXBContext.newInstance(POJO.class);
Marshaller marshaller = context.createMarshaller();

marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
marshaller.marshal(pojo, System.out);
```

生成的 XML 如下：

```XML
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:pojo xmlns:ns2="http://lttpp.org/xsd/TestScheme" ID="4">
    <name>Eclipse</name>
    <value>Mars</value>
</ns2:pojo>
<!-- located at pathTo/file -->
```

生成（或任意符合规范）的 XML，可以反向转化成 Java 对象：

```Java
// XML document to Java POJO

JAXBContext context = JAXBContext.newInstance(POJO.class);  
Unmarshaller unmarshaller = context.createUnmarshaller();  
POJO pojo = (POJO) unmarshaller.unmarshal(new File("pathTo/file"));
```

转化后得到的就是一个普通的 Java 对象。另外，其中生成的 XML 也是符合 schema/XSD 约束的，可以通过其验证。

至此，JAXB 的整个过程，我们都走了一遍。

### XStream

[XStream](http://x-stream.github.io/) 是一个支持 Java 对象到 XML 文档序列化和反序列化的第三方库。如下代码所示：

```Java
XStream xstream = new XStream(new DomDriver());

xstream.alias("person", Person.class);
xstream.alias("phonenumber", PhoneNumber.class);

Person joe = new Person("Joe", "Walnes");
joe.setPhone(new PhoneNumber(123, "1234-456"));
joe.setFax(new PhoneNumber(123, "9999-999"));

String xml = xstream.toXML(joe);
```

```Java
Person newJoe = (Person)xstream.fromXML(xml);
```

```Java
public class Person {
    private String firstname;
    private String lastname;
    private PhoneNumber phone;
    private PhoneNumber fax;
    // ... constructors and methods
}

public class PhoneNumber {
    private int code;
    private String number;
    // ... constructors and methods
}
```

更多细节，请查看 [Two Minute Tutorial](http://x-stream.github.io/tutorial.html)。

## 最后

XML 作为数据交换格式，已经是过去时，其替代者 JSON 同样语言无关，平台无关，而且更简单易读，是更多被新系统采用的格式。
