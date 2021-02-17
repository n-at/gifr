package ru.doublebyte.gifr.utils;

public class FileNameUtils {

    public static String escape(String name) {
        return name
                .replaceAll("\"", "\\\"")
                .replaceAll("\n", "\\\n");
    }

}
