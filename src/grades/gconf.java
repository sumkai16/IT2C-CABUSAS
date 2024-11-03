package grades;

import java.util.Scanner;
import system.config;

public class gconf {
    private config conf = new config();
    private Scanner sc = new Scanner(System.in);

    public void addGrade() {
        listStudents();
        System.out.print("Student ID: ");
        String studentID = sc.next();
        listTeachers();
        System.out.print("Teacher ID: ");
        String teacherID = sc.next();
        listCourses();
        System.out.print("Course ID: ");
        String courseID = sc.next();
        
        String sql = "INSERT INTO tbl_grades (s_id, t_id, c_id) VALUES (?, ?, ?)";
        conf.addRecord(sql, studentID, teacherID, courseID);
        
        System.out.println("What term do you want to add?\n1.Prelim\t 2.Midterm\n3.Prefinal\t 4.Finals");
        int term = sc.nextInt();
        
        System.out.print("Enter Grade: ");
        double grade = sc.nextDouble();
        sql = "";
        switch(term) {
            case 1: sql = "UPDATE tbl_grades SET g_prelim = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 2: sql = "UPDATE tbl_grades SET g_midterm = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 3: sql = "UPDATE tbl_grades SET g_prefinal = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
            case 4: sql = "UPDATE tbl_grades SET g_finals = ? WHERE s_id = ? AND t_id = ? AND c_id = ?"; break;
        }
        conf.addRecord(sql, grade, studentID, teacherID, courseID);
    }

    public void viewGrades() {
        String gradesQuery = "SELECT * FROM tbl_grades";
        String[] gradesHeaders = {"Grade ID", "Student ID", "Teacher ID", "Course ID", "Prelim", "Midterm", "Prefinal", "Finals"};
        String[] gradesColumns = {"g_id", "s_id", "t_id", "c_id", "g_prelim", "g_midterm", "g_prefinal", "g_finals"};
        conf.viewRecords(gradesQuery, gradesHeaders, gradesColumns);
        
    }

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

    public void deleteGrade() {
        System.out.print("Enter Grade ID you want to delete: ");
        String gradeID = sc.next();
        

        String sqlDelete = "DELETE FROM tbl_grades WHERE g_id = ?";
        conf.deleteRecord(sqlDelete, gradeID);
    }
    
    private void listStudents() {
        String sql = "SELECT s_id, s_fname, s_lname FROM tbl_students";
        String[] headers = {"Student ID", "First Name", "Last Name"};
        String[] columns = {"s_id", "s_fname", "s_lname"};

        conf.viewRecords(sql, headers, columns);
    }

    private void listTeachers() {
        String sql = "SELECT t_id, t_fname, t_lname FROM tbl_teachers";
        String[] headers = {"Teacher ID", "First Name", "Last Name"};
        String[] columns = {"t_id", "t_fname", "t_lname"};

        conf.viewRecords(sql, headers, columns);
    }

    private void listCourses() {
        String sql = "SELECT c_id, c_code FROM tbl_courses";
        String[] headers = {"Course ID", "Course Coide"};
        String[] columns = {"c_id", "c_code"};

        conf.viewRecords(sql, headers, columns);
    }

}
