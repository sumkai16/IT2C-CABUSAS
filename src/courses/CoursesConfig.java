package courses;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import system.config;
import teachers.TeacherConfig;

public class CoursesConfig {
    private Scanner in = new Scanner(System.in);
    private config conf = new config();
    private TeacherConfig teach = new TeacherConfig();

    // Add new course
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

        // Assign teacher to the course
        System.out.print("Teacher ID to assign: ");
        int teacherId = in.nextInt();

        System.out.print("Course Credits: ");
        int credits = in.nextInt();
        
        // Insert course details into the database
        String sql = "INSERT INTO tbl_courses (c_code, c_description, t_id, c_credits) VALUES (?, ?, ?, ?)";
        conf.addRecord(sql, code, desc, teacherId, credits);
        
        System.out.println("Course added successfully.");
    }

    // View all courses
    public void viewCourses() {
        String coursesQuery = "SELECT c.c_id, c.c_code, t.t_lname, COUNT(s.s_id) AS student_count "
                            + "FROM tbl_courses c "
                            + "INNER JOIN tbl_teachers t ON c.t_id = t.t_id "
                            + "LEFT JOIN tbl_students s ON s.s_id = c.c_id "  // Assuming students have a `course_id` column
                            + "GROUP BY c.c_id";

        String[] coursesHeaders = {"Course ID", "Course Code", "Teacher Assigned", "Student Count"};
        String[] coursesColumns = {"c_id", "c_code", "t_lname", "student_count"};
        
        conf.viewRecords(coursesQuery, coursesHeaders, coursesColumns);

        System.out.print("Select Course ID you want to view: ");
        int cid = in.nextInt();

        // Display course details and teacher information
        viewCourseDetailsAndTeacherInfo(cid);
    }

    // Merged Method: View course details and teacher information
    public void viewCourseDetailsAndTeacherInfo(int courseId) {
        String courseDetailsQuery = "SELECT c.c_code, c.c_description, t.t_fname, t.t_lname "
                                   + "FROM tbl_courses c "
                                   + "INNER JOIN tbl_teachers t ON c.t_id = t.t_id "
                                   + "WHERE c.c_id = ?";

        String[] courseDetailsHeaders = {"Course Code", "Course Description", "Teacher First Name", "Teacher Last Name"};
        String[] courseDetailsColumns = {"c_code", "c_description", "t_fname", "t_lname"};
        
        conf.viewRecordsWithParams(courseDetailsQuery, courseDetailsHeaders, courseDetailsColumns, courseId);
    }

    // View course grades for the specific course
    public void viewCourseGrades(int courseId) {
        String gradesQuery = "SELECT s.s_fname, s.s_lname, g.prelim_grade, g.midterm_grade, g.prefinal_grade, g.final_grade "
                            + "FROM tbl_students s "
                            + "INNER JOIN tbl_grades g ON s.s_id = g.s_id "
                            + "INNER JOIN tbl_courses c ON c.c_id = g.c_id WHERE c.c_id = ?";
        
        String[] gradesHeaders = {"Student Name", "Prelim Grade", "Midterm Grade", "Prefinal Grade", "Final Grade"};
        String[] gradesColumns = {"s_fname", "s_lname", "prelim_grade", "midterm_grade", "prefinal_grade", "final_grade"};
        
        conf.viewRecordsWithParams(gradesQuery, gradesHeaders, gradesColumns, courseId);
    }

    public void updateCourses() {
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
        String newValue = null;
        
        switch(action) {
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
                int newCred = in.nextInt();
                sqlUpdate = "UPDATE tbl_courses SET c_credits = ? WHERE c_id = ?";
                conf.updateRecord(sqlUpdate, newCred, courseId);
                System.out.println("Course updated successfully.");
                return;
        }

        conf.updateRecord(sqlUpdate, newValue, courseId);
        System.out.println("Course updated successfully.");
    }

    // Delete course
    public void deleteCourses() {
        System.out.print("Enter Course ID to delete: ");
        int courseId = in.nextInt();

        String sqlDelete = "DELETE FROM tbl_courses WHERE c_id = ?";
        conf.deleteRecord(sqlDelete, courseId);

        System.out.println("Course deleted successfully.");
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
}
