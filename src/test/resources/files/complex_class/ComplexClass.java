package com.example.test;

public class ComplexClass { // Noncompliant {{Complex class: class complexity 56 is higher than configured: 31.25}}
    public void m1() {
        if (1 > 2) {
            if (2 < 3) {
                while (4 < 3) {
                    for (int i = 0; i < 987; i++) {
                        switch (1) {
                            case 1:
                                if (2 > 4) {
                                    while (3 > 2) {
                                        System.out.println("Hello, world!");
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    public void m2() {
        if (1 > 2) {
            if (2 < 3) {
                while (4 < 3) {
                    for (int i = 0; i < 987; i++) {
                        switch (1) {
                            case 1:
                                if (2 > 4) {
                                    while (3 > 2) {
                                        System.out.println("Hello, world!");
                                    }
                                }
                        }
                    }
                }
            }
        }
    }
}
