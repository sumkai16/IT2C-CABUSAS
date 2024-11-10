package courses;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.SQLException;
import system.config;

public class addGrades {
    private Scanner in = new Scanner(System.in);
    private config conf = new config();

    public void addGrades() {
        try {
            // Prompt for Student ID and Course ID
            System.out.print("Enter Student ID: ");
            int studentId = in.nextInt();

            System.out.print("Enter Course ID: ");
            int courseId = in.nextInt();

            // Validate if the student is enrolled in the specified course
            if (!isStudentEnrolledInCourse(studentId, courseId)) {
                System.out.println("Student is not enrolled in this course.");
                return;
            }

            // Collect and validate grades for each term
            double prelimGrade = getValidGrade("Prelim");
            double midtermGrade = getValidGrade("Midterm");
            double prefinalGrade = getValidGrade("Prefinal");
            double finalGrade = getValidGrade("Finals");

            // SQL to insert grades into tbl_grades
            String sql = "INSERT INTO tbl_grades (student_id, course_id, prelim, midterm, prefinal, finals) VALUES (?, ?, ?, ?, ?, ?)";

            // Insert the grades into the database
            conf.addRecord(sql, studentId, courseId, prelimGrade, midtermGrade, prefinalGrade, finalGrade);
            System.out.println("Grades added successfully for Student ID: " + studentId + " in Course ID: " + courseId);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter numeric values for IDs and grades.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // Method to validate and retrieve a grade for a specific term
    private double getValidGrade(String gradeType) {
        double grade;
        while (true) {
            System.out.print("Enter " + gradeType + " Grade (1.0 to 5.0): ");
            try {
                grade = in.nextDouble();
                if (grade >= 1.0 && grade <= 5.0 && (Math.round(grade * 10) / 10.0 == grade)) {
                    break; // Grade is valid
                } else {
                    System.out.println("Invalid grade. Please enter a grade between 1.0 and 5.0 with only one decimal place.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric grade with one decimal.");
                in.nextLine(); 
            }
        }
        return grade;
    }

    // Check if the student is enrolled in the specified course
    private boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String checkEnrollmentSql = "SELECT COUNT(*) FROM tbl_student_courses WHERE student_id = ? AND course_id = ?";
        int count = (int) conf.getSingleValue(checkEnrollmentSql, studentId, courseId);
        return count > 0;
    }
}
