package com.example.test;

interface SwissArmyKnife { // Noncompliant {{Swiss army knife: number of methods in interface higher than threshold '5'}}
    void m1();

    void m2();

    default String m3() {
        return "Hello, world!";
    }

    static String m4();

    int m5();

    byte[] m6;
}
