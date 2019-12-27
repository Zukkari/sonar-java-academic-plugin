interface A { // Noncompliant {{Speculative generality: provide at least one implementation for this interface}}

}

interface B {

}

class C implements B {

}
