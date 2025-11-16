package com.rocs.beag.plugin.test;

import java.util.*;

public class AIGradeCalculatorWithCodeGen {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Student Grade Calculator");
            System.out.println("2. AI Code Generator");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            int choice = getInt();

            switch (choice) {
                case 1 -> runGradeCalculator();
                case 2 -> runAICodeGenerator();
                case 3 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ----------------------------------------------------------------------
    // STUDENT GRADE CALCULATOR
    // ----------------------------------------------------------------------

    private static void runGradeCalculator() {

        System.out.print("Enter student name: ");
        String name = scanner.nextLine();

        List<Double> grades = new ArrayList<>();

        while (true) {
            System.out.print("Enter grade (or -1 to finish): ");
            double g = getDouble();
            if (g == -1) break;
            grades.add(g);
        }

        if (grades.isEmpty()) {
            System.out.println("No grades entered.");
            return;
        }

        double avg = grades.stream().mapToDouble(a -> a).average().orElse(0);

        System.out.println("\nAverage: " + avg);
        System.out.println("Letter Grade: " + letterGrade(avg));
        System.out.println("AI Remark: " + remark(avg));
    }

    private static String letterGrade(double avg) {
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    private static String remark(double avg) {
        if (avg >= 90) return "Excellent performance! Keep it up.";
        if (avg >= 80) return "Good job! You can aim for A with consistent study.";
        if (avg >= 70) return "Fair. Focus on weak topics.";
        if (avg >= 60) return "Needs improvement. Study more regularly.";
        return "At risk. Seek help and focus on fundamentals.";
    }

    // ----------------------------------------------------------------------
    // AI CODE GENERATOR
    // ----------------------------------------------------------------------

    private static void runAICodeGenerator() {
        System.out.println("\n===== AI CODE GENERATOR =====");
        System.out.println("Type what you want to generate (e.g., 'gpa method', 'student class', 'grade calculator').");
        System.out.println("Type 'exit' to return.\n");

        while (true) {
            System.out.print("AI> ");
            String prompt = scanner.nextLine().toLowerCase();

            if (prompt.equals("exit")) return;

            if (prompt.contains("student class")) {
                System.out.println(generateStudentClass());
            }
            else if (prompt.contains("gpa")) {
                System.out.println(generateGPAMethod());
            }
            else if (prompt.contains("letter grade")) {
                System.out.println(generateLetterGradeMethod());
            }
            else if (prompt.contains("grade calculator")) {
                System.out.println(generateGradeCalculatorClass());
            }
            else {
                System.out.println("AI does not understand the request.\nTry: 'student class', 'gpa method', 'letter grade method'");
            }
        }
    }

    // ----------------------------------------------------------------------
    // AI Generated Code Templates
    // ----------------------------------------------------------------------

    private static String generateStudentClass() {
        return """
        // AI-GENERATED STUDENT CLASS
        public class Student {
            private String name;
            private double[] grades;

            public Student(String name, double[] grades) {
                this.name = name;
                this.grades = grades;
            }

            public String getName() {
                return name;
            }

            public double[] getGrades() {
                return grades;
            }
        }
        """;
    }

    private static String generateGPAMethod() {
        return """
        // AI-GENERATED GPA METHOD
        public double computeGPA(double[] grades) {
            if (grades.length == 0) return 0;
            double total = 0;
            for (double g : grades) total += g;
            return total / grades.length;
        }
        """;
    }

    private static String generateLetterGradeMethod() {
        return """
        // AI-GENERATED LETTER GRADE METHOD
        public String letterGrade(double score) {
            if (score >= 90) return "A";
            if (score >= 80) return "B";
            if (score >= 70) return "C";
            if (score >= 60) return "D";
            return "F";
        }
        """;
    }

    private static String generateGradeCalculatorClass() {
        return """
        // AI-GENERATED GRADE CALCULATOR CLASS
        public class GradeCalculator {
            public double average(double[] grades) {
                double sum = 0;
                for (double g : grades) sum += g;
                return sum / grades.length;
            }

            public String letter(double avg) {
                if (avg >= 90) return "A";
                if (avg >= 80) return "B";
                if (avg >= 70) return "C";
                if (avg >= 60) return "D";
                return "F";
            }
        }
        """;
    }

    // ----------------------------------------------------------------------
    // INPUT HELPERS
    // ----------------------------------------------------------------------

    private static int getInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid. Try again: ");
            }
        }
    }

    private static double getDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("Invalid. Try again: ");
            }
        }
    }
}
