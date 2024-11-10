package courses;

import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import system.config;
import teachers.TeacherConfig;

public class CoursesConfig {
    Scanner in = new Scanner(System.in);
    config conf = new config();
    TeacherConfig teach = new TeacherConfig();

    public void addCourses() {
        System.out.print("Course Code: ");
        String code = in.nextLine();
        System.out.print("Course Description: ");
        String desc = in.nextLine();

        // Check for available teachers before proceeding
        if (!checkAvailableTeachers()) {
            System.out.println("No teachers available. Exiting add course operation.");
            return;
        }

        try {
            System.out.print("Teacher ID to assign: ");
            int teacherId = in.nextInt();

            System.out.print("Course Credits: ");
            int credits = in.nextInt();

            // Insert course details into the database
            String sql = "INSERT INTO tbl_courses (c_code, c_description, t_id, c_credits) VALUES (?, ?, ?, ?)";
            conf.addRecord(sql, code, desc, teacherId, credits);

            System.out.println("Course added successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // View all courses
    public void viewCourses() {
        String coursesQuery = "SELECT c.c_id, c.c_code, (t.t_fname || ' ' || t.t_lname) AS teacher_full_name, "
                            + "COUNT(s.s_id) AS student_count "
                            + "FROM tbl_courses c "
                            + "INNER JOIN tbl_teachers t ON c.t_id = t.t_id "
                            + "LEFT JOIN tbl_students s ON s.s_id = c.c_id "
                            + "GROUP BY c.c_id";

        String[] coursesHeaders = {"Course ID", "Course Code", "Teacher Assigned", "Student Count"};
        String[] coursesColumns = {"c_id", "c_code", "teacher_full_name", "student_count"};

        conf.viewRecords(coursesQuery, coursesHeaders, coursesColumns);
    }

    public void wantView() {
        System.out.print("Do you want to view course information? (yes/no): ");
        String viewChoice = in.next();

        while (viewChoice.equalsIgnoreCase("yes")) {
            System.out.println("1. View Course Grades\n2. View Course Details\n3. Back");
            System.out.print("Enter Choice: ");
            int option = in.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Enter Course ID to view grades: ");
                    int courseIdForGrades = in.nextInt();
                    try {
                        viewCourseGrades(courseIdForGrades);
                    } catch (IOException e) {
                        System.out.println("Error viewing course grades: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter Course ID to view details: ");
                    int courseIdForDetails = in.nextInt();
                    viewCourseInfo(courseIdForDetails);
                    break;

                case 3:
                    System.out.println("Returning to main menu.");
                    return; // Exit method if user selects "Back"

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }

            System.out.print("Do you want to continue viewing courses? (yes/no): ");
            viewChoice = in.next();
        }
        System.out.println("Exiting view operation.");
    }

    public void viewCourseGrades(int courseId) throws IOException {
        // Check if the course exists
        String courseCheckQuery = "SELECT COUNT(*) FROM tbl_courses WHERE c_id = ?";
        int courseCount = (int) conf.getSingleValue(courseCheckQuery, courseId);

        if (courseCount == 0) {
            System.out.println("Course not found.");
            return; // Exit the method if the course doesn't exist
        }

        // View course details before displaying grades
        viewCourseInfo(courseId);

        // Check if students are enrolled in the specific course
        String enrollmentCheckQuery = "SELECT COUNT(*) FROM tbl_student_courses WHERE course_id = ?";
        int enrolledCount = (int) conf.getSingleValue(enrollmentCheckQuery, courseId);

        if (enrolledCount == 0) {
            System.out.println("No students are enrolled in this course.");
            return; // Exit the method if no students are enrolled
        }

        // Adjusted query to retrieve grades and student_id from tbl_grades
        String gradesQuery = "SELECT s.s_id AS student_id, (s.s_fname || ' ' || s.s_lname) AS student_name, "
                             + "g.g_prelim AS prelim, g.g_midterm AS midterm, g.g_prefinal AS prefinal, g.g_finals AS finals "
                             + "FROM tbl_students s "
                             + "INNER JOIN tbl_student_courses sc ON s.s_id = sc.student_id "
                             + "INNER JOIN tbl_grades g ON g.s_id = s.s_id "
                             + "INNER JOIN tbl_courses c ON c.c_id = sc.course_id "
                             + "WHERE sc.course_id = ?";

        String[] gradesHeaders = {"Student ID", "Student Name", "Prelim Grade", "Midterm Grade", "Prefinal Grade", "Final Grade"};
        String[] gradesColumns = {"student_id", "student_name", "prelim", "midterm", "prefinal", "finals"};
        conf.viewRecordsWithParams(gradesQuery, gradesHeaders, gradesColumns, courseId);

        // Ask if the user wants to input or update grades
        System.out.print("Do you want to input or update the grades? (yes/no): ");
        String inputUpdateChoice = in.next();

        if (inputUpdateChoice.equalsIgnoreCase("yes")) {
            inputGrades(courseId); // Call the method to input or update grades
        }
    }


    // Update a course
    public void updateCourses() {
        try {
            System.out.print("Enter Course ID to update: ");
            int courseId = in.nextInt();

            System.out.println("1. Update Course Code\n2. Update Course Description\n3. Update Course Credits");
            System.out.print("Enter Action (1-3): ");
            int action = in.nextInt();

            while (action < 1 || action > 3) {
                System.out.print("Invalid choice. Enter Action (1-3): ");
                action = in.nextInt();
            }

            String sqlUpdate = null;
            Object newValue = null;

            switch (action) {
                case 1: // Course Code
                    System.out.print("Enter New Course Code: ");
                    newValue = in.next();
                    sqlUpdate = "UPDATE tbl_courses SET c_code = ? WHERE c_id = ?";
                    break;
                case 2: // Course Description
                    System.out.print("Enter New Course Description: ");
                    newValue = in.next();
                    sqlUpdate = "UPDATE tbl_courses SET c_description = ? WHERE c_id = ?";
                    break;
                case 3: // Course Credits
                    System.out.print("Enter New Course Credits: ");
                    newValue = in.nextInt();
                    sqlUpdate = "UPDATE tbl_courses SET c_credits = ? WHERE c_id = ?";
                    break;
            }

            conf.updateRecord(sqlUpdate, newValue, courseId);
            System.out.println("Course updated successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // Delete a course
    public void deleteCourses() {
        try {
            System.out.print("Enter Course ID to delete: ");
            int courseId = in.nextInt();

            String sqlDelete = "DELETE FROM tbl_courses WHERE c_id = ?";
            conf.deleteRecord(sqlDelete, courseId);

            System.out.println("Course deleted successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // Check if there are available teachers
    private boolean checkAvailableTeachers() {
        String checkSql = "SELECT COUNT(*) FROM tbl_teachers";
        int count = (int) conf.getSingleValue(checkSql);

        if (count == 0) {
            System.out.println("No Available Teachers.");
            return false;
        } else {
            System.out.println("Available Teachers:");
            String listSql = "SELECT t_id, t_fname, t_lname FROM tbl_teachers";
            String[] headers = {"Teacher ID", "First Name", "Last Name"};
            String[] columns = {"t_id", "t_fname", "t_lname"};
            conf.viewRecords(listSql, headers, columns);
            return true;
        }
    }

    public void viewCourseInfo(int cid) {
        System.out.println("--------------------------------------------------------------------------------");
        try (PreparedStatement state = conf.connectDB().prepareStatement("SELECT * FROM tbl_courses WHERE c_id = ?")) {
            state.setInt(1, cid);

            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    System.out.println(
                                "\nCourse Code:           | " + result.getString("c_code")
                            +   "\nCourse Description:    | " + result.getString("c_description")
                            +   "\nTeacher ID:            | " + result.getInt("t_id"));
                } else {
                    System.out.println("Course ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method to input or update grades in tbl_grades
    public void inputGrades(int courseId) {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = in.nextInt();

            // Validate if the student is enrolled in the specified course
            if (!isStudentEnrolledInCourse(studentId, courseId)) {
                System.out.println("Student is not enrolled in this course.");
                return;
            }

            // Collect and validate grades for prelim, midterm, prefinal, and finals
            double prelim = getValidGrade("Prelim");
            double midterm = getValidGrade("Midterm");
            double prefinal = getValidGrade("Prefinal");
            double finals = getValidGrade("Finals");

            // Check if grades already exist for this student and course
            String checkQuery = "SELECT COUNT(*) FROM tbl_student_courses sc "
                               + "INNER JOIN tbl_courses c ON sc.course_id = c.c_id "
                               + "WHERE sc.student_id = ? AND c.c_id = ?";
            int existingGradeCount = (int) conf.getSingleValue(checkQuery, studentId, courseId);

            if (existingGradeCount > 0) {
                // If grades exist, update the grades for the student
                String updateQuery = "UPDATE tbl_grades SET g_prelim = ?, g_midterm = ?, g_prefinal = ?, g_finals = ? "
                                   + "WHERE s_id = ? AND c_id = ?";
                conf.addRecord(updateQuery, prelim, midterm, prefinal, finals, studentId, courseId);
                System.out.println("Grades updated successfully.");
            } else {
                // If grades do not exist, insert the grades into tbl_grades
                String insertQuery = "INSERT INTO tbl_grades (s_id, c_id, g_prelim, g_midterm, g_prefinal, g_finals) "
                                   + "VALUES (?, ?, ?, ?, ?, ?)";
                conf.addRecord(insertQuery, studentId, courseId, prelim, midterm, prefinal, finals);
                System.out.println("Grades recorded successfully.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            in.nextLine(); // Clear scanner buffer
        }
    }

    // Helper method to check if a student is enrolled in a given course
    private boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String query = "SELECT COUNT(*) FROM tbl_student_courses WHERE student_id = ? AND course_id = ?";
        int enrollmentCount = (int) conf.getSingleValue(query, studentId, courseId);
        return enrollmentCount > 0;
    }

    // Helper method to validate grades
   private double getValidGrade(String gradeType) {
        double grade;
        while (true) {
            System.out.print("Enter " + gradeType + " Grade (1.0 to 5.0): ");
            try {
                grade = in.nextDouble();
                if (grade >= 1.0 && grade <= 5.0) {
                    break; // Grade is valid
                } else {
                    System.out.println("Invalid grade. Please enter a grade between 1.0 and 5.0.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a numeric grade between 1.0 and 5.0.");
                in.nextLine(); // Clear scanner buffer
            }
        }
        return grade;
    }
}
