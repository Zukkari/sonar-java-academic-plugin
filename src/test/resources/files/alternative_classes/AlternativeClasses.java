import java.io.PrintStream;

class A { // Noncompliant {{Alternative classes with different classes: similar class 'B'}}
    public void m1(String p1, int p2, byte p3, String[] names) {

    }

    public void m3(String p1, String p2) {

    }
}

class B { // Noncompliant {{Alternative classes with different classes: similar class 'A'}}
    public void different(String p1, int p2, byte p3, String[] names) {

    }

    public void m1(String p1, String p2) {

    }
}

class C {
    public void m3(PrintStream p1, String name, int a) {

    }

    public void m4(System system, int code, byte b) {

    }
}
