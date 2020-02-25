package com.example.test;

class UnstableDependencies { // Noncompliant {{Unstable dependencies: the following dependencies are less stable than the class 'com.example.test.UnstableDependencies (instability 0.5)': com.example.test.ServiceB (instability 0.67)}}
    private ServiceA serviceA;
    private ServiceC serviceC;
    private ServiceB serviceB;

    public String m1()  {
        serviceA.m1();
        return serviceB.m2();
    }
}

class ServiceA { // Noncompliant: {{Unstable dependencies: the following dependencies are less stable than the class 'com.example.test.ServiceA (instability 0.33)': com.example.test.UnstableDependencies (instability 0.5)}}
    private UnstableDependencies unstableDependencies;

    void m1() {

    }
}

class ServiceB {
    public ServiceA serviceA;
    private UnstableDependencies unstableDependencies;

    UnstableDependencies m2() {
        return unstableDependencies;
    }
}

class ServiceC {
    private UnstableDependencies unstableDependencies;

    void m4() {
        System.out.println(unstableDependencies.toString());
    }
}
