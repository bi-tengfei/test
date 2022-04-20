package com.doit.wc;

public class Test {

    public static long avg(long a, long b) {
        return (a & b) + ((a ^ b) >> 1);
    }

    public static void main(String[] args) {
        System.out.println(avg(1, 5));
    }
}
