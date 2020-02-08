package com.example.test;

public class ForLoop {
    public void forLoop() { // Noncompliant
        for (int i = 0; i < 10; i++) {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        }
    }
}
