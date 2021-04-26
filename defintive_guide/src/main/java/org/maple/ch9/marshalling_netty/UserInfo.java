package org.maple.ch9.marshalling_netty;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = -3714149359883606857L;

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
