package courses;

import java.util.InputMismatchException;
import java.util.Scanner;
import system.config;

public class enrollCourse {
    Scanner in = new Scanner(System.in);
    config conf = new config();
    CoursesConfig cr = new CoursesConfig();

    public void enrollStudentInCourses() {
        try {
            int courseId = 0;
            System.out.print("Enter Student ID to enroll: ");
            int studentId = in.nextInt();
            in.nextLine(); // Clear buffer after integer input
            
            cr.viewCourses(); // Display available courses to the user
            System.out.print("Enter Course IDs to enroll in (separated by commas): ");
            String courseIdsInput = in.nextLine();
            
            // Split the input by commas and trim any whitespace
            String[] courseIdsArray = courseIdsInput.split(",");
            
            // Prepare the SQL statement for batch insert
            String enrollSql = "INSERT INTO tbl_student_courses (student_id, course_id) VALUES (?, ?)";
            
            for (String courseIdStr : courseIdsArray) {
                
                try {
                    courseId = Integer.parseInt(courseIdStr.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid course ID: " + courseIdStr.trim());
                    continue; // Skip invalid course ID and move to the next
                }
                
                // Check if the student is already enrolled in the course
                if (isStudentEnrolledInCourse(studentId, courseId)) {
                    System.out.println("Student is already enrolled in course ID: " + courseId);
                    continue;
                }
                
                // Enroll the student in the course
                conf.addRecord(enrollSql, studentId, courseId);
            }
             System.out.println("Student enrolled in course ID: " + courseId + " successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers for Student ID and Course IDs.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // Helper method to check if a student is already enrolled in a specific course
    private boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String checkEnrollmentSql = "SELECT COUNT(*) FROM tbl_student_courses WHERE student_id = ? AND course_id = ?";
        int count = (int) conf.getSingleValue(checkEnrollmentSql, studentId, courseId);
        return count > 0;
    }
}
