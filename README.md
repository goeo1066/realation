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