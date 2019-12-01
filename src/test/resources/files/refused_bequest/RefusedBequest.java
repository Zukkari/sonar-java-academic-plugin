public class RefusedBequest extends ParentClass { // Noncompliant
    public int exec() {
        return 1 + 1;
    }
}

class ParentClass {
    protected void print() {
        System.out.println("Hello, world!");
    }
}
