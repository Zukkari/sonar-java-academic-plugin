package com.example.test;

public class GodClass { // Noncompliant {{God class: access to foreign data too high and class cohesion is low and method complexity is low}}
    private ServiceA serviceA = new ServiceA();

    void main() {
        String x = serviceA.attribute;
        if (x.length() > 0) {
            int y = serviceA.getAttribute2();
            if (y < 3) {
                System.out.println(x + y);
            } else {
                String z = new ServiceB().attribute1;
                if (z.length() == 2) {
                    byte[] data = new ServiceB().getAttribute2();
                    if (data.length > 17) {
                        char c = ServiceB.attribute3;
                        if (c == 2) {
                            System.out.println(z + data + c);
                        }
                    }
                }
            }
        }
    }

    String exec() {
        return "Hello, world!";
    }
}

class ServiceA {
    String attribute;
    private int attribute2;

    public int getAttribute2() {
        return attribute2;
    }
}

class ServiceB {
    String attribute1;
    private byte[] attribute2;
    public static char attribute3;

    public byte[] getAttribute2() {
        return attribute2;
    }
}
