// Noncompliant@+12
// Noncompliant@+16
public class Comments {

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
    }
}
