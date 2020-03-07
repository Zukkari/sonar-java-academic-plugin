package com.example.test;

public class LazyClassInstructions { // Noncompliant: {{Lazy class: class contains low complexity methods}}
    public void m1() {
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        if (1 > 2) {
            System.out.println("Hello");
        } else {
            System.out.println("Bye!");
        }
    }
}
