package courses;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import system.config;
import students.StudentsConfig;
import java.sql.*;

public class addGrades {
        StudentsConfig sc = new StudentsConfig();
        Scanner in = new Scanner(System.in);
        config conf = new config();
        CoursesConfig cc = new CoursesConfig();
        
    public void viewGradesMenu() throws IOException {
        while (true) {
           
            System.out.println("\n  View Grades Option");
            System.out.println("----------------------------");
            System.out.println("1. View Grades by Course");
            System.out.println("2. View Grades by Student");
            System.out.println("3. Exit to Main Menu");
            System.out.println("----------------------------");
            System.out.print("Choose an option: ");

            try {
                int choice = in.nextInt();
                in.nextLine(); // Clear input buffer

                switch (choice) {
                    case 1:
                        cc.viewCourses();
                        System.out.print("Enter Course ID: ");
                        int courseId = in.nextInt();
                        in.nextLine();
                        viewCourseGrades(courseId);
                        break;
                    case 2:
                        sc.viewStudents();
                        System.out.print("Enter Student ID: ");
                        int studentId = in.nextInt();
                        in.nextLine();
                        viewStudentGrades(studentId);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select an option from 1 to 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 3.");
                in.nextLine(); // Clear invalid input
            }
        }
    }

    public void viewCourseGrades(int courseId) throws IOException {
        if (!isCourseExists(courseId)) return;

        cc.viewCourseInfo(courseId);

        if (!isCourseEnrolled(courseId)) return;

        String gradesQuery = getSortedGradesQuery(courseId, 2);
        String[] gradesHeaders = {"Student ID", "Student Name", "Prelim Grade", "Midterm Grade", "Prefinal Grade", "Final Grade"};
        String[] gradesColumns = {"s_id", "student_name", "g_prelim", "g_midterm", "g_prefinal", "g_finals"};
        conf.viewRecordsWithParams(gradesQuery, gradesHeaders, gradesColumns, courseId);
    }

    public void viewStudentGrades(int studentId) {
        if (!isStudentExists(studentId)) return;

        // Fetch and display student information
       String studentInfoQuery = "SELECT s_id, s_fname || ' ' || s_lname AS full_name, s_program, s_section FROM tbl_students WHERE s_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(studentInfoQuery)) {

            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("----------------------------");
                    System.out.println("Student Information");
                    System.out.println("----------------------------");
                    System.out.println("Student ID: " + rs.getInt("s_id"));
                    System.out.println("Name: " + rs.getString("full_name"));
                    System.out.println("Program: " + rs.getString("s_program"));
                    System.out.println("Section: " + rs.getString("s_section"));
                    
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching student information: " + e.getMessage());
        }
        String enrollmentCheckQuery = "SELECT COUNT(*) AS course_count FROM tbl_student_courses WHERE student_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(enrollmentCheckQuery)) {

            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt("course_count") == 0) {
                    System.out.println("This student is not enrolled in any courses.");
                    return; // Exit the method early if no courses are found
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking enrollment status: " + e.getMessage());
            return;
        }
        // Fetch and display student grades
        String studentGradesQuery = "SELECT c.c_code, " +
                                    "COALESCE(g.g_prelim, 'N/A') AS g_prelim, " +
                                    "COALESCE(g.g_midterm, 'N/A') AS g_midterm, " +
                                    "COALESCE(g.g_prefinal, 'N/A') AS g_prefinal, " +
                                    "COALESCE(g.g_finals, 'N/A') AS g_finals " +
                                    "FROM tbl_student_courses sc " +
                                    "JOIN tbl_courses c ON sc.course_id = c.c_id " +
                                    "LEFT JOIN tbl_grades g ON sc.student_id = g.s_id AND sc.course_id = g.c_id " +
                                    "WHERE sc.student_id = ?";

        String[] headers = {"Course Code", "Prelim Grade", "Midterm Grade", "Prefinal Grade", "Final Grade"};
        String[] columns = {"c_code", "g_prelim", "g_midterm", "g_prefinal", "g_finals"};
        conf.viewRecordsWithParams(studentGradesQuery, headers, columns, studentId);
    }


    private boolean isCourseExists(int courseId) {
        String courseCheckQuery = "SELECT COUNT(*) FROM tbl_courses WHERE c_id = ?";
        int courseCount = (int) conf.getSingleValue(courseCheckQuery, courseId);
        if (courseCount == 0) {
            System.out.println("Course not found.");
            return false;
        }
        return true;
    }

    private boolean isCourseEnrolled(int courseId) {
        String enrollmentCheckQuery = "SELECT COUNT(*) FROM tbl_student_courses WHERE course_id = ?";
        int enrolledCount = (int) conf.getSingleValue(enrollmentCheckQuery, courseId);
        if (enrolledCount == 0) {
            System.out.println("No students are enrolled in this course.");
            return false;
        }
        return true;
    }

    private boolean isStudentExists(int studentId) {
        String studentCheckQuery = "SELECT COUNT(*) FROM tbl_students WHERE s_id = ?";
        int studentCount = (int) conf.getSingleValue(studentCheckQuery, studentId);
        if (studentCount == 0) {
            System.out.println("Student not found.");
            return false;
        }
        return true;
    }

    void manageGradesMenu(int courseId) {
        while (true) {
            System.out.println("\n--- Grade Management Options ---");
            System.out.println("1. Add Grades");
            System.out.println("2. Edit Grades");
            System.out.println("3. Delete Grades");
            System.out.println("4. Exit to Course List");
            System.out.print("Choose an option: ");

            try {
                int choice = in.nextInt();
                in.nextLine();

                switch (choice) {
                    case 1:
                        inputGrades(courseId);
                        break;
                    case 2:
                        editGrades(courseId);
                        break;
                    case 3:
                        deleteGrades(courseId);
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid choice. Please select an option from 1 to 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                in.nextLine();
            }
        }
    }

    

    private String getSortedGradesQuery(int courseId, int sortOption) {
        String query = "SELECT s.s_id, (s.s_fname || ' ' || s.s_lname) AS student_name, " +
                       "COALESCE(g.g_prelim, 'N/A') AS g_prelim, " +
                       "COALESCE(g.g_midterm, 'N/A') AS g_midterm, " +
                       "COALESCE(g.g_prefinal, 'N/A') AS g_prefinal, " +
                       "COALESCE(g.g_finals, 'N/A') AS g_finals " +
                       "FROM tbl_student_courses sc " +
                       "JOIN tbl_students s ON sc.student_id = s.s_id " +
                       "LEFT JOIN tbl_grades g ON sc.student_id = g.s_id AND sc.course_id = g.c_id " +
                       "WHERE sc.course_id = ? ";

        switch (sortOption) {
            case 1:
                query += "ORDER BY s.s_id";
                break;
            case 2:
                query += "ORDER BY student_name";
                break;
            case 3:
                query += "ORDER BY g.g_finals DESC";
                break;
            default:
                break;
        }
        return query;
    }


    void inputGrades(int courseId) {
        int studentId = getStudentIdForGradeAction(courseId);
        if (studentId == -1) return;

        double prelim = getValidGrade("Prelim");
        double midterm = getValidGrade("Midterm");
        double prefinal = getValidGrade("Prefinal");
        double finals = getValidGrade("Final");

        String sqlInsert = "INSERT INTO tbl_grades (s_id, c_id, g_prelim, g_midterm, g_prefinal, g_finals) VALUES (?, ?, ?, ?, ?, ?) "
                           + "ON CONFLICT (s_id, c_id) DO UPDATE SET "
                           + "g_prelim = EXCLUDED.g_prelim, "
                           + "g_midterm = EXCLUDED.g_midterm, "
                           + "g_prefinal = EXCLUDED.g_prefinal, "
                           + "g_finals = EXCLUDED.g_finals";

        conf.addRecord(sqlInsert, studentId, courseId, prelim, midterm, prefinal, finals);
        System.out.println("Grades entered successfully.");
    }

    private void editGrades(int courseId) {
        int studentId = getStudentIdForGradeAction(courseId);
        if (studentId == -1) return;

        double prelim = getValidGrade("Prelim");
        double midterm = getValidGrade("Midterm");
        double prefinal = getValidGrade("Prefinal");
        double finals = getValidGrade("Final");

        String sqlUpdate = "UPDATE tbl_grades SET g_prelim = ?, g_midterm = ?, g_prefinal = ?, g_finals = ? " +
                           "WHERE s_id = ? AND c_id = ?";

        conf.addRecord(sqlUpdate, prelim, midterm, prefinal, finals, studentId, courseId);
        System.out.println("Grades updated successfully.");
    }

    private void deleteGrades(int courseId) {
        int studentId = getStudentIdForGradeAction(courseId);
        if (studentId == -1) return;

        String sqlDelete = "DELETE FROM tbl_grades WHERE s_id = ? AND c_id = ?";
        conf.addRecord(sqlDelete, studentId, courseId);
        System.out.println("Grades deleted successfully.");
    }

    private int getStudentIdForGradeAction(int courseId) {
        sc.viewStudents();
        System.out.print("Enter Student ID: ");
        int studentId = in.nextInt();
        in.nextLine();

        if (!isStudentEnrolledInCourse(studentId, courseId)) return -1;
        if (isFinalGradeAlreadyEntered(studentId, courseId)) return -1;

        return studentId;
    }

    private boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String enrollmentCheckQuery = "SELECT COUNT(*) FROM tbl_student_courses WHERE student_id = ? AND course_id = ?";
        int enrollmentCount = (int) conf.getSingleValue(enrollmentCheckQuery, studentId, courseId);
        if (enrollmentCount == 0) {
            System.out.println("The student is not enrolled in this course.");
            return false;
        }
        return true;
    }

    private boolean isFinalGradeAlreadyEntered(int studentId, int courseId) {
        String finalGradeCheckQuery = "SELECT COUNT(*) FROM tbl_grades WHERE s_id = ? AND c_id = ? AND g_finals IS NOT NULL";
        int finalGradeCount = (int) conf.getSingleValue(finalGradeCheckQuery, studentId, courseId);
        if (finalGradeCount > 0) {
            System.out.println("Final grade has already been entered for this student in this course. Updates or deletion are not allowed.");
            return true;
        }
        return false;
    }

    private double getValidGrade(String gradeType) {
        double grade;
        while (true) {
            System.out.print("Enter " + gradeType + " Grade (1.0 to 5.0): ");
            try {
                grade = in.nextDouble();
                if (grade >= 1.0 && grade <= 5.0 && (Math.round(grade * 10) / 10.0 == grade)) {
                    break;
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
    
    
}