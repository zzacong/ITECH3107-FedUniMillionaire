package au.edu.federation.itech3107.fedunimillionaire30360914.utils;

import android.text.Html;

public class MyString {

    public static String capitalise(String text) {
        if (text != null && !text.isEmpty()) {
            return text.substring(0, 1).toUpperCase() + text.substring(1);
        }
        return text;
    }

    public static String unescape(String text) {
        return Html.fromHtml(text).toString();
    }
}
