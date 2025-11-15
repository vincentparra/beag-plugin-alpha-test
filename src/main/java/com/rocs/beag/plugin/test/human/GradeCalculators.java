package com.rocs.beag.plugin.test.human;

import java.util.Scanner;

public class GradeCalculators {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("Enter grade for Math:");
        int math = input.nextInt();
        System.out.println("Enter grade for English:");
        int english = input.nextInt();
        System.out.println("Enter grade for Science:");
        int science = input.nextInt();
        System.out.println("Enter grade for Filipino:");
        int filipino = input.nextInt();

        int total = math + english + science + filipino;

        int average = total/4;

        System.out.println("Total:"+total);
        System.out.println("Average:"+average);
    }
}
