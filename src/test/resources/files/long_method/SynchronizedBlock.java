public class SynchronizedBlock {
    public void synchronizedBlock() { // Noncompliant
        synchronized (this) {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        }
    }
}
