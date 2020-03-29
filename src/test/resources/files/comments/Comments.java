package com.example.test;

public class Comments { // Noncompliant {{This class contains too many comments 34 versus configured 29.5}}

    /**
     * This is Javadoc.
     * Javadoc is a valid comment
     *
     * @param i parameter
     * @return return value
     */
    public String m1(int i) {
        /*
            Here however we have a block comment
         */
        System.out.println(i);

        // By the way now this is a line comment
        return "Hello, world!";
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
        // Comment
    }
}
