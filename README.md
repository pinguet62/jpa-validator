# Validate JPA mapping

## Usage

The visitor class: `Checker`
```java
Checker checker = new JdbcMetadataChecker("jdbc:postgresql://HOST:PORT/DATABASE?user=USERNAME&password=PASSWORD&currentSchema=SCHEMA");
```

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
