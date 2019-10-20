public class TryBlock {
    public void tryBlock() { // Noncompliant
        try {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        } catch (Exception e) {
            System.err.println("oopsie!");
        }
    }

    public void tryBlock1() { // Noncompliant
        try {
            System.out.println("Hello, world!");
        } catch (Exception e) {
            System.out.println("Hello, world!");
        } finally {
            System.out.println("Hello, world!");
        }
    }

    public void tryBlock2() { // Noncompliant
        try {
            System.out.println("Hello, world!");
        } catch (Exception e) {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        }
    }
}
