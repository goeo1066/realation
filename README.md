## The blueprint of the Objectives

### Select

```java
PersonRelation relation = new PersonRelation<>();
```

or 
```java
Relation<PersonEntity> relation = new RelationImpl<>(PersonEntity.class);
```

and
```java
PersonEntity params = new PersonEntity("John", "30");
Person result = relation
        .select()
        .where((e, b) -> b
                .eq(e.getName(), ":name")
                .eq(e.getAge(), ":age")
                .and(() -> b
                        .eq(e.getName(), ":name")
                        .eq(e.getAge(), ":age"))
                .and(() -> b
                        .eq(e.getName(), relation
                                .select()
                                .where(() -> b
                                        .eq(e.getName(), ":name")
                                        .eq(e.getAge(), ":age")))))
        .orderBy((e, b) -> b
                .asc(e.getAge())
                .desc(e.getName()))
        .paging(1, 10)
        .withParameter(params)
        .querySingle()
;
```

### Update

```java
PersonEntity params = new PersonEntity("John", "30");
List<Person> updated = relation
        .update()
        .set((e, b) -> b
                .set(e.getName(), ":name")
                .set(e.getAge(), ":age"))
        .where((e, b) -> b
                .eq(e.getName(), ":name")
                .eq(e.getAge(), ":age"))
        .withParameter(params)
        .update()
;
```

### Insert

```java
PersonEntity params = new PersonEntity("John", "30");
List<Person> inserted = relation
        .insert()
        .value((e, b) -> b
                .value(e.getName(), ":name")
                .value(e.getAge(), ":age"))
        .withParameter(params)
        .insert()
;
```

### Delete

```java
PersonEntity params = new PersonEntity("John", "30");
List<Person> deleted = relation
        .delete()
        .where((e, b) -> b
                .eq(e.getName(), ":name")
                .eq(e.getAge(), ":age"))
        .withParameter(params)
        .delete()
;
```

### Merge Into

```java
List<PersonEntity> value = new PersonEntity("John", "30");
PersonEntity params = new PersonEntity("John", "30");
List<Person> merged = relation
        .mergeInto()
        .using(value)
        .on((e, b) -> b
                .eq(e.getName(), ":name")
                .eq(e.getAge(), ":age"))
        .whenMatchedThenUpdate((e, b) -> b.all()
                .except(e.getName())
        .whenNotMatchedThenInsert((e, b) -> b
                .all()
                .value(e.getAge(), ":age")
                .except(e.getName()))
        )
        .withParameter(params)
        .merge()
;
```

### Insert On Conflict

```java
PersonEntity params = new PersonEntity("John", "30");
List<Person> inserted = relation
        .insert()
        .value((e, b) -> b
                .value(e.getName(), ":name")
                .value(e.getAge(), ":age"))
        .onConflict((e, b) -> b
                .doNothing())
        .withParameter(params)
        .insert()
;
```