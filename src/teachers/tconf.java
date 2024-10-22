
package teachers;

import java.util.Scanner;
import system.config;

public class tconf {
    public void addTeachers(){
         Scanner sc = new Scanner(System.in);
         config conf = new config();
         
         System.out.print("Teacher First Name: ");
         String fname = sc.next();
         System.out.print("Teacher Last Name: ");
         String lname = sc.next();
         System.out.print("Teacher Sex: ");
         String sex = sc.next();
         System.out.print("Teacher Date of Birth (MM/DD/YY): ");
         String dob = sc.next();
         System.out.print("Teacher Department: ");
         String department = sc.next();
         System.out.print("Teacher Courses Handled: ");
         String courses = sc.next();
        System.out.print("Teacher Email: ");
         String email = sc.next();
         System.out.print("Teacher Age: ");
         String age = sc.next();
         String sql = "INSERT INTO tbl_teachers (t_fname, t_lname, t_sex, t_dateofbirth, t_department, t_courses_handled, t_email, t_age)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
         conf.addRecord(sql, fname, lname, email, sex, dob, department, courses, email, age);
     }
    
}
