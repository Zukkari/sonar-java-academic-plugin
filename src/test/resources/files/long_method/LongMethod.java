public class LongMethod {

    public void exec() { // Noncompliant
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
        System.out.println("Hello, world!");
    }


    public void branch() { // Noncompliant
        if (true) {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        } else {
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
            System.out.println("Hello, world!");
        }
    }
}
