package ru.gooamoko.ftpclient.utils;

import java.util.Collection;

public class ApplicationUtils {

    private ApplicationUtils(){
        // Only static methods used
    }

    public static boolean isEmpty(Collection<?> source) {
        return source == null || source.isEmpty();
    }

}
