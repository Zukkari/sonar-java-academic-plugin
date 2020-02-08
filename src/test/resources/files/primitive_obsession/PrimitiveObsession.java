package com.example.test;

public class PrimitiveObsession {
    private External external; // Noncompliant: {{Primitive obsession: externally declared class used 4 times with max allowed 3}}

    public void m1(String name) {
        external.print(name);
    }

    public void m2(External2 e2) {
        external.print(e2);
    }

    void m3() {
        System.out.println(external.toString());
    }

    External m4() {
        return external;
    }
}
