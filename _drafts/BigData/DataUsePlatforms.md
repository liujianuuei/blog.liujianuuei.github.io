# 数据应用和服务

## 用户画像

通过全方位的标签，刻画一个用户的特征，即给用户画像，然后针对性的经营。

### 存储方式

画像的应用分两种场景，一种是直接面向业务人员平台展示，一种是通过 API 进行系统对接，比如经营场景。

平台展示是通过用户ID查询该用户所有标签，即“人查值”，本质上是一个 OLAP 场景，可以采用 KV 存储，比如 [HBase](TechItself-batch.md#HBase)，或者一般的 OLAP 引擎，比如 [Greenplum](TechItself-batch.md#OLAP)（所有标签按用户ID维度聚合成一行，本质上还是 KV 结构）。

与平台展示相反，经营场景是通过标签查询一群人，即“值查人”。而且其对性能也不是那么“敏感”，可以直接基于 Hive 存储介质，用一些快速 SQL 查询引擎获取数据，比如 [Presto](TechItself-batch.md#Presto)。

## 用户旅程

通过[事务表](DataWarehouse-batch-overview.md#事务表/事务事实表)的反应**完整业务变化事实**的特性，构建用户全生命周期旅程。

### 存储方式

用户旅程的场景是通过用户ID查询该用户所有旅程事件，本质上是一个 OLAP 场景，可以采用 KV 存储，比如 [HBase](TechItself-batch.md#HBase)，或者一般的 OLAP 引擎，比如 [Greenplum](TechItself-batch.md#OLAP)。

## 指标体系

本文讨论限定于离线指标。

指标作为衡量系统或业务健康度的晴雨表，在决策和紧急处置场景下发挥着重要作用。指标应用遵循[表达框架——指标技术体系](DataTools.md#指标配置化加工工具)，构建完善的指标体系。

## 标签体系

本文讨论限定于离线标签。

用户画像之外，还可以建立丰富的标签体系（业务人员负责）。从不同角度刻画用户以及用户的行为，进而针对性地经营。

### 存储方式

标签应用分两种场景，一种是直接面向业务人员平台展示，一种是通过 API 进行系统对接，比如经营场景。

平台展示是通过用户ID查询该用户所有标签，即“人查值”，本质上是一个 OLAP 场景，可以采用 KV 存储，比如 [HBase](TechItself-batch.md#HBase)，或者一般的 OLAP 引擎，比如 [Greenplum](TechItself-batch.md#OLAP)（所有标签按用户ID维度聚合成一行，本质上还是 KV 结构）。

与平台展示相反，经营场景是通过标签查询一群人，即“值查人”。而且其对性能也不是那么“敏感”，可以直接基于 Hive 存储介质，用一些快速 SQL 查询引擎获取数据，比如 [Presto](TechItself-batch.md#Presto)。

## 数据服务——API

上述所讲的都是指数据的应用。数据可以通过广义上的 API 的方式（比如：HDFS 文件地址、Kafka 所代表的的消息队列、SQL 和 NoSQL 数据库等）对外提供服务。

## 数据服务——自助数据分析工具

除了 API 的方式，业务自助取数进行分析也是一种数据服务策略。本文讨论限定于离线数据分析。

自助分析工具（或多维分析工具）并不完善，不详述。

![自助数据分析工具/多维分析工具](dw-batch-self-service-analysis-tool.png)

![用户路径分析工具/时序验证工具实现参考](dw-batch-self-service-path-analysis.png)

![用户路径分析工具+经营时机工具实现方案](dw-batch-self-service-path-analysis+tag.png)
