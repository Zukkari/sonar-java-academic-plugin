public class BrainMethod {
    private boolean b1;
    private boolean b2;
    private boolean b3;
    private boolean b4;

    public void m1() { // Noncompliant: {{Brain method}}
        if (b1) {
            if (b2) {
                if (b3) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println(b4);
                }
            }
        }
    }

    public void m1() {
        System.out.println("Hello, world!");
    }

}
