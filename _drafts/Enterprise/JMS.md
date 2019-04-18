# JMS

## 消息模型

**Provider**

- Apache ActiveMQ

**Domains**

- P2P:
  - Destination: Queue
  - Producer: Sender
  - Consumer: Receiver
  - Mode: Sync & Async(Listner/onMessage)
- Pub/Sub:
  - Destination: Topic
  - Producer: Publiser
  - Consumer: Subscriber
  - Mode: Sync & Async(Listner/onMessage)

**同步消息接收模型 & 异步消息接收模型**

P2P 模式使用 queue 作为 destination，消息可以被同步或异步的发送和接收，每个消息只会给一个 Consumer 传送一次。

Consumer 可以使用 MessageConsumer.receive() 同步地接收消息，也可以通过使用 MessageConsumer.setMessageListener() 注册一个 MessageListener 实现异步接收。

多个 Consumer 可以注册到同一个 queue 上，但一个消息只能被一个 Consumer 所接收，然后由该 Consumer 来确认消息。并且在这种情况下，Provider 对所有注册的 Consumer 以轮询的方式发送消息。

Pub/Sub 模式使用 topic 作为 destination，发布者向 topic 发送消息，订阅者注册接收来自 topic 的消息。发送到 topic 的任何消息都将自动传递给所有订阅者。接收方式（同步和异步）与 P2P 域相同。

**持久化**

除非显式指定，否则 topic 不会为订阅者保留消息。当然，这可以通过持久化（Durable）订阅来实现消息的保存。这种情况下，当订阅者与 Provider 断开时，Provider 会为它存储消息。当订阅者重新连接时，将会受到所有的断连期间未消费的消息。

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
