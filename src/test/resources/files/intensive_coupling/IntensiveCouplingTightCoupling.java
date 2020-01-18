class IntensiveCoupling {
    private ServiceA serviceA = new ServiceA();

    public void procedure() { // Noncompliant: {{Intensive coupling: too high coupling with external methods}}
        if (serviceA != null) {
            if (serviceA != null && true) {
                serviceA.m1();
                serviceA.m2();
                serviceA.m3();
                serviceA.m4();
                serviceA.m5();
                serviceA.m6();
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

    void m5() {
    }

    void m6() {
    }
}
