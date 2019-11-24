public class LazyClassCoupling extends A { // Noncompliant:
    private D d = new D();
    private C c = new C();
    private A a = new A();
    private B b = new B();
}

class A extends B {
    public void m1() {
        if (2 > 2) {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        } else {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        }
    }
}

class B extends C {
    public void m2() {
        if (2 > 2) {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        } else {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        }
    }
}

class C extends D {
    public void m3() {
        if (2 > 2) {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        } else {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        }
    }
}

class D {
    public void m4() {
        if (2 > 2) {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        } else {
            if (2 > 2) {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            } else {
                if (2 > 2) {
                    System.out.println("Hello, world!");
                } else {
                    System.out.println("Hello, world!");
                }
            }
        }
    }
}
