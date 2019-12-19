public class RefusedBequest extends ParentClass { // Noncompliant
    public int exec() {
        return 1 + 1;
    }
}

class ParentClass {
    protected void m1() {
        System.out.println("Hello, world!");
    }

    protected void m2() {
        m1();
    }

    protected void m3() {
        m2();
    }

    protected void m4() {
        m3();
    }

    protected void m5() {
        m4();
    }

    protected void m6() {
        m5();
    }

    protected void m7() {
        m6();
    }

    protected void m8() {
        m7();
    }
}
