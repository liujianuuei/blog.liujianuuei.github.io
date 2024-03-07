# 大数据技术

## Hadoop

Generally speaking, Hadoop includes HDFS and MapReduce two components.

### HDFS

HDFS stands for Hadoop Distributed File System. It is a distributed file system which can run on normal machines.

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

MepReduce 是一种编程模型。

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

## ZooKeeper(ZK)

## Kafka


