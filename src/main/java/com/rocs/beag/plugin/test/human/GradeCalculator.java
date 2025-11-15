package com.rocs.beag.plugin.test.human;

public class GradeCalculator {

    public static void main(String[] args) {

        int math, english, science, filipino;

        math = 56;
        english = 78;
        science = 54;
        filipino = 89;

        int total = math + english + science + filipino;

        int average = total/4;

        System.out.println("Total:"+total);
        System.out.println("Average:"+average);
    }
}
