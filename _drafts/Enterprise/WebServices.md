### Web Services

Web Services 是 RPC 的一种应用，就 Java 生态来说，我们是指 **JAX-WS Web Services** （当然还有 POJO Web Services），也就是 JAX-RPC。我们先来看下 Java 官方关于 RPC 的[说明](http://docs.oracle.com/javaee/1.4/tutorial/doc/JAXRPC.html)：

> JAX-RPC stands for Java API for XML-based RPC. JAX-RPC is a technology for building web services and clients that use remote procedure calls (RPC) and XML. Often used in a distributed client-server model, an RPC mechanism enables clients to execute procedures on other systems.

> In JAX-RPC, a remote procedure call is represented by an XML-based protocol such as SOAP. The SOAP specification defines the envelope structure, encoding rules, and conventions for representing remote procedure calls and responses. These calls and responses are transmitted as SOAP messages (XML files) over HTTP.

> Although SOAP messages are complex, the JAX-RPC API hides this complexity from the application developer. On the server side, the developer specifies the remote procedures by defining methods in an interface written in the Java programming language. The developer also codes one or more classes that implement those methods. Client programs are also easy to code. A client creates a proxy (a local object representing the service) and then simply invokes methods on the proxy. With JAX-RPC, the developer does not generate or parse SOAP messages. It is the JAX-RPC runtime system that converts the API calls and responses to and from SOAP messages.

> With JAX-RPC, clients and web services have a big advantage: the platform independence of the Java programming language. In addition, JAX-RPC is not restrictive: a JAX-RPC client can access a web service that is not running on the Java platform, and vice versa. This flexibility is possible because JAX-RPC uses technologies defined by the World Wide Web Consortium (W3C): HTTP, SOAP, and the Web Service Description Language (WSDL). WSDL specifies an XML format for describing a service as a set of endpoints operating on messages.

#### SOAP

SOAP 即简单对象访问协议(Simple Object Access Protocol)，是一种简单的基于 XML 的一个子集的数据交换协议，它使应用程序通过 HTTP 来交换信息。或者更简单地说，SOAP 是用于访问网络服务的协议。

一条 SOAP 消息就是一个普通的 XML 文档，包含下列元素：

+ 必需的 Envelope 元素，可把此 XML 文档标识为一条 SOAP 消息。
+ 可选的 Header 元素，包含头部信息。
+ 必需的 Body 元素，包含所有的调用和响应信息。
+ 可选的 Fault 元素，提供有关在处理此消息所发生错误的信息。

我们来看一个SOAP实例：

```XML
<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ns="http://www.tibco.com/cim/services/contentservice/wsdl/2.0">
    <soap:Header />
    <soap:Body>
        <ns:DownloadRequest>
            <!--Optional: -->
            <ns:UserInfo>
                <ns:UserName>Admin</ns:UserName>
                <!--Optional: -->
                <ns:Password>euc!1d</ns:Password>
                <!--Optional: -->
                <ns:Enterprise>TIBCOCDC</ns:Enterprise>
            </ns:UserInfo>
            <ns:DownloadFile contextType="Event">
                <!--Optional: -->
                <ns:DocumentType>generalDoc</ns:DocumentType>
                <!--You have a CHOICE of the next 2 items at this level -->
                <!--Optional: -->
                <ns:DocumentId>334080</ns:DocumentId>
                <!--Optional: -->
                <!--ns:DocumentPath></ns:DocumentPath -->
            </ns:DownloadFile>
            <!--You may enter ANY elements at this point -->
        </ns:DownloadRequest>
    </soap:Body>
</soap:Envelope>
```

#### WSDL

WSDL是web services 描述语言，由以下几部分组成：

+ service（port）：定义提供 web service 的地址。
+ portType：描述一个 web service 可被执行的操作（action），以及相关的消息。可以把 portType 元素比作传统编程语言中的一个函数库（或一个模块、或一个类）。
+ binding：为每个 portType 定义消息格式和协议细节，比如 service style 属性（style 属性可取值 rpc 或 document）和 transport 属性（transport 属性定义了要使用的 SOAP 协议，在下面的例子中我们使用 HTTP）等。
+ message：定义 web service 使用的消息，每个消息均由一个或多个 part 组成，可以把这些part比作传统编程语言中一个函数调用的参数。
+ types：定义消息类型，它使用某种类型系统（如 XSD）。

我们来看一个WSDL实例：

```WSDL
<?xml version="1.0" encoding="UTF-8"?>
<!--Created by TIBCO WSDL-->
<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" xmlns:tns="http://xmlns.example.com/1264757112187/SendMessageOperationImpl/Server" xmlns:ns0="http://www.tibco.com/schemas/SOAP/Client/Schema.xsd" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap12/" name="Untitled" targetNamespace="http://xmlns.example.com/1264757112187/SendMessageOperationImpl/Server">
    <wsdl:types>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" attributeFormDefault="unqualified" elementFormDefault="qualified" targetNamespace="http://www.tibco.com/schemas/SOAP/Client/Schema.xsd">
            <xs:element name="root">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="str" type="xs:string"/>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:schema>
    </wsdl:types>
    <wsdl:service name="server">
        <wsdl:port binding="tns:SOAPEventSourceBinding" name="SOAPEventSource">
            <soap:address location="http://localhost:8888/Server/server"/>
        </wsdl:port>
    </wsdl:service>
    <wsdl:portType name="PortType">
        <wsdl:operation name="SendMessageOperation">
            <wsdl:input message="tns:InputMessage"/>
            <wsdl:output message="tns:OutputMessage"/>
        </wsdl:operation>
    </wsdl:portType>
    <wsdl:binding name="SOAPEventSourceBinding" type="tns:PortType">
        <soap:binding style="rpc" transport="http://schemas.xmlsoap.org/soap/http"/>
        <wsdl:operation name="SendMessageOperation">
            <soap:operation soapAction="/Server/server" soapActionRequired="true" style="rpc"/>
            <wsdl:input>
                <soap:body namespace="http://InputMessageNamespace" parts="Input" use="literal"/>
            </wsdl:input>
            <wsdl:output>
                <soap:body namespace="http://OutputMessageNamespace" parts="out" use="literal"/>
            </wsdl:output>
        </wsdl:operation>
    </wsdl:binding>
    <wsdl:message name="InputMessage">
        <wsdl:part element="ns0:root" name="Input"/>
    </wsdl:message>
    <wsdl:message name="OutputMessage">
        <wsdl:part element="ns0:root" name="out"/>
    </wsdl:message>
</wsdl:definitions>
```

#### Engine

有了 WSDL，我们就可以对 web service 发起调用了，而著名的 Web Services / SOAP / WSDL engine 有 [Apache Axis2](https://axis.apache.org/axis2/java/core/index.html) 等。其也包括对 Java Architecture for XML Binding (JAXB) 的支持，更多细节，请查看官方文档。
