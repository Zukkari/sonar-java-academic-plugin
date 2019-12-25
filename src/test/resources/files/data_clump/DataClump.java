public class DataClump { // Noncompliant {{Data clump: similar to class: 'Service'}}
    private String name;
    private int age;
    private char character;
}

class Service { // Noncompliant {{Data clump: similar to class: 'DataClump'}}
    private String name;
    private int age;
    private char character;
}
