package courses;

import java.util.Scanner;
import system.config;
import teachers.TeacherConfig;

public class CoursesConfig {
        Scanner in = new Scanner(System.in);
        config conf = new config();
    public void addCourses() {
        TeacherConfig teach = new TeacherConfig();
        String op = null;
        System.out.print("Course Code: ");
        Scanner sc = new Scanner(System.in);
        String code = sc.nextLine();
        System.out.print("Course Description: ");
        String desc = sc.nextLine();
        if (!checkAvailableTeachers()) {
            System.out.println("Do you want to add teacher? (Y/N): ");
            op = in.next();
            if(op.equals("Y") || op.equals("y")){
                teach.addTeachers();
                System.out.println("Available Teachers:");
                String listSql = "SELECT t_id, t_fname, t_lname FROM tbl_teachers";
                String[] headers = {"Teacher ID", "First Name", "Last Name"};
                String[] columns = {"t_id", "t_fname", "t_lname"};
                conf.viewRecords(listSql, headers, columns);
            }
        }
        System.out.print("ID of teacher Assigned: ");
        int tid = sc.nextInt();
        System.out.print("Course Credits: ");
        int cred = sc.nextInt();
        String sql = "INSERT INTO tbl_courses (c_code, c_description, t_id, c_credits)"
                   + " VALUES (?, ?, ?, ?)";
        conf.addRecord(sql, code, desc, tid, cred);
    }
    
    public void viewCourses() {
        
            String coursesQuery = "SELECT c_id, c_code, t.t_lname, c_credits "
                + "FROM tbl_courses "
                + "INNER JOIN tbl_teachers t ON tbl_courses.t_id = t.t_id";
        String[] coursesHeaders = {"Course ID", "Course Code", "Teacher Assigned", "Credits"};
        String[] coursesColumns = {"c_id", "c_code", "t_lname", "c_credits"};       
        conf.viewRecords(coursesQuery, coursesHeaders, coursesColumns);

    }

    public void updateCourses() {
           
        System.out.print("Enter Course ID to update: ");
        int courseId = in.nextInt();

        System.out.println("1. Course Code\t2. Course Description\n3. Course Credits");
        System.out.print("Enter Action (1-3 only): ");
        int action = in.nextInt();
        while (action < 1 || action > 3) {
            System.out.print("Enter Action (1-3 only): ");
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
                conf.updateRecord("UPDATE tbl_courses SET c_credits = ? WHERE c_id = ?", newCred, courseId);
                return;
        }
        
        conf.updateRecord(sqlUpdate, newValue, courseId);
    }
    
    public void deleteCourses() {
        System.out.print("Enter Course ID to delete: ");
        int courseId = in.nextInt();
        
        String sqlDelete = "DELETE FROM tbl_courses WHERE c_id = ?";
        conf.deleteRecord(sqlDelete, courseId);
    }
    
    public boolean checkAvailableTeachers() {
        System.out.print("Do you want to see the available teachers? (Y/N): ");
        String op = in.next();

        if (op.equalsIgnoreCase("Y")) {
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
        return false; 
    }
}
