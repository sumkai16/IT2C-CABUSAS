package courses;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import system.config;

public class enrollCourse {
    Scanner in = new Scanner(System.in);
    config conf = new config();
    CoursesConfig cr = new CoursesConfig();

    public void enrollStudentInCourses() {
        try {
            System.out.print("Enter Student ID to enroll: ");
            int studentId = in.nextInt();
            in.nextLine();
            
            if (!isStudentIdValid(studentId)) {
                System.out.println("Student ID does not exist. Enrollment failed.");
                return;
            }

            cr.viewCourses();
            System.out.print("Enter Course IDs to enroll in (separated by commas): ");
            String courseIdsInput = in.nextLine();
            String[] courseIdsArray = courseIdsInput.split(",");
            HashSet<Integer> courseIds = new HashSet<>();

            String enrollSql = "INSERT INTO tbl_student_courses (student_id, course_id) VALUES (?, ?)";

            for (String courseIdStr : courseIdsArray) {
                int courseId;
                
                try {
                    courseId = Integer.parseInt(courseIdStr.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid course ID: " + courseIdStr.trim() + " (not a number). Skipping.");
                    continue;
                }

                if (!courseIds.add(courseId)) {
                    System.out.println("Duplicate course ID: " + courseId + " in input. Skipping.");
                    continue;
                }

                if (!isCourseIdValid(courseId)) {
                    System.out.println("Course ID " + courseId + " does not exist. Skipping.");
                    continue;
                }

                if (isStudentEnrolledInCourse(studentId, courseId)) {
                    System.out.println("Student is already enrolled in course ID: " + courseId + ". Skipping.");
                    continue;
                }

                conf.addRecord(enrollSql, studentId, courseId);
                System.out.println("Student successfully enrolled in course ID: " + courseId);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers for Student ID and Course IDs.");
            in.nextLine();
        }
    }

    private boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String checkEnrollmentSql = "SELECT COUNT(*) FROM tbl_student_courses WHERE student_id = ? AND course_id = ?";
        int count = (int) conf.getSingleValue(checkEnrollmentSql, studentId, courseId);
        return count > 0;
    }

    private boolean isStudentIdValid(int studentId) {
        String studentCheckSql = "SELECT COUNT(*) FROM tbl_students WHERE s_id = ?";
        int count = (int) conf.getSingleValue(studentCheckSql, studentId);
        return count > 0;
    }

    private boolean isCourseIdValid(int courseId) {
        String courseCheckSql = "SELECT COUNT(*) FROM tbl_courses WHERE c_id = ?";
        int count = (int) conf.getSingleValue(courseCheckSql, courseId);
        return count > 0;
    }
}
