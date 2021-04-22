package au.edu.federation.itech3107.fedunimillionaire30360914.utils;

public class MyString {

    public static String capitalise(java.lang.String text) {
        if (text != null && !text.isEmpty()) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }
}
