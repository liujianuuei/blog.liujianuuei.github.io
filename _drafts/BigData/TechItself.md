# 大数据技术

## Hadoop

从狭义上来说, Hadoop 包括 HDFS（Hadoop Distributed File System） 和 MapReduce 两个组件。

### HDFS

HDFS 是一个分布式的文件系统，可以运行在大规模廉价机器上。

#### Row vs. Columnar

There are two main ways in which you can organize your data: rows and columns.

1. **Row** – the data is organized by record. Think of this as a more “traditional” way of organizing and managing data. All data associated with a specific record is stored adjacently. In other words, the data of each row is arranged such that the last column of a row is stored next to the first column entry of the succeeding data row. 
2. **Columnar** – the values of each table column (field) are stored next to each other. This means like items are grouped and stored next to one another. Within fields the read-in order is maintained; this preserves the ability to link data to records.

![](row-format-michael-preston.png)

you can represent row data visually in the order in which it would be stored in memory, like this:

    1, Michael, Jones, Dallas, 32, 2, Preston, James, Boston, 25

you can represent columnar data visually in the order in which it would be stored in memory

    1, 2, Michael, Preston, Jones, James, Dallas, Boston, 32, 25

##### The Particulars of Row Formatting

With data in row storage memory, items align in the following way when stored on disk:

    1, Michael, Jones, Dallas, 32, 2, Preston, James, Boston, 25

Adding more to this dataset is trivial – you just append any newly acquired data to the end of the current dataset:

    1, Michael, Jones, Dallas, 32, 2, Preston, James, Boston, 25, 3, Amy, Clarke, Denver, 37

As writing to the dataset is relatively cheap and easy in this format, row formatting is recommended for any case in which you have **write-heavy** operations. However, read operations can be very inefficient.

That said, row formatting does offer advantages when schema changes – we’ll cover this later. In general, if the data is wide – that is, it has many columns – and is write-heavy, a row-based format may be best.

##### The Particulars of Columnar Formatting

Again, referring to the example dataset in columnar format, we can visually represent the data in the order in which it would be stored in memory as follows:

    1, 2, Michael, Preston, Jones, James, Dallas, Boston, 32, 25

Writing data is somewhat more time-intensive in columnar formatted data than it is in row formatted data; instead of just appending to the end as in a row-based format, you must read in the entire dataset, navigate to the appropriate positions, and make the proper insertions:

    1, 2, 3, Michael, Preston, Amy, Jones, James, Clarke, Dallas, Boston, Denver, 32, 25, 37

To accomplish this(Task: Obtain the sum of ages for individuals in the data.), go only to the storage location that contains information on ages and read the necessary data.  This saves a large amount of memory and time by skipping over non-relevant data very quickly. As thus, columnar formatting is recommended for any case in which you have **read-heavy** operations.

But efficient querying isn’t the only reason columnar-formatted data is popular. Columnar-formatted data also allows for efficient compression. By storing each attribute together (ID, ages, and so on) you can benefit from commonalities between attributes, such as a shared or common data type or a common length (number of bits) per entry.  For example, if you know that age is an integer data type that won’t exceed a value of 200, you can compress the storage location and reduce memory usage/allocation, as you don’t need standard amounts of allocated memory for such values. (BIGINT is typically stored as 4 bytes, for instance, whereas a short int can be stored as 2 bytes).

Further, columnar-formatted files usually support a number of flexible compression options (Snappy, gzip, and LZO, for example) and provide efficient encoding schemes. For example, you can use different encoding for compressing integer and string data; as all the data is very similar in a column, it can be compressed more quickly for storage and decompressed for analysis or other processing.  In a row-based storage structure, on the other hand, you must compress many types of data together – and those rows can get very long in schema on read – and decompress pretty much the entire table when it’s time for analysis or other processing.

##### Popular file formats for big data

Now let’s take a deeper look into three popular file formats for big data: Avro, ORC, and Parquet.

——Excerpt From: [Parquet, ORC, and Avro: The File Format Fundamentals of Big Data](https://www.upsolver.com/blog/the-file-format-fundamentals-of-big-data)。

### MepReduce(MR)

MepReduce 是一种用于数据处理的编程模型。

#### MapReduce 如何处理数据



## Hive

### UDF

Hive 支持用户自定义函数，通过语法 `ADD JAR` 加载函数实现，通过语法 `CREATE TEMPORARY FUNCTION <函数名> AS <类名>` 绑定函数名。例如：

```hiveql
SET hive.exec.dynamic.partition=TRUE
SET hive.exec.dynamic.partition.mode=nonstrict;
-- ocr格式表加
SET hive.merge.orcfile.stripe.level=FALSE;
-- 小文件合并
SET hive.merge.mapfiles=TRUE;
SET hive.merge.mapredfiles=TRUE;
SET hive.merge.smallfiles.avgsize=64000000;
SET hive.merge.size.per.task=256000000;
SET hive.merge.tezfiles=TRUE;
SET mapreduce.input.fileinputformat.split.maxsize=256000000;
SET mapreduce.input.fileinputformat.split.minsize=1;
SET mapreduce.input.fileinputformat.split.minsize.per.node=128000000;
SET mapreduce.input.fileinputformat.split.minsize.per.rack=128000000;
-- 其他设置
SET hive.map.aggr=TRUE;
SET hive.exec.parallel=TRUE;
SET hive.exec.parallel.thread.number=256;
SET hive.exec.reducers.max=2000;
SET mapreduce.job.running.map.limit=256;
SET mapreduce.job.running.reduce.limit=256;
SET mapreduce.job.reduces=2000;
SET mapreduce.map.memory.mb=40960;
SET mapreduce.map.java.opts=-Xmx37000m;
SET mapreduce.reduce.memory.mb=40960;
SET mapreduce.reduce.java.opts=-Xmx37000m;
SET yarn.app.mapreduce.am.resource.mb=6096;
SET yarn.app.mapreduce.am.command-opts=-Xmx5200m;

ADD jar dgs://path/to/hive/behavior_UDF-1.0-SNAPSHOT.jar;
CREATE TEMPORARY FUNCTION hc AS 'com.credit.app.udf.HashCode';
CREATE TEMPORARY FUNCTION isNumber AS 'com.credit.app.udf.JudgeNum';

INSERT overwrite TABLE loan_data_warehouse.dwd_loan_track_statistic_metric_di_1 PARTITION (dt = '{DATE-1}',stat_code)
SELECT hc('did="', t.did, '"&&statCode=', t.stat_code) AS stat_hash,
       NULL AS rm_dupl_stat_hash,
       NULL AS global_uni_key,
       t.create_time AS create_time,
       NULL AS insert_time,
       NULL AS process_numeral_field,
       NULL AS process_string_field,
       t.stat_code AS stat_code
FROM (
    SELECT
        distinct_id did,
        concat(unix_timestamp(time), '000') AS create_time,
        CASE WHEN ch in ('umoneyappnew','umoneyapp','wallet') then 1023
          else 1024
        end as stat_code
    from mdw_dwd.dwd_flw_sensors_events_back_dd
    where ds = '{DATE-1}'
     AND distinct_id IS NOT NULL
     AND length(distinct_id) = 14
     AND substring(distinct_id, 1, 1) NOT IN ('0','1','-')
     AND isNumber(distinct_id)
     AND event_key in ('jxj001_apply_userguide_click','jxj001_operation_activity_click')
     AND from_unixtime(unix_timestamp(time), 'yyyyMMdd') = '{DATE-1}'
    UNION ALL
    SELECT
        did,
        concat(unix_timestamp(optime),'000') AS create_time,
        IF(event_key = 'xloan_repay_customizedrepaycalculate',1025,1026) AS stat_code
    from mdw_dwd.dwd_flw_track_event_dd
    where ds = '{DATE-1}'
        AND did is not null
        AND length(did) = 14
        AND substring(did, 1, 1) not in ('0', '1', '-')
        AND isNumber(did)
        AND event_key in ('xloan_repay_customizedrepaycalculate','xloan_repay_overdueprecalculate')
        AND from_unixtime(unix_timestamp(optime),'yyyyMMdd') = '{DATE-1}'
) t;
```

```java
package com.credit.app.udf;

import org.apache.hadoop.hive.ql.exec.UDF;

public class JudgeNum extends UDF
{
  public boolean evaluate(String s) {
    if (s.startsWith("-")) {
      s = s.substring(1);
    }
    char[] ch = s.toCharArray();
    int l = ch.length;
    if (l == 1 && Integer.valueOf(s).intValue() == 0) return true; 
    for (int i = 0; i < l; i++) {
      if (i == 0 && ch[i] == '0') return false; 
      if (!Character.isDigit(ch[i])) return false; 
    } 
    return true;
  }
}
```

函数加载和绑定语句，可以和普通 Hive SQL 放在一起执行。这样，制作 UDF 就非常方便，不需要另起一个程序上下文环境（当然，UDF 本身还是需要在 Java 环境中实现）。

## HBase

## Presto

## Spark

Apache Spark™ is a multi-language engine for executing data engineering, data science, and machine learning on single-node machines or clusters.

Hadoop（主要指MapReduce）的优势在于批处理（Batch Processing），但是在交互式分析（Interactive SQL）和迭代处理（Iterative Processing）方便表现不佳。Spark 主要就是为了解决这两方面的问题而设计。

Spark Core 类似于 MR，其提供了类似 MR-API 的底层 API 称作 Dataset-API（旧称 RDD-API），基于 Dataset-API 封装了 Java、Python 等高级编程语言接口。

Spark SQL 是一个类似 SQL-on-Hadoop 的 SQL-on-Spark 的模块，提供对结构化数据的 SQL 形式的访问能力。Dataset-API 也可以访问结构化数据（并不推荐这种方式），这时候，Dataset-API 称作 DataFrame-API。

Spark 的数据源可以是本地文件（不常用）、HDFS、Hive、HBase，以及其它一些不同来源不同格式（比如 JSON、非结构化文本等）的数据，理论上只要有新的数据源，为其编写一个 Connector，就可以被 Spark 支持。

![](cluster-computing-spark-role.png)

**选型原则**

—— CLI/spark-sql.sh - 重SQL也即结构化数据处理（如果有需要处理少量非结构化数据，则应独立前置预处理好）

—— Java-Program/Python-Program - 半结构化或非结构化数据处理（可结合少量SQL也即结构化数据处理）

例外情况：如果是重SQL任务，但涉及UDF，则只能通过Python-API或Java-API的方式做。

### 运行时架构

![](cluster-computing-spark-overview.png)

Spark 基于 M/S（也就是主/从） 架构，在集群中，有一个中央驱动节点（Driver Node）负责中央协调，调度其它各个分布式工作节点（Worker Node）进行计算。节点也是独立运行的 Java 进程。

驱动节点（上运行的驱动程序，也即 Java 进程）通过 SparkContext（你的程序创建出来的和 Spark 交互的重要对象）协调其它节点（上运行的驱动程序，也即 Java 进程）工作，主要步骤如下：

一、首先通过资源管理器（比如 YARN）申请资源；

二、然后申请工作节点上的执行器（Executor，其上运行的驱动程序，也即 Java 进程）；

三、再把主任务（程序所代表的的任务）拆转化为多个子任务（a DAG of tasks），并把子任务分发给各个执行器执行，执行结果会返回给驱动节点。

在 M/S 架构中，还有一个客户端角色需要关注，客户端通过 `spark-submit` 命令提交任务给 Spark 并等待处理结果（驱动节点会返回处理结果给客户端）。

## ZooKeeper

ZooKeeper(ZK) 是一种分布式过程协同（协作和同步）技术。

我们先看分布式系统的定义，分布式系统是同时跨越多个物理主机，独立运行的多个软件组件（或操作系统的进程）所组成的系统。

**多线程**运行方式可以充分利用现代处理器的多核处理能力，即并发处理能力，类似多线程，分布式的目的是扩展单物理主机的限制，利用多物理主机也即**多进程**的处理能力，即并行处理能力。

无论是并发还是并行，都需要**协同**。多线程通过加锁等[同步原语](../JavaSE/Java/Concurrency.md)来实现线程间同步，多进程则通过 ZK 实现进程间同步。

ZK 通过共享存储模型（分布式系统中的进程通信有两种选择：直接通过网络进行信息交换，或读写某些共享存储）来实现进程间的协作和同步。

### 主/从（M/S）架构应用

主/从架构是分布式系统的典型架构，众多分布式系统都是主/从架构，比如 HBase。在这种架构中，主节点（或进程，后续不再区分节点和进程的概念）负责跟踪从节点的状态，并分配任务给从节点。

主/从架构需要解决三个关键问题：

**一、主节点故障**

如果当前主节点故障，新任务无法分配，失败任务无法重新分配。系统需要选举一个新的主节点（选主）——判断哪些从节点有效，并判断一个从节点的状态相对于系统其它从节点是否时效。

注：当系统中两个或多个主节点开始同时工作（由于各种原因导致），导致整体行为的不一致，这种问题称为脑裂（split-brain）。

**二、从节点故障**

主节点需要检测从节点是否故障（崩溃检测），并把故障节点的尚未完成的任务，重新分配给其它有效从节点。

**三、主从通信故障**

如果主从通信断开，主节点需要重新分配任务，但这样存在同一个任务被执行两次的可能性。

另外，ZK 负责整个系统的元数据（即协同数据）管理。

#### 主要角色

一个主/从架构主要包括三个角色：

**主节点**

主节点负责监听新的从节点和任务，分配任务给可用的从节点。

**从节点**

从节点需要注册自己，以及监听新任务。

**客户端**

客户端创建新任务并等待系统的响应。

再次说明，所有协同需要用到的的元数据信息都存储在 ZK 系统里。

### FLP 和 CAP 定律

CAP 理论指的是在一个分布式系统中，Consistency（一致性）、Availability（可用性）、Partition Tolerance（分区容错性）这三个基本要求不能同时满足，最多只能同时满足其中的两项。

ZK 设计为尽量满足一致性和可用性。在发生分区错误的时候，ZK 也可以转为只读模式（相当于牺牲了可用性）。

其实，早在1985年，FLP 定律就指出在异步通信（与同步通信不同，异步通信没有时钟，不能时间同步，不能使用超时，不能探测失败，消息可以任意延迟或乱序）场景中，即使只有一个进程失败，也没有任何算法能够保证非失败进程达到一致性。在这样的系统模型下，如果一个节点的进程停止工作，其他节点并不会立即知晓，它们可能会认为是消息延迟或该进程特别慢，因此仍然会尝试读取消息。然而，由于节点故障的存在，无法确保所有非失败进程能够在有限时间内达成一致。因此，FLP定理实际上告诉人们，不要浪费时间去为异步通信的分布式系统设计在任意场景下都能实现共识的算法。

## Kafka


