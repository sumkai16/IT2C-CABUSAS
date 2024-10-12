
package students;

import java.util.Scanner;
import system.config;

public class studentsconf {
    public void addStudents(){
         Scanner sc = new Scanner(System.in);
         config conf = new config();
         
         System.out.print("Student First Name: ");
         String fname = sc.next();
         System.out.print("Student Last Name: ");
         String lname = sc.next();
         System.out.print("Student Email: ");
         String email = sc.next();
         System.out.print("Student Status: ");
         String status = sc.next();
         System.out.print("Student Program: ");
         String program = sc.next();
         System.out.print("Student Section: ");
         String section = sc.next();

         String sql = "INSERT INTO tbl_students (s_fname, s_lname, s_email, s_status, s_program, s_section)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";
         conf.addRecord(sql, fname, lname, email, status, program, section);
     }
    public void viewStudents() {
        config conf = new config();
        String votersQuery = "SELECT * FROM tbl_students";
        String[] votersHeaders = {"ID", "First Name", "Last Name", "Email", "Status", "Program", "Section"};
        String[] votersColumns = {"s_id", "s_fname", "s_lname", "s_email", "s_status", "s_program", "s_section"};
        
        conf.viewRecords(votersQuery, votersHeaders, votersColumns);
    }
     
    public void updateStudents() {
        Scanner in = new Scanner (System.in);
        config dbConfig = new config();
        System.out.print("Enter ID you want to update: ");
        int studentId = in.nextInt();

            System.out.println("1. First Name\t2. Last Name\n3. Email\t4. Status\n5. Program\t6. Section");
             System.out.print("Enter Action (1-6 only): ");
            int action = in.nextInt();
            while (action>5 || action<0){
               System.out.print("Enter Action (1-6 only): ");
                 action = in.nextInt();
            }
            String newfName = null, newlName = null, email = null, status = null, program = null, section = null ;
           
              
                    switch(action){
                case 1://FirstName 
                    System.out.print("Enter New First Name: ");
                     newfName = in.next();
                    String sqlUpdate = "UPDATE tbl_students SET s_fname = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, newfName, studentId);
                     break;
                case 2://LastName 
                    System.out.print("Enter New Last Name: ");
                     newlName = in.next();
                    sqlUpdate = "UPDATE tbl_students SET s_lname = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, newlName, studentId);
                     break;
                case 3://Email 
                    System.out.print("Enter New Email: ");
                     email = in.next();
                     sqlUpdate = "UPDATE tbl_students SET s_email = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, email, studentId);
                     break;
                case 4://Status 
                    System.out.print("Enter New Status: ");
                     status = in.next();  
                     sqlUpdate = "UPDATE tbl_students SET s_status = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, status, studentId);
                     break;
                case 5://Program 
                    System.out.print("Enter New Program: ");
                     program = in.next();
                     sqlUpdate = "UPDATE tbl_students SET s_program = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, program, studentId);
                     break;
                case 6://Section 
                    System.out.print("Enter New Section: ");
                     section = in.next();   
                     sqlUpdate = "UPDATE tbl_students SET s_section = ?"
                                         + " WHERE s_id = ?";     
                    dbConfig.updateRecord(sqlUpdate, section, studentId);
                     break;
            }
    }
    
    public void deleteStudents (){
        Scanner in = new Scanner (System.in);
        config dbConfig = new config();
         System.out.print("Enter ID you want to delete: ");
         int studentId = in.nextInt();
         
        String sqlDelete = "DELETE FROM tbl_students WHERE s_id = ?";
        dbConfig.deleteRecord(sqlDelete, studentId);
    }

    
}
