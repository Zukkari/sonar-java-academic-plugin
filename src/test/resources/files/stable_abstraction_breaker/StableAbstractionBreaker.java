// Instability 0, since 0 dependencies
// Abstraction 0.5
// Distance is 0.75
public abstract class StableAbstractionBreaker { // Noncompliant {{Stable abstraction breaker: distance from main is 0.75 which is greater than 0.5 configured}}

    public abstract void abstactMethod();

    public void concreteMethod1() {
    }

    public String concreteMethod2() {
        return "Hello, world!";
    }
}
