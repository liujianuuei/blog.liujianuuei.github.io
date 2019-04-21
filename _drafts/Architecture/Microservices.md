### Microservices Todo: http://microservices.io/patterns/

#### WHAT ARE MICROSERVICES

Microservices 是一种面向服务的架构，我们知道 SOA 也是一种面向服务的架构，那么这两者有什么区别呢？由于 SOA （比如，作为其实现之一，以 SOAP 作为数据交换形式的 WebService）的复杂和难以维护，并没有被广泛应用（请查看 [You've probably heard that SOA failed.](http://www.javaworld.com/article/2683277/architecture-scalability/what-microservices-architecture-really-means.html)），而 microservices 致力于解决 SOA 的难以维护和应用的缺陷，所以，microservices 也被称作 SOA 2.0，两者基本来自于同样的想法，都是为了降低企业级应用的复杂度。我们先来看几则关于 SOA 的描述（摘自[迷思-知乎专栏](http://zhuanlan.zhihu.com/prattle/19846487)）：

> SOA是一种思想，而不是wikipedia上列举的一些「已经没落的」技术的合集（XML/HTTP/SOAP/WSDL/UDDI）。十年前 SOAP/WSDL/UDDI 大行其道，现在已经基本无人问津（如果你趟过在线旅游的浑水，那么可能知道 booking.com 依旧提供基于 SOAP 封装的 API），可见技术会没落，但思想不会。

> SOA的精髓是严格的松散耦合，大家按照一个契约（service interface）来进行交流。正如Bezos所述那样，"There will be no other form of interprocess communication allowed: no direct linking, no direct reads of another team's data store, no shared-memory model, no back-doors whatsoever. The only communication allowed is via service interface calls over the network"。

基于以上我们可以得出一个推论，**基于网络的服务**是唯一对各种语言、平台以及系统解耦的方式。这就是 SOA 的精髓所在，也是 microservices 的精髓。

![](thePaoloMalinverno.png)

那么，一个 microservices 架构，应该具备哪些特征呢？如下（摘自[DZone](https://dzone.com/refcardz/getting-started-with-microservices)）：

> 1. Domain-Driven Design: Functional decomposition can be easily achieved using Eric Evans’s DDD approach.
> 2. Single Responsibility Principle: Each service is responsible for a single part of the functionality, and does it well.
> 3. Explicitly Published Interface: A producer service publishes an interface that is used by a consumer service.
> 4. Independent DURS (Deploy, Update, Replace, Scale): Each service can be independently deployed, updated, replaced, and scaled.
> 5. Lightweight Communication: REST over HTTP, STOMP over WebSocket, and other similar lightweight protocols are used for communication between services.

#### REGISTRATION & DISCOVERY

应用 Microservices 架构的一个关键是**服务的注册和发现**（service registration and discovery），或称作服务的 Management，一些专门的容器和框架为我们提供了这一服务，比如 Docker，Mesos 和 Kubernetes，以及如下所述（摘自[DZone](https://dzone.com/refcardz/getting-started-with-microservices)）：

> In a microservice world, multiple services are typically distributed in a PaaS environment. Immutable infrastructure is provided by containers or immutable VM images. Services may scale up and down based upon certain pre-defined metrics. The exact address of a service may not be known until the service is deployed and ready to be used.

> The dynamic nature of a service’s endpoint address is handled by service registration and discovery. Each service registers with a broker and provides more details about itself (including the endpoint address). Other consumer services then query the broker to find out the location of a service and invoke it. There are several ways to register and query services such as ZooKeeper, etcd, consul, Kubernetes, Netflix Eureka, and others.

#### COMMUNICATION

既然服务之间是完全独立的，那么就涉及一个相互之间通信的问题。正如，我们开始说的，只有 service interface calls over the network 是合法的，但是在现实当中，基于需求的复杂性，还有其它可能的通信方式：

1. service interface calls over the network：具体比如 REST over HTTP，这是标准合法的服务之间通信方式；
2. 基于其它协议的网络调用：比如以 XML 作为数据格式，基于 HTTP 的请求。
3. 共享数据的方式：具体比如共享内存，或共享数据库存储。

说到这里，是不是感觉和什么技术很像，就是 OSGi，OSGi 也是细分一个大型应用成一个个的 bundle，bundle 之间是独立的，且也要通过某种方式来通信。附录包含了更多关于 OSGi 的技术和思想的介绍。

#### In-Action

那么，具体如何构建 microservices 呢？有多种选择，比如 Spring Boot 就是创建 microservices 不错的选择。请查看 [Building Microservices With Java](https://dzone.com/articles/building-microservices-with-java)。

Ref https://www.evernote.com/l/AREUoggBsvhNtoN5ApNdrAjTM5TMtI0rf8A/

#### 最后

观念（精简 App 和 Service，以及 RESTful 的思想）的变化以及 Cloud（包括 Docker 生态） 的发展，为基于服务的软件架构带来了可能。

### 附录

#### OSGi

OSGi 是一套面向Java的动态模型系统（The Dynamic Module System for Java）的规范及标准。你可以动态地安装、卸载、启动、停止不同的应用模块即bundle，而不需要重启容器。

**Bundle**

Bundle从形式上讲，就是在 MANIFEST.MF 文件中加入了 OSGi 特定描述的一个 jar 包。Bundle就是 OSGi 中的模块。Bundle 有自己的生命周期。在 OSGi 中，每个 Bundle 都有自己独立于其它 Bundle 的 Class Loader 正因为这样 Bundle 之间是不可见的。这里就牵涉到一个关键的问题：Bundle 之间如何交互呢？

**Service**

Bundle 之间可以通过提供和消费 service 来达到交互的目的。所谓 service 就是一个 Bundle 提供的一个功能，在实现上，往往是一个类。服务消费方可以获取这个 service 并使用期功能。那么 service 是如何提供和获取的呢？有两种方式，一种是通过 BundleContext，一种是通过 Declarative Service 方式。

**BundleContext**

通过 BundleContext 进行交互通信，即一个 bundle 在 BundleContext 里注册 service，另一个 bundle 从 BundleContext 里得到该 service 并使用。BundleContext 通过唯一标识来识别服务，服务消费方和服务提供方要有一个*服务方式*的约定，即方法签名（包括返回类型）必须是固定的（这是最小不可变部分），这样即便服务提供方做任何实现方面的改动，对服务消费方都是透明的。示意代码如下：

```Java
bundleContext.registerService("someServiceSymbolicName", service, props);
```

```Java
bundleContext.getService(bundleContext.getServiceReference("someServiceSymbolicName"));
```

**注意**：Import 和 Export packages 不是必须的，而且这么做其实是一种耦合。但为什么还这么做，其实根本原因是因为Java是静态类型的语言，而 Java 7 支持动态类型，因此通过动态类型的调用，就可以免去导入提供服务的包。

**Declarative Service**

Declarative Service 的方式允许 Bundle 通过配置文件的方式注册和获取 service。但其实这并不可取，配置文件并不比 Java 代码简单，也并不比 Java 代码的方式（即通过 BundleContext 方式）灵活解耦，甚至相反，配置文件更难于维护，而且就注册 service 的 Bundle 来说，在 component.xml 文件里 service 的实现类是写死的，这反而是一种耦合。

**OSGi 容器**

只要你的应用完全符合OSGi规范，它就可以在所有符合OSGi规范的容器内运行。现在，有两种流行的开源OSGi容器：

+ Equinox：OSGi Service Platform Release 4 的一个实现，是 Eclipse 模块化运行时的核心。
+ Apache Felix：Apache 软件基金会赞助的一个 OSGi 容器。


Virgo （https://github.com/sercxtyf/onboard）
