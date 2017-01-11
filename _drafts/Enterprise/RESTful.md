### RESTful

网站，那种用于展示静态数据的页面式网站已经是过去式了。**网站即软件**，这是 Web Application 兴起的根本所在，也是习惯于以传统软件思维方式思考的人们必须转变的思维方式。通过一个唯一的入口，而且除了这个唯一的入口不再需要客户端安装任何东西，就可以使用和传统本地安装的软件相同的功能，这就是未来。正如[理解RESTful架构](http://www.ruanyifeng.com/blog/2011/09/restful)一文所说：

```
……传统上，软件和网络是两个不同的领域，很少有交集；软件开发主要针对单机环境，网络则主要研究系统之间的通信。互联网的兴起，使得这两个领域开始融合，现在我们必须考虑，如何开发在互联网环境中使用的软件。
```

REST 即 **Re**presentational **S**tate **T**ransfer。如果一个架构符合 REST 原则，就称它为 RESTful 架构。那什么是RESTful架构：

1. 每一个 URI 代表一种**资源**，不应包含操作动词；
2. 客户端和服务器之间，**传递资源的表现**；
3. 客户端通过四个 HTTP 动词，对服务器端资源进行操作，实现**表现层状态转化**。

#### Java API for RESTful Web Services (JAX-RS)

https://jersey.java.net/
https://dzone.com/refcardz/rest-foundations-restful
