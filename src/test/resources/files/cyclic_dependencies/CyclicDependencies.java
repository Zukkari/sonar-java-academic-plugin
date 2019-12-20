public class CyclicDependecies {
    private A a = new A();

    public A getA() {
        return a;
    }
}


class A {
    private B b = new B();

    public B getB() {
        return b;
    }
}

class B {
    private CyclicDependecies cyclicDependecies = new CyclicDependecies();

    public CyclicDependecies getCyclicDependecies() {
        return cyclicDependecies;
    }
}
