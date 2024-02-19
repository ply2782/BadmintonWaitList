package com.jc.javalib;

public class MyClass {
    public static void main(String[] args) {
        String abc = "abcdefgh";
        String hellow = "hellow";
        StringBuilder builder = new StringBuilder();
        builder.append(abc.charAt(0)).append(hellow).append(abc.charAt(7));
        String result = builder.toString();
        System.out.println(result);

    }
}