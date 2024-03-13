# 数据质量或治理

本文侧重于讨论离线数据质量和治理。

## 数据质量标准

![](dw-batch-quality-standards.png)

## 数据质量全景

![](dw-batch-quality-arch-overview.png)

## 数据质量实现

### 数据加工引擎

![](dw-batch-quality-build-arch.png)

![](dw-batch-quality-build-features.png)

### 实时监控工具/watchdog/corndog

### 开发流程规范

略。

## 模型优化/稳定性

——模型加工查询效率低下——考虑切换计算引擎
——————————————资源或队列隔离
——————————————考虑水平分表——by常用where条件
——————————————考虑垂直分表——适用表比较稀疏（大量的空值和0值）的场景


注：外表的insert需搭配hivedgs.insert_existing_partitions_behavior='OVERWRITE'使用，设置该参数则分区级覆盖，不设置该参数，则新增；外表禁止使用delete操作。


