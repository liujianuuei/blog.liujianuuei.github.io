# 数据质量或治理

本文侧重于讨论离线数据质量和治理。

## 数据质量标准

![](dw-batch-quality-standards.png)

## 数据质量全景

![](dw-batch-quality-arch-overview.png)

## 数据质量实现

### 开发流程规范

规范化的开发流程是保证数据质量的第一步。具体不详述。

### 数据线上监控

数据任务上线后的监控是保证数据质量的第二步。

**就地监控——数据加工引擎**

数据在加工的时候进行及时监控（就地监控）和拦截是保证质量的主要方式。实现如下：

![](dw-batch-quality-self-impl-arch.png)

![](dw-batch-quality-self-impl-features.png)

针对实时数据，也需要应用类似的前（①过滤②脏数据分流）后（①结果检查）置逻辑，不同的是操作对象是无边界的数据流。

注：本文不提供针对实时数据的监控框架，加工引擎只适用于离线加工，但针对实时数据原理相同。

**质量中心——离线监控工具**

除了就地监控之外，作为第三方的集中式质量中兴的作用也是很有必要的。实现如下：

![](dw-batch-quality-corndog-impl-arch.png)

其中核心点在于：①实体的抽象②监控指标的设计③规则表达式。

![](dw-batch-quality-corndog-impl-details.png)

**质量中心——实时监控工具**

/watchdog/corndog/woof

## 模型优化/稳定性

——模型加工查询效率低下——考虑切换计算引擎
——————————————资源或队列隔离
——————————————考虑水平分表——by常用where条件
——————————————考虑垂直分表——适用表比较稀疏（大量的空值和0值）的场景


注：外表的insert需搭配hivedgs.insert_existing_partitions_behavior='OVERWRITE'使用，设置该参数则分区级覆盖，不设置该参数，则新增；外表禁止使用delete操作。



