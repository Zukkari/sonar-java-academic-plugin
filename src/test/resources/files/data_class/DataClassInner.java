package com.example.test;

public class DataClassInner {
    public static class Inner { // Noncompliant {{Refactor this class so it includes more than just data}}
        private String f1;

        public String getF1() {
            return f1;
        }

        public void setF1(String f1) {
            this.f1 = f1;
        }
    }

    public class InnerNotStatic { // Noncompliant {{Refactor this class so it includes more than just data}}
        private String f2;

        public String getF2() {
            return f2;
        }

        public void setF2(String f2) {
            this.f2 = f2;
        }
    }
}
