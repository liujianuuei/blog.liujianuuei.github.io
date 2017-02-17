# Persistence

从对象关系角度而言，持久化就相当于对象引用或叫关联。就拿数据库举例，外键关联就是对象引用，从而我们不必在程序（比如 Java 程序）里维护双向的指向关系。比如学生选课这种场景，如果没有持久化，那么我们需要在学生类里保存指向学生所选课程的引用，在课程里保存学生的引用，这样才能保证我们通过学生可以得到他所选的课程，通过课程也可以得到它是被哪个学生所选。如果不保存这种双向的关系，而是只保存一种单向的关系，比如只在课程里保存学生的引用，则当我们需要得到一个学生所选课程的时候，就需要遍历所有课程以得到某个学生所选的那些，这不是我们所想要的，因这回造成额外开销，性能低下。从某种意义上来说，持久化技术就是为这而生的，在数据库里，我们可以在第三张表里实现这种映射关系，这张表我们保存学生的主键和课程的主键，并维护这种对应关系。这样，当我们在程序里需要得到某学生所选课程时，就可以通过 SQL 语言，查询到他所选的那些课程，就像我们通过对象引用得到的一样。更重要的是，持久化层面的对象关联性能经过精心设计和优化。

持久化固然是为了永久保存数据并可以恢复到内存而设计的一种技术，但我们可以通过另一种角度（如上）看待持久化，这就是程序逻辑的角度。接下来的讨论都只限于持久化技术本身，即永久保存数据并恢复。

## JDBC

## ORM

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
 - Transaction management & Locking: Pessimistic, Optimistic, On-Demand. - Test this!
5. [object -> datasbse] Is there a mechanisms for setting defaults for new objects?
6. Is manipulation to raw object thru key-value model allowed: valueForKey, takeValueForKey? I think this should be forbidden.
7. How to apply a filter (e.g. multi-table queries)?
8. How to execute store procedure and function?
9. How to run raw sql?

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
