# JMS

## 消息模型

**Provider**

Apache ActiveMQ

**Domains**

- P2P: Queue(Destination), Sender/Receiver
- Pub/Sub: Topic(Destination), Publiser/Subscriber, Listner/onMessage

**同步消息接收模型**

**异步消息接收模型**

JMS Delivery Mode : PERSISTENT NON_PERSISTENT

## 事务性

Session 可以被指定为事务性的，也就是将发送的消息分组到一个班事务单元里，通过 commit() 和 rollback() 来提交事务或回滚事务。

## 消息确认

+ AUTO_ACKNOWLEDGE - 自动确认。对于同步消费者，receive 方法调用返回且没有异常发生时，将自动对收到的消息予以确认；对于异步消息，当 MessageListener.onMessage 方法返回且没有异常发生时，即对收到的消息自动确认。
+ CLIENT_ACKNOWLEDGE - 客户端通过调用消息的 acknowledge 方法来确认消息接收成功。
+ DUPS_OK_ACKNOWLEDGE - 这种确认方式允许 JMS 不必急于确认收到的消息，允许在收到多个消息之后一次完成确认。与AUTO_ACKNOWLEDGE 相比，这种确认方式在某些情况下可能更有效，因为没有确认，当系统崩溃或者网络出现故障的时候，消息可以被重新传递。

## Java Message Service 2.0 / Simplified JMS

// TODO

https://www.open-open.com/doc/34845e442e93474cb12ca92ffaf1eb54.html
