package com.example.test;

class ShotgunSurgery {
    public static void utilMethod() { // Noncompliant
        System.out.println("Hello, world!");
    }
}


class A {
    public void a() {
        ShotgunSurgery.utilMethod();
    }
}

class B {
    public void b() {
        ShotgunSurgery.utilMethod();
    }
}

class C {
    public void c() {
        ShotgunSurgery.utilMethod();
    }
}
