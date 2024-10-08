package sgs;
import java.util.*;
import java.sql.*;
public class main {
    
       
    
     
    
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String op = null;
         
        do {
            System.out.println("===========================================");
            System.out.println("Welcome to Student Grading System");
            System.out.println("===========================================");            
            System.out.println("|        1. ADD                            |");
            System.out.println("|        2. VIEW                           |");
            System.out.println("|        3. UPDATE                         |");
            System.out.println("|        4. DELETE                         |");
            System.out.println("|        5. EXIT                           |");
            System.out.println("===========================================");
            System.out.print("Enter Action (1-5 only): ");
            int action = in.nextInt();
            
            switch(action){
                case 1:
                    main demo = new main();
                    demo.addStudents();
                break;
                case 2:
                    main demo1 = new main();
                    demo1.viewStudents();
                    
            }
            System.out.print("Do you want to continue?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
        System.out.println("Thank You, See you soon!");
        
        
        
        
    }
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
    private void viewStudents() {
        config conf = new config();
        String votersQuery = "SELECT * FROM tbl_students";
        String[] votersHeaders = {"ID", "First Name", "Last Name", "Email", "Status", "Program", "Section"};
        String[] votersColumns = {"s_id", "s_fname", "s_lname", "s_email", "s_status", "s_program", "s_section"};
        
        conf.viewRecords(votersQuery, votersHeaders, votersColumns);
    }
     
    
}
