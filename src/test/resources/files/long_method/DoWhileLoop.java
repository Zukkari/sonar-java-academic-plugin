package com.example.test;

public class DoWhileLoop {
    public void doWhile() { // Noncompliant
        int x = 3;
        do {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        } while (x > 1);
    }
}
