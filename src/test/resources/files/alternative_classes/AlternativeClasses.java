package com.example.test;

import java.io.PrintStream;
import java.util.concurrent.Callable;

class A { // Noncompliant {{Alternative classes with different classes: similar class 'B'}}
    public void m1(String p1, int p2, byte p3, String[] names) {

    }

    public void m3(String p1, String p2) {

    }
}

class B { // Noncompliant {{Alternative classes with different classes: similar class 'A'}}
    public void different(String p1, int p2, byte p3, String[] names) {

    }

    public void m1(String p1, String p2) {

    }
}

class C {
    public void m3(PrintStream p1, String name, int a) {

    }

    public void m4(System system, int code, byte b) {

    }
}


class D {
    public static void main(String[] args) {
        Callable<String> callable = new Callable<>() {
            @Override
            public Object call() throws Exception {
                return "Hello, world!";
            }
        };
    }
}


abstract class Z { // Compliant, part of hierarchy
    abstract void m1(double p1, int p2, byte p3, String[] names);

    abstract void m3(double p1, double p2);
}

class X extends Z { // Compliant, part of hierarchy
    @Override
    public void m1(double p1, int p2, byte p3, String[] names) {

    }

    @Override
    public void m3(double p1, String p2) {

    }
}

class Y extends Z { // Compliant, part of hierarchy
    @Override
    public void m1(double p1, int p2, byte p3, String[] names) {

    }

    @Override
    public void m3(double p1, String p2) {

    }
}
