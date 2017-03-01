# Validate JPA mapping

## Usage

The visitor class: `Checker`
```java
Checker checker = new JdbcMetadataChecker("jdbc:postgresql://HOST:PORT/DATABASE?user=USERNAME&password=PASSWORD&currentSchema=SCHEMA");
JdbcMetadataChecker.INSTANCE = checker;
```

The `@Entity` classes: `List<Class<?>>`.  
You can use the util class:
```java
List<Class<?>> entities = JpaUtils.getEntities("foo.bar.pack");
```

Run check:
```java
new Processor().accept(entities);
```

## VS Hibernate

If you are using **Hibernate**, you can use the `hibernate.hbm2ddl.auto` *configuration* key with``validate` value.  
But the validation is limited to: table & column name, `@Entity` existing & relationships.  
This library (try) add full support validation: foreign key between entities, `nullable` & `length` constraints, ...

## TODO

In progress support:
* JPA:
	* composite ID: `@IdClass`
	* multi join columns: `@JoinTable(joinColumns, inverseJoinColumns)`
	* unique constraints: ``@Table(uniqueConstraints)`
