### 网络

#### 什么是网络

网络就其本质来说，是由节点和连线构成，表示诸多对象及其相互关系的拓扑图。在计算机领域中，网络是信息传输、接收、共享的虚拟平台。

#### 网络层级模型

网络可以抽象为七层（或者更简单的五层）模型：

|  | 协议 | 性质 |
| ------------- |-------------| -----|
| 应用层（表示层，会话层） | HTTP，SMTP，FTP，Telnet | HTTP：短连接、无状态 |
| 传输层 | TCP，UDP | TCP：长连接、有状态；UDP：无连接、无状态 |
| 网络层 | IP |  |
| 网络接口层（数据链路层 ，物理层）| Ethernet，Serial Line |  |

#### URI

URI 是统一资源标识符（uniform resource identifier），是全世界网络资源的唯一标识。URI 有两种形式的实现：

**URL**

URL 是 统一资源定位符（uniform resource locator），一个 正确的 URL 遵循以下格式 [protocol][host][location]：

+ `http://` `www.joes-hardware.com` `/tools.html`
+ `file://` `localhost` `/C:/tools.html`

基于 file 协议的的资源定位符，可以省略 host，写成如下格式：

+ `file://` `/C:/tools.html`

**URN**

URI 的第二种实现是统一资源名，即 URN（uniform resource name），其无需知道资源的具体存放位置，因此一个主机上的资源不能重名，通过名字的唯一性来索引，这就需要一个单独的服务器来解析这种名字到具体位置的资源的映射关系。我们来看一个简单的例子：

[protocol][host][name] := `urn:` `ietf:` `rfc:2141`

其基本还是遵循和 URL 一致的格式，只是不再需要资源位置信息。URN 还处于实验阶段。

#### Messages

输入浏览器的 URL，比如 `http://www.joes-hardware.com/tools.html`，会被自动转成如下格式的 HTTP 请求（Request）发往服务器端：

```HTTP
GET /tools.html HTTP/1.1
Host: www.joes-hardware.com
```

服务器收到请求，处理并返回响应（Response）：

```HTTP
HTTP/1.1 200 OK
Date: Fri, 08 Jan 2016 07:49:54 GMT
Server: Apache/2.2.22 (Unix) DAV/2 FrontPage/5.0.2.2635 mod_ssl/2.2.22 OpenSSL/1.0.1h
Last-Modified: Fri, 12 Jul 2002 07:50:17 GMT
ETag: "146deb7-1b1-3a58f649c4040"
Accept-Ranges: bytes
Content-Length: 433
Content-Type: text/html

<HTML>

<HEAD><TITLE>Joe's Tools</TITLE></HEAD>

<BODY>

<H1>Tools Page</H1>

<H2>Hammers</H2>

<P>Joe's Hardware Online has the largest selection of
<A HREF="./hammers.html">hammers</A> on the earth.</P>

<H2><A NAME=drills></A>Drills</H2>

<P>Joe's Hardware has a complete line of cordless and corded drills,
as well as the latest in plutonium-powered atomic drills, for those
big around the house jobs.</P> ...

</BODY>

</HTML>
```

这些请求和响应也就是 HTTP Messages，从请求到响应的一个来回，称为一个 HTTP **事务（Transaction）**。

#### Methods

每个 HTTP 请求，都必须为其指定一个请求方法（Method），HTTP 支持如下请求方法：

| HTTP 方法 | 描述 |
| ------------- |-------------|
| GET | 获取服务器资源 |
| PUT | 更新服务器资源 |
| DELETE | 删除服务器资源 |
| POST | Send client data into a server gateway application. |
| HEAD | Send just the HTTP headers from the response for the named resource. |

#### Status Codes

每条 HTTP 响应消息，都会携带一个状态代码，告诉客户端请求的状态，下表列出了几种常见状态代码：

| HTTP 状态码 | 描述 |
| ------------- |-------------|
| 200 | OK。文档正确返回 |
| 302 | Redirect（重定向）。到其他地方去获取资源 |
| 404 | Not Found（没找到）。无法找到这个资源 |

伴随状态代码，HTTP 还会附带一条解释性的文本信息，但只是用来描述。

#### HTTP/2

#### Socket

IP 标识 Internet 上的计算机，端口号（port#）标识正在计算机上运行的进程（程序）。IP 与 端口号的组合构成一个 Socket。Socket 是语言层面的抽象，一旦通过 Socket 在服务器端和客户端建立起连接，就可以通过 IO 流进行读写操作了。

socket websocket Comet EventSource servlet servlet 3.0 tcp/ip http

http://blog.csdn.net/peace1213/article/details/49942971
http://cuishen.iteye.com/blog/242842

xlightweb

http://www.cnblogs.com/sharpxiajun/p/3936268.html

JDK7 -
新增 URLClassLoader.close 方法，请看 [Closing a URLClassLoader](http://download.oracle.com/javase/7/docs/technotes/guides/net/ClassLoader.html).
支持 Sockets Direct Protocol (SDP) 提供高性能网络连接，详情请看 [Understanding the Sockets Direct Protocol](http://download.oracle.com/javase/tutorial/sdp/sockets/index.html)。

SDP(Socket Direct Protocol)

SDP，套接字定向协议，提供了高吞吐量低延迟的高性能网络连接。它的设计目标是为了使得应用程序能够透明地利用 RDMA(Remote Direct Memory Access) 通信机制来加速传统的 TCP/IP 网络通信。最初 SDP 由 Infiniband 行业协会的软件工作组所指定，主要针对 Infiniband 架构，后来 SDP 发展成为利用 RDMA 特性进行传输的重要协议。JDK7 这次实现 Solaris 和 Linux 平台上的 SDP。

Socket 和文件的异步 IO。
Socket channel 的功能完善，支持 binding、多播等。

#### SCTP

SCTP(Stream Control Transmission Protocol) 即流控制传输协议，由 RFC 2960 规范。它是一种类似于 TCP 的可靠传输协议。SCTP 在两个端点之间提供稳定、有序的数据传递服务（非常类似于 TCP），并且可以保护数据消息边界（例如 UDP）。然而，与 TCP 和 UDP 不同，SCTP 是通过多宿主（Multi-homing）和多流（Multi-streaming）功能提供这些收益的，这两种功能均可提高可用性 。
