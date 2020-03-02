package com.example.test;

public class MiddleMan { // Noncompliant {{Middle man: delegation ratio is 0.67 with limit set to 0.5}}
    private Service service;

    public void m1() {
    }

    public String getName() {
        return service.getName();
    }

    public int getAge() {
        return service.getAge();
    }
}

class Service {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
