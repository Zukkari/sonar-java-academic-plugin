package com.example.test;

class ServiceA {
    public String a;
    public String b;

    private int x;
    private int y;

    public void m1() {
    }

    public String m2() {
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class A {
    private ServiceA serviceA = new ServiceA();

    public void template() { // Noncompliant: {{Missing template method: similar to method(s): B#template}}
        String a = serviceA.a;
        String b = serviceA.b;

        int x = serviceA.getX();
        int y = serviceA.getY();

        String m2 = serviceA.m2();

        System.out.println(a + b + x + y + m2);
    }

}

class B {

    public void template() { // Noncompliant: {{Missing template method: similar to method(s): A#template}}
        ServiceA otherServiceName = new ServiceA();

        String a = otherServiceName.a;
        String b = otherServiceName.b;

        int x = otherServiceName.getX();
        int y = otherServiceName.getY();

        String m2 = otherServiceName.m2();

        System.out.println(a + b + x + y + m2);
    }
}
