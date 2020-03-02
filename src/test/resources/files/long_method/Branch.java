package com.example.test;

public class Branch {

    public void branch() { // Noncompliant
        if (true) {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        } else {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        }
    }

}
