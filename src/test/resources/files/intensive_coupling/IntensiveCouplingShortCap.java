class IntensiveCoupling {
    private ServiceA serviceA = new ServiceA();
    private ServiceB serviceB = new ServiceB();

    public void procedure() { // Noncompliant: {{Intensive coupling: method count is higher than short term memory count}}
        if (serviceA != null) {
            if (serviceA != null && serviceB != null) {
                serviceA.m1();
                serviceB.m1();

                serviceA.m2();
                serviceB.m2();

                serviceA.m3();
                serviceB.m3();

                serviceA.m4();
                serviceB.m4();
            }
        }
    }
}

class ServiceA {
    void m1() {
    }

    void m2() {
    }

    void m3() {
    }

    void m4() {
    }
}

class ServiceB {
    void m1() {
    }

    void m2() {
    }

    void m3() {
    }

    void m4() {
    }
}
