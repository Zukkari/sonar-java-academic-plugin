package com.example;

class First {
    private int v1;
    private int v2;

    void m2() {
    }

    void m1() {
    }
}

class Second {
    private int v1;

    void m1() {
    }
}

interface X {
    void m1();
}

interface A {
    default void m1() {
    }
}

interface B {
    static void m1() {
    }
}
