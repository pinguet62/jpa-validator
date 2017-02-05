# Validate JPA mapping

## Usage

The visitor class: `Checker`.  
For this moment only 1 support: PostgreSQL:
```java
Checker checker = new PostgresChecker("jdbc:postgresql://HOST:PORT/DATABASE?user=username&password=PASSWORD&currentSchema=SCHEMA");
```
But you can implement your own `Checker`!

The `@Entity` classes: `List<Class<?>>`.
You can use the util class:
```java
List<Class<?>> entities = new EntityScanner("foo.bar.pack").get();
```

Run check:
```java
new Processor(checker).accept(entities);
```

## TODO

* JPA:
	* composite ID: `@IdClass`
	* multi join columns: `@JoinTable(joinColumns)`
* Validation:
	* Exception: custom
	* Message: explicite
* Support: other databases
* Testing
