package com.github.goeo1066.realation.core;

public class RealationUtils {
    public static String lowerCamelToSnake(String camel, boolean isUpper) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        if (isUpper) {
            return result.toString().toUpperCase();
        }

        return result.toString();
    }

    public static String upperCamelToLowerCamel(String camel) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (i == 0) {
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
