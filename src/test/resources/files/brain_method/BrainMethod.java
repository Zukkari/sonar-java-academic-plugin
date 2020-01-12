class BrainMethod {
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

    public void m2() { // Noncompliant: {{Brain method}}
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                for (int k = 0; k < 100; k++) {
                    System.out.println(b1 || b2 || b3 || b4);
                }
            }
        }
    }

    public void m3() { // Noncompliant: {{Brain method}}
        while (b1) {
            while (b2) {
                while (b3) {
                    while (b4) {
                        System.out.println("Hello, world!");
                    }
                }
            }
        }
    }

    public void m4() { // Noncompliant: {{Brain method}}
        try {
            if (b1) {
                if (b2) {
                    if (b3) {
                        System.out.println(b4);
                    }
                }
            }
        } finally {
            System.out.println("OK!");
        }
    }
}
