interface A { // Noncompliant {{Speculative generality: provide at least one implementation for this interface}}

}

interface B {

}

class C implements B {

}

interface D {

}

interface F extends D { // Noncompliant {{Speculative generality: provide at least one implementation for this interface}}

}
