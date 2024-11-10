package grades;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import system.config;
import courses.CoursesConfig;

public class GradesConfig {
        config conf = new config();
        Scanner sc = new Scanner(System.in);
        CoursesConfig cr = new CoursesConfig();
    // Add grade for a specific course
    public void addGrade() {
        listStudents();
        System.out.print("Student ID: ");
        String studentID = sc.next();
        
        listCourses();
        System.out.print("Course ID: ");
        String courseID = sc.next();
        
        String sql = "INSERT INTO tbl_grades (s_id, c_id) VALUES (?, ?)";
        conf.addRecord(sql, studentID, courseID);
        
        System.out.println("What term do you want to add?\n1.Prelim\t 2.Midterm\n3.Prefinal\t 4.Finals");
        int term = sc.nextInt();
        
        System.out.print("Enter Grade: ");
        double grade = sc.nextDouble();
        sql = "";
        switch(term) {
            case 1: sql = "UPDATE tbl_grades SET g_prelim = ? WHERE s_id = ?  AND c_id = ?"; break;
            case 2: sql = "UPDATE tbl_grades SET g_midterm = ? WHERE s_id = ?  AND c_id = ?"; break;
            case 3: sql = "UPDATE tbl_grades SET g_prefinal = ? WHERE s_id = ?  AND c_id = ?"; break;
            case 4: sql = "UPDATE tbl_grades SET g_finals = ? WHERE s_id = ?  AND c_id = ?"; break;
        }
        conf.addRecord(sql, grade, studentID, courseID);
    }

    // View grades for specific courses with their assigned teachers
    public void viewGrades() throws IOException {
        try {
            cr.viewCourses();
            System.out.print("Select Course ID you want to view: ");
            int cid = sc.nextInt();
            cr.viewCourseGrades(cid);
            
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            sc.nextLine(); 
        }
        String gradesQuery = "SELECT g.g_id, (s.s_fname || ' ' || s.s_lname) AS student_name, "  
                           + "g.g_prelim, g.g_midterm, g.g_prefinal, g.g_finals "
                           + "FROM tbl_grades g "
                           + "JOIN tbl_students s ON g.s_id = s.s_id "
                           + "JOIN tbl_teachers t ON g.t_id = t.t_id "
                           + "JOIN tbl_courses c ON g.c_id = c.c_id";

        String[] gradesHeaders = {"Grade ID", "Student Name", "Prelim", "Midterm", "Prefinal", "Finals"};
        String[] gradesColumns = {"g_id", "student_name",  "g_prelim", "g_midterm", "g_prefinal", "g_finals"};
        
        conf.viewRecords(gradesQuery, gradesHeaders, gradesColumns);
    }

    // Update a specific grade for a term
    public void updateGrade() {
        listStudents();
        System.out.print("Enter Student ID: ");
        String studentID = sc.next();
        listTeachers();
        System.out.print("Enter Teacher ID: ");
        String teacherID = sc.next();
        listCourses();
        System.out.print("Enter Course ID: ");
        String courseID = sc.next();

        System.out.println("Which grade do you want to update?\n1.Prelim\t2.Midterm\n3.Prefinal\t4.Finals");
        int term = sc.nextInt();
        System.out.print("Enter New Grade: ");
        double newGrade = sc.nextDouble();

        String sql = "";
        switch(term) {
            case 1: sql = "UPDATE tbl_grades SET g_prelim = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 2: sql = "UPDATE tbl_grades SET g_midterm = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 3: sql = "UPDATE tbl_grades SET g_prefinal = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 4: sql = "UPDATE tbl_grades SET g_finals = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
        }
        conf.updateRecord(sql, newGrade, studentID, teacherID, courseID);
    }

    // Delete a grade entry
    public void deleteGrade() {
        System.out.print("Enter Grade ID you want to delete: ");
        String gradeID = sc.next();
        
        String sqlDelete = "DELETE FROM tbl_grades WHERE g_id = ?";
        conf.deleteRecord(sqlDelete, gradeID);
    }
    
    // List students with full names
    private void listStudents() {
        String sql = "SELECT s_id, (s_fname || ' ' || s_lname) AS full_name FROM tbl_students";
        String[] headers = {"Student ID", "Full Name"};
        String[] columns = {"s_id", "full_name"};

        conf.viewRecords(sql, headers, columns);
    }

    // List teachers with full names
    private void listTeachers() {
        String sql = "SELECT t_id, (t_fname || ' ' || t_lname) AS full_name FROM tbl_teachers";
        String[] headers = {"Teacher ID", "Full Name"};
        String[] columns = {"t_id", "full_name"};

        conf.viewRecords(sql, headers, columns);
    }

    // List courses with codes
    private void listCourses() {
        String sql = "SELECT c_id, c_code FROM tbl_courses";
        String[] headers = {"Course ID", "Course Code"};
        String[] columns = {"c_id", "c_code"};

        conf.viewRecords(sql, headers, columns);
    }
    
    // View detailed information of a course
    
}
