package com.github.goeo1066.realation;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "person_info", schema = "public")
public class PersonInfo {
    @Id
    private Integer idx;
    private String name;
    private Integer age;
    private String address;

    public Integer getIdx() {
        return idx;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "idx=" + idx +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }
}
