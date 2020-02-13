package com.example.test;

import org.apache.tools.ant.util.optional.JavaxScriptRunner;

import javax.swing.*;

public class PrimitiveObsession {
    private JavaxScriptRunner external; // Noncompliant: {{Primitive obsession: externally declared class used 4 times with max allowed 3}}

    public void m1(String name) throws Exception {
        external.evaluateScript("Hello, world!");
    }

    public void m2(TransferHandler e2) throws Exception {
        external.getManagerName();
    }

    void m3() {
        System.out.println(external.toString());
    }

    JavaxScriptRunner m4() {
        return external;
    }
}
