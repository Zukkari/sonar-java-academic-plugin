public class RefusedBequestOverride extends ParentClass { // Noncompliant

    @Override
    protected void m1() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m4() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m5() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m6() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                    m2();
                } else {
                    m3();
                    m4();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                    m3();
                } else {
                    m3();
                    m4();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m7() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m8() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m9() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }

    public void m10() {
        if (1 > 2) {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        } else {
            if (1 > 2) {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            } else {
                if (1 > 2) {
                    m2();
                } else {
                    m3();
                }
            }
        }
    }
}

public class ParentClass {
    protected void m1() {
        System.err.println("Oops!");
    }

    protected void m2() {
        m1();
    }

    protected void m3() {
        m2();
    }

    protected void m4() {
        m3();
    }
}