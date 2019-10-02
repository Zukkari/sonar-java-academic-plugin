public class DataClassCustomSetter { // Noncompliant {{Refactor this class so it includes more than just data}}
    private String f1;

    public void setF1(String x) {
        f1 = x1;
    }
}
