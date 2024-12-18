# 数仓架构——有批也有流的世界

## 批 & 流

批先独立成长起来，然后流的需求出现。

### 混合批流——第一代架构

Lambda(λ) 架构是最简单最容易自然长成的批流架构，但问题也很突出：

① 相同业务逻辑就批和流进行两次编程，最终维护两个代码库，两个研发团队。

② 批流分别独立处理和输出结果，数据一致性是潜在风险（原因如不同的代码缺陷）。

![](dw-arch-lambda.png)

### 流式架构（或批流一体架构）——第二代架构

Kappa(κ) 架构的核心是流式处理，不再有独立的批处理。

![](dw-arch-kappa.png)

![](dw-arch-kappa-details.png)

来源：https://nexocode.com/blog/posts/kappa-architecture/

但问题是，供事后分析的离线数据或批视图（Batch View）怎么存储。批存储介质应该是一个 Analytics SQL Database，且需要满足如下特性：

① 支持大规模数据集（Large-Volume） - 至少是分布式存储介质。

② 支持事务型增删改（ACID） - 因为要通过流式计算生成离线数据。

③ 支持高并发写（可以低并发读，High-Performance） - 因为要通过流式计算不断更新数据集。

④ 自动按时间封版同时还能追溯历史变更 - 自动按时间（比如天级或小时级）分区。

⑤ 支持 SQL 查询 - 业务人员更熟悉 SQL。

备选存储介质：

- 数据湖（比如 Delta Lake、Apache Iceberg、Apache Hudi）
- Apache Doris
- Apache Phoenix(HBase)
- Apache Ignite - As a SQL Database
- Apache Pinot（Shopify's use-case）
- Apache Kudu


> Delta Lake

> ACID Transactions: Protect your data with serializability, the strongest level of isolation.
> DML Operations: SQL, Scala/Java and Python APIs to **merge, update and delete datasets**.


> Iceberg

> Iceberg is a high-performance format for huge analytic tables. Iceberg brings the reliability and simplicity of **SQL tables** to big data, while making it possible for engines like Spark, Trino, Flink, Presto, Hive and Impala to safely work with the same tables, at the same time. Iceberg supports flexible SQL commands to **merge new data, update existing rows, and perform targeted deletes**. Iceberg can eagerly rewrite data files for read performance, or it can use delete deltas for faster updates.


重要参考：[Kappa Architecture is Mainstream Replacing Lambda](Kappa-Architecture-is-Mainstream-Replacing-Lambda-Kai-Waehner.pdf)

#### Kappa(κ) vs. Lambda(λ)

![](dw-arch-lambda-vs-kappa.png)

来源：https://nexocode.com/blog/posts/lambda-vs-kappa-architecture/
