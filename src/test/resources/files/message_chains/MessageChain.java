public class MessageChain {
    public void exec() {
        A a = new A();

        String message = a.getMessage(); // Noncompliant
        System.out.println(message);
    }
}

public class A {
    private B b;

    public String getMessage() {
        return b.getMessage(); // Noncompliant
    }
}

public class B {
    private C c;

    public String getMessage() {
        return c.getX(); // Noncompliant
    }
}

public class C {
    private String x;

    public String getX() {
        return loadX(); // Compliant
    }

    public String loadX() {
        return loadZ(); // Compliant
    }

    public String loadZ() {
        return x; // Compliant
    }
}
