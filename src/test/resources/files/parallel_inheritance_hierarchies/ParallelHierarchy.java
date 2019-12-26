public class ParallelHierarchy extends D1 { // Noncompliant {{Parallel hierarchy with class: 'ParallelAlternative'}}

}

class ParallelAlternative extends D { // Noncompliant {{Parallel hierarchy with class: 'ParallelHierarchy'}}

}

abstract class A1 {

}


abstract class B1 extends A1 {

}

abstract class C1 extends B1 {

}

abstract class D1 extends C1 {

}


abstract class A {

}

abstract class B extends A {

}

abstract class C extends B {

}

abstract class D extends C {

}
