package courses;

import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import system.config;
import teachers.TeacherConfig;

public class CoursesConfig {
    Scanner in = new Scanner(System.in);
    config conf = new config();
    TeacherConfig teach = new TeacherConfig();

    public void addCourses() {
        System.out.print("Course Code: ");
        String code = in.nextLine().trim();

        if (code.isEmpty()) {
            System.out.println("Course Code cannot be empty.");
            return;
        }

        System.out.print("Course Description: ");
        String desc = in.nextLine().trim();

        if (desc.isEmpty()) {
            System.out.println("Course Description cannot be empty.");
            return;
        }

        if (!checkAvailableTeachers()) {
            System.out.println("No teachers available. Exiting add course operation.");
            return;
        }

        try {
            System.out.print("Teacher ID to assign: ");
            int teacherId = in.nextInt();
            in.nextLine(); // Clear buffer

            if (!isTeacherIdValid(teacherId)) {
                System.out.println("Teacher ID does not exist.");
                return;
            }

            System.out.print("Course Credits: ");
            int credits = in.nextInt();
            in.nextLine(); // Clear buffer

            if (credits < 1 || credits > 10) {
                System.out.println("Course Credits must be between 1 and 10.");
                return;
            }

            String sql = "INSERT INTO tbl_courses (c_code, c_description, t_id, c_credits) VALUES (?, ?, ?, ?)";
            conf.addRecord(sql, code, desc, teacherId, credits);

            System.out.println("Course added successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear buffer
        }
    }

    public void viewCourses() {
        String coursesQuery = "SELECT c.c_id, c.c_code, (t.t_fname || ' ' || t.t_lname) AS teacher_full_name, "
                            + "COUNT(sc.student_id) AS student_count "
                            + "FROM tbl_courses c "
                            + "INNER JOIN tbl_teachers t ON c.t_id = t.t_id "
                            + "LEFT JOIN tbl_student_courses sc ON sc.course_id = c.c_id "
                            + "GROUP BY c.c_id, c.c_code, teacher_full_name";

        String[] coursesHeaders = {"Course ID", "Course Code", "Teacher Assigned", "Student Count"};
        String[] coursesColumns = {"c_id", "c_code", "teacher_full_name", "student_count"};

        conf.viewRecords(coursesQuery, coursesHeaders, coursesColumns);
    }

    public void wantView() {
        System.out.print("Do you want to view course information? (yes/no): ");
        String viewChoice = in.nextLine().trim();

        while (viewChoice.equalsIgnoreCase("yes")) {
            System.out.println("1. View Course Grades\n2. View Course Details\n3. Back");
            System.out.print("Enter Choice: ");
            
            try {
                int option = in.nextInt();
                in.nextLine(); // Clear buffer

                switch (option) {
                    case 1:
                        System.out.print("Enter Course ID to view grades: ");
                        int courseIdForGrades = in.nextInt();
                        in.nextLine(); // Clear buffer
                        viewCourseGrades(courseIdForGrades);
                        break;

                    case 2:
                        System.out.print("Enter Course ID to view details: ");
                        int courseIdForDetails = in.nextInt();
                        in.nextLine(); // Clear buffer
                        viewCourseInfo(courseIdForDetails);
                        break;

                    case 3:
                        System.out.println("Returning to main menu.");
                        return;

                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }

                System.out.print("Do you want to continue viewing courses? (yes/no): ");
                viewChoice = in.nextLine().trim();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                in.nextLine(); // Clear buffer
            } catch (IOException ex) {
                Logger.getLogger(CoursesConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Exiting view operation.");
    }

    // Method to return a query string based on the sorting option
    private String getSortedGradesQuery(int courseId, int sortOption) {
        // Base query to retrieve grades with LEFT JOIN to include all enrolled students
        String baseQuery = "SELECT s.s_id AS student_id, (s.s_fname || ' ' || s.s_lname) AS student_name, "
                         + "g.g_prelim AS prelim, g.g_midterm AS midterm, g.g_prefinal AS prefinal, g.g_finals AS finals "
                         + "FROM tbl_students s "
                         + "INNER JOIN tbl_student_courses sc ON s.s_id = sc.student_id "
                         + "LEFT JOIN tbl_grades g ON g.s_id = s.s_id AND g.c_id = sc.course_id "
                         + "INNER JOIN tbl_courses c ON c.c_id = sc.course_id "
                         + "WHERE sc.course_id = ? ";

        // Append ORDER BY clause based on sortOption
        switch (sortOption) {
            case 1:
                baseQuery += "ORDER BY s.s_id";  // Sort by Student ID
                break;
            case 2:
                baseQuery += "ORDER BY s.s_lname";  // Sort by Last Name
                break;
            case 3:
                baseQuery += "ORDER BY g.g_finals DESC";  // Sort by Highest Final Grade (descending)
                break;
            default:
                // No sorting if the option is invalid
                break;
        }

        return baseQuery;
    }

    public void viewCourseGrades(int courseId) throws IOException {
        String courseCheckQuery = "SELECT COUNT(*) FROM tbl_courses WHERE c_id = ?";
        int courseCount = (int) conf.getSingleValue(courseCheckQuery, courseId);

        if (courseCount == 0) {
            System.out.println("Course not found.");
            return;
        }

        viewCourseInfo(courseId);

        String enrollmentCheckQuery = "SELECT COUNT(*) FROM tbl_student_courses WHERE course_id = ?";
        int enrolledCount = (int) conf.getSingleValue(enrollmentCheckQuery, courseId);

        if (enrolledCount == 0) {
            System.out.println("No students are enrolled in this course.");
            return;
        }

        // Initial query to display unsorted records
        String gradesQuery = getSortedGradesQuery(courseId, 4); // Option 4 represents no sorting
        String[] gradesHeaders = {"Student ID", "Student Name", "Prelim Grade", "Midterm Grade", "Prefinal Grade", "Final Grade"};
        String[] gradesColumns = {"student_id", "student_name", "prelim", "midterm", "prefinal", "finals"};
        conf.viewRecordsWithParams(gradesQuery, gradesHeaders, gradesColumns, courseId);

        // Prompt user to sort after viewing records
        System.out.print("Do you want to sort the student list? (yes/no): ");
        String sortChoice = in.nextLine().trim();

        if (sortChoice.equalsIgnoreCase("yes")) {
            // Ask user for sorting preference
            System.out.println("Choose an option to sort the student list:");
            System.out.println("1. Sort by Student ID");
            System.out.println("2. Sort by Last Name");
            System.out.println("3. Sort by Highest Final Grade");
            System.out.print("Enter your choice (1-3): ");
            int choice = Integer.parseInt(in.nextLine().trim());

            // Get the sorted query based on user choice
            gradesQuery = getSortedGradesQuery(courseId, choice);
            conf.viewRecordsWithParams(gradesQuery, gradesHeaders, gradesColumns, courseId);
        }

        System.out.print("Do you want to input or update the grades? (yes/no): ");
        String inputUpdateChoice = in.nextLine().trim();

        if (inputUpdateChoice.equalsIgnoreCase("yes")) {
            addGrades ac = new addGrades();
            ac.manageGradesMenu(courseId);
        }
    }


    public void updateCourses() {
        try {
            System.out.print("Enter Course ID to update: ");
            int courseId = in.nextInt();
            in.nextLine(); // Clear buffer

            System.out.println("1. Update Course Code\n2. Update Course Description\n3. Update Course Credits\n4. Update Teacher Assigned");
            System.out.print("Enter Action (1-4): ");
            int action = in.nextInt();
            in.nextLine(); // Clear buffer

            while (action < 1 || action > 4) {
                System.out.print("Invalid choice. Enter Action (1-4): ");
                action = in.nextInt();
                in.nextLine(); // Clear buffer
            }

            String sqlUpdate = null;
            Object newValue = null;

            switch (action) {
                case 1:
                    System.out.print("Enter New Course Code: ");
                    newValue = in.nextLine().trim();
                    sqlUpdate = "UPDATE tbl_courses SET c_code = ? WHERE c_id = ?";
                    break;
                case 2:
                    System.out.print("Enter New Course Description: ");
                    newValue = in.nextLine().trim();
                    sqlUpdate = "UPDATE tbl_courses SET c_description = ? WHERE c_id = ?";
                    break;
                case 3:
                    System.out.print("Enter New Course Credits: ");
                    newValue = in.nextInt();
                    in.nextLine(); // Clear buffer
                    sqlUpdate = "UPDATE tbl_courses SET c_credits = ? WHERE c_id = ?";
                    break;
                case 4:
                    // Display available teachers to the user
                    if (!checkAvailableTeachers()) {
                        System.out.println("No teachers available to assign.");
                        return;
                    }

                    System.out.print("Enter New Teacher ID to assign: ");
                    int newTeacherId = in.nextInt();
                    in.nextLine(); // Clear buffer

                    // Check if the new teacher ID is valid
                    if (!isTeacherIdValid(newTeacherId)) {
                        System.out.println("Invalid Teacher ID. Operation aborted.");
                        return;
                    }
                    newValue = newTeacherId;
                    sqlUpdate = "UPDATE tbl_courses SET t_id = ? WHERE c_id = ?";
                    break;
            }

            conf.updateRecord(sqlUpdate, newValue, courseId);
            System.out.println("Course updated successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear buffer
        }
    }


    public void deleteCourses() {
        try {
            System.out.print("Enter Course ID to delete: ");
            int courseId = in.nextInt();
            in.nextLine(); // Clear buffer

            String sqlDelete = "DELETE FROM tbl_courses WHERE c_id = ?";
            conf.deleteRecord(sqlDelete, courseId);

            System.out.println("Course deleted successfully.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            in.nextLine(); // Clear buffer
        }
    }

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
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        String query = "SELECT c.c_code, c.c_description, c.c_credits, t.t_fname, t.t_lname " +
                       "FROM tbl_courses c " +
                       "JOIN tbl_teachers t ON c.t_id = t.t_id " +
                       "WHERE c.c_id = ?";

        try (PreparedStatement state = conf.connectDB().prepareStatement(query)) {
            state.setInt(1, cid);

            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    String teacherFullName = result.getString("t_fname") + " " + result.getString("t_lname");
                    System.out.println(
                            "\nCourse Code:           | " + result.getString("c_code") +
                            "\nCourse Description:    | " + result.getString("c_description") +
                            "\nCredits:               | " + result.getInt("c_credits") +
                            "\nTeacher Name:          | " + teacherFullName
                    );
                } else {
                    System.out.println("Course not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in database operation: " + e.getMessage());
        }
       
    }


    public boolean isTeacherIdValid(int teacherId) {
        String teacherCheckSql = "SELECT COUNT(*) FROM tbl_teachers WHERE t_id = ?";
        int count = (int) conf.getSingleValue(teacherCheckSql, teacherId);
        return count > 0;
    }

}
