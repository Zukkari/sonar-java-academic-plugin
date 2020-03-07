package com.example.test;

public class BlobClass { // Noncompliant {{Blob class: cohesion is below threshold: 1.5}}
    private final String v1 = "hello!";
    private final String v2 = "hello!";
    private final String v3 = "hello!";
    private final String v4 = "hello!";
    private final String v5 = "hello!";
    private final String v6 = "hello!";
    private final String v7 = "hello!";
    private final String v8 = "hello!";
    private final String v9 = "hello!";
    private final String v10 = "hello!";
    private final String v11 = "hello!";
    private final String v12 = "hello!";
    private final String v13 = "hello!";

    public void m1() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m2() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m3() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m4() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m5() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m6() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m7() {
        boolean not = v1.equals("world!");
        System.out.println(not);
    }

    public void m8() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m9() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m10() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m11() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m12() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m13() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m14() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m15() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m16() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m17() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m18() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m19() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m20() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m21() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }

    public void m22() {
        boolean not = hello.equals("world!");
        System.out.println(not);
    }
}
