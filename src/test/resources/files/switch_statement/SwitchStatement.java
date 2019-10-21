class Switch {
    public void exec() {
        System.out.println("Hello!");

        switch (1) {  // Noncompliant
            case 1: return;
        }
    }


    public String switch2() {
        switch (4) { // Noncompliant
            case 1: return "x";
            case 2: return "a";
            default: return "b";
        }
    }
}
