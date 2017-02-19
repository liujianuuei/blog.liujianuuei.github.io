# Persistence

从对象关系角度而言，持久化就相当于对象引用或叫关联。就拿数据库举例，外键关联就是对象引用，从而我们不必在程序（比如 Java 程序）里维护双向的指向关系。比如学生选课这种场景，如果没有持久化，那么我们需要在学生类里保存指向学生所选课程的引用，在课程里保存学生的引用，这样才能保证我们通过学生可以得到他所选的课程，通过课程也可以得到它是被哪个学生所选。如果不保存这种双向的关系，而是只保存一种单向的关系，比如只在课程里保存学生的引用，则当我们需要得到一个学生所选课程的时候，就需要遍历所有课程以得到某个学生所选的那些，这不是我们所想要的，因这回造成额外开销，性能低下。从某种意义上来说，持久化技术就是为这而生的，在数据库里，我们可以在第三张表里实现这种映射关系，这张表我们保存学生的主键和课程的主键，并维护这种对应关系。这样，当我们在程序里需要得到某学生所选课程时，就可以通过 SQL 语言，查询到他所选的那些课程，就像我们通过对象引用得到的一样。更重要的是，持久化层面的对象关联性能经过精心设计和优化。

持久化固然是为了永久保存数据并可以恢复到内存而设计的一种技术，但我们可以通过另一种角度（如上）看待持久化，这就是程序逻辑的角度。接下来的讨论都只限于持久化技术本身，即永久保存数据并恢复。

## JDBC

## ERM (Entity-Relationship Model)

1. n:1 的 n 是主表，1 是外表，即被引用表，n 包含 1 的主键；
2. n:n 需要第三张表来表示；
3. n:1 的 n 是 source，1 是 destination；
4. 一般而言，n:1 的 1 owns n。

![Entity Relationship Model](theEntityRelationshipModel.jpg)

## ORM (Object-Relational Mapping)

一个 ORM 框架需要解决以下问题，我们可以用下面的问题审视任何一个 ORM 框架：

1. [database <-> object]:
 1. can we map an object to a single table, a subset of a table, or to more than one table (ﬂatten attributes)?
 2. can we map an instance variable to a derived column, such as “price * discount” or “salary * 12”?
 3. how to map an object inheritance hierarchy to database tables?
 4. how to map table primary and foreign keys to objects, ie, relationship mapping? who should be source while who should be destination? do we need to manage **reciprocal relationships**, in which the destination entity of a relationship has a back reference to the source？
 5. how to map n:n? do we have to map the intermediate join table (also known as a correlation table)? EOF allow hide the correlation tables.
 6. how does the data types mapping between database and objects look like (RTF text, image data, and your own custom data types)?
2. [database -> object] How to maintain that a row in the database be associated with only one object in a given context in application? (Your enterprise objects shouldn’t override the equals method. This is because EOF relies on the default implementation to check instance equality rather than value equality.) Test this!
3. [database -> object] What is the strategy of resolution of relationships? Is prefetching relationships allowed?
4. [object -> datasbse] Is there a mechanisms for ensuring that the integrity of data is maintained between application and the database?
 - Validation: validateForSave, validateForDelete, validateForInsert, validateForUpdate, validateValueForKey, validateXXX, which shoulld be automatically invoked before assigning value to property and saving anything to the database. But in most cases, this validation mechanism is not useful as validation always happens on UI and document parsing moment. This also raises a question what is a good validation mechanism? How to avoid duplicate validation?
 - Referential integrity enforcement: If can specify whether a to-one relationship is optional or mandatory? If can specify delete rules(cascade, nullify, deny) for relationships?
 - Automatic primary and foreign key generation
 - Transaction management & Locking mechanisms: Pessimistic, Optimistic, On-Demand. - Test this!
5. [object -> datasbse] Is there a mechanisms for setting defaults for new objects?
6. Is manipulation to raw object thru key-value model allowed: valueForKey, takeValueForKey? I think this should be forbidden.
7. How to apply a filter (e.g. multi-table queries)? How can i controll the number of objects fetched?
8. How to execute store procedure and function?
9. How to run raw sql?
10. Is object caching supportted?
11. How it manages database connections and how we can customize the process, e.g. limit the number of connections?
12. Is cross-database supportted, e.g. make different models for each database, and then you can create relationships from an entity in one database to an entity in another? Is a two-phase commit implemented?

### Locking

The ORM Framework should offer three types of locking:

- *Pessimistic.* With this strategy, Framework uses your database server’s native locking mechanism to lock rows as they’re fetched into your application. If you try to fetch an object that someone else has already fetched, the operation will fail because the corresponding database row is locked. This approach prevents update conﬂicts by never allowing two users to look at the same object at the same time.

- *Optimistic.* With this strategy, update conﬂicts aren’t detected until you try to save an object’s changes to the database. At this point, the Framework checks the database row to see if it’s changed since your object was fetched. If the row has been changed, it aborts the save operation.

   Framework determines that a database row has changed since its corresponding object was fetched using a technique called snapshotting. When the Framework fetches an object from the database, it records a snapshot of the state of the corresponding database row. When changes to an object are saved to the database, the snapshot is compared with the corresponding database row to ensure that the row data hasn’t changed since the object was last fetched.

- *On-Demand.* This approach is a mixture of the pessimistic and optimistic strategies. With on-demand locking, you lock an object after you fetch it but before you attempt to modify it. When you try to get a lock on the object, it can fail for one of two reasons: the corresponding database row has changed since you fetched the object (optimistic locking), or because someone else already has a lock on the row (pessimistic locking).

### Transactions

#### Transactions and Optimistic Locking

If you’re using optimistic locking (the default) and you’re just fetching objects, framework never explicitly starts or stops transactions. Instead, when a SELECT is performed on a database row, opening (and subsequently closing) a transaction is typically handled by the database server itself, implicitly. Ultimately, it is the responsibility of the adaptor for each database server to ensure that the right thing happens.

Under optimistic locking, framework explicitly starts a transaction when you perform a save operation. A save operation consists of three basic parts:

- Beginning a transaction
- Performing the speciﬁed operations (including checking 
snapshots)
- Committing the transaction, or rolling back if the transaction fails. In either case, the transaction is closed.

#### Transactions and Pessimistic Locking

When you use pessimistic locking, framework explicitly starts a transaction as soon as you fetch objects, and every object you fetch is locked. The transaction stays open until you commit it, or roll it back.

Consequently, using pessimistic locking is very expensive. It’s not suitable for applications that have user interaction since large portions of your database could be locked down for indeterminate periods of time. A good alternative to pessimistic locking is using on-demand locking to lock individual objects.

#### Transactions and On-Demand Locking

When you use on-demand locking to get a server lock on an object, framework explicitly opens a transaction and keeps it open as long as you have a lock on the object. The transaction stays open until you commit it, or roll it back.

## JPA

http://www.oracle.com/technetwork/java/javaee/tech/persistence-jsp-140049.html

hibernate
mybatis
DBUtils

JDBC layer
Metadata
O-R Mapping 
Ad-hoc queries
Transaction layer
Object management (server cache)
Session management
