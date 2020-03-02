package com.example.test;

public class FeatureEnvy {
    private byte myOwnVariable = 3;

    public String run(ServiceA serviceA) { // Noncompliant {{Feature envy}}
        String varA = serviceA.getVarA();
        int varB = serviceA.getVarB();
        char varC = serviceA.getVarC();
        return varA + varB + varC;
    }

    public String run1(ServiceA serviceA) { // Noncompliant {{Feature envy}}
        return serviceA.getVarA() + serviceA.getVarB() + serviceA.getVarC();
    }

    public String run2(ServiceA serviceA) { // Noncompliant {{Feature envy}}
        String varA = serviceA.varA;
        int varB = serviceA.varB;
        char varC = serviceA.varC;
        byte x = this.myOwnVariable;
        String other = run1(serviceA);
        return varA + varB + varC + x;
    }
}

class ServiceA {
    String varA;
    int varB;
    char varC;

    public String getVarA() {
        return varA;
    }

    public int getVarB() {
        return varB;
    }

    public char getVarC() {
        return varC;
    }
}
