package com.example.test;

class UnstableDependencies { // Noncompliant {{Unstable dependencies: the following dependencies are less stable than the class 'com.example.test.UnstableDependencies (instability 0.5)': com.example.test.ServiceB (instability 0.67)}}
    private ServiceA serviceA;

    public String m1()  {
        ServiceC serviceC = new ServiceC();
        serviceA.m1();
        return new ServiceB().m2();
    }
}

class ServiceA { // Noncompliant: {{Unstable dependencies: the following dependencies are less stable than the class 'com.example.test.ServiceA (instability 0.33)': com.example.test.UnstableDependencies (instability 0.5)}}
    private UnstableDependencies unstableDependencies;

    void m1() {

    }
}

class ServiceB {
    public ServiceA serviceA;

    UnstableDependencies m2() {
        return new UnstableDependencies();
    }
}

class ServiceC {
    void m4() {
        UnstableDependencies unstableDependencies = new UnstableDependencies();
        System.out.println(unstableDependencies.toString());
    }
}
