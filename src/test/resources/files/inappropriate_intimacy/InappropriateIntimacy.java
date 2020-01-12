class A { // Noncompliant {{Inappropriate intimacy: number of method calls 5 with class B is greater than configured 4}}
    private B b = new B();

    public static void print(Object o) {
        System.out.println(o);
    }

    public int plusOne(int i) {
        return B.plusNumber(i, 1);
    }

    public void printPlusTen(int a) {
        int res = B.plusNumber(a, 10);
        b.printNumber(res);
    }
}

class B { // Noncompliant {{Inappropriate intimacy: number of method calls 5 with class A is greater than configured 4}}
    public static int plusNumber(int a, int b) {
        return a + b;
    }

    public void printNumber(int a) {
        A.print(a);
    }

    public void doStuff() {
        new A().printPlusTen(23);
    }
}
