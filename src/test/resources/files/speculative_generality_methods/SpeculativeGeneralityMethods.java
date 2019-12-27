public class SpeculativeGeneralityMethods {
    public void m1(String name) { // Noncompliant {{Speculative generality: unused method parameter: 'name'}}
        System.out.println("Hello, world!");
    }

    public int m2(String p1, int p2, int p4) { // Noncompliant {{Speculative generality: unused method parameter: 'p1', 'p4'}}
        return p2;
    }
}
