package students;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import system.config;
import system.main;

public class StudentsConfig {
     Scanner sc = new Scanner(System.in);
     config conf = new config();
     main sys = new main();
    private int op = 0;

    public void addStudents() {
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

        String sql = "INSERT INTO tbl_students (s_fname, s_lname, s_email, s_status, s_program, s_section) VALUES (?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, fname, lname, email, status, program, section);
    }

    public void viewStudents() {
        String query =      "SELECT * FROM tbl_students";
        String[] headers =  {"ID", "First Name", "Last Name", "Program", "Section"};
        String[] columns =  {"s_id", "s_fname", "s_lname", "s_program", "s_section"};
        conf.viewRecords(query, headers, columns);
        
        System.out.println("1. Add Student\t\t4. View Full Information\n2. Update Student\t5. Back\n3. Delete Student");
        op = sc.nextInt();
        switch(op) {
            case 1: addStudents(); break;
            case 2: updateStudents(); break;
            case 3: deleteStudents(); break;
            case 4: {
                System.out.print("Enter Student ID for full information: ");
                int userId = sc.nextInt();
                try {
                    viewUserInfo(userId);
                } catch (IOException e) {
                    System.out.println("Error viewing user information: " + e.getMessage());
                }
                break;
            }
            case 5: sys.main(new String[]{});
            default: System.out.println("Invalid option. Please try again."); break;
        }
    }

    public void updateStudents() {
        System.out.print("Enter ID to update: ");
        int studentId = sc.nextInt();
        System.out.println("1. First Name\n2. Last Name\n3. Email\n4. Status\n5. Program\n6. Section");
        System.out.print("Choose (1-6): ");
        int choice = sc.nextInt();
        
        System.out.print("Enter New Value: ");
        String newValue = sc.next();
        String sqlUpdate = "UPDATE tbl_students SET " + getColumnName(choice) + " = ? WHERE s_id = ?";
        conf.updateRecord(sqlUpdate, newValue, studentId);
    }

    public void deleteStudents() {
        System.out.print("Enter ID to delete: ");
        int studentId = sc.nextInt();
        String sqlDelete = "DELETE FROM tbl_students WHERE s_id = ?";
        conf.deleteRecord(sqlDelete, studentId);
    }

    private String getColumnName(int choice) {
        switch (choice) {
            case 1: return "s_fname";
            case 2: return "s_lname";
            case 3: return "s_email";
            case 4: return "s_status";
            case 5: return "s_program";
            case 6: return "s_section";
            default: return "";
        }
    }

    public void viewUserInfo(int userID) throws IOException {
        System.out.println("--------------------------------------------------------------------------------");
        try {
            PreparedStatement state = conf.connectDB().prepareStatement("SELECT * FROM tbl_students WHERE s_id = ?");
            state.setInt(1, userID);
            
            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    System.out.println("\nFull Information: "
                            + "\n--------------------------------------------------------------------------------"
                            + "\nUser ID:           | "+result.getInt(      "s_id")
                            + "\nFirst Name:        | "+result.getString(   "s_fname")
                            + "\nLast Name:         | "+result.getString(   "s_lname")
                            + "\nEmail Address:     | "+result.getString(   "s_email")
                            + "\nStatus:            | "+result.getString(   "s_status")
                            + "\nProgram:           | "+result.getString(   "s_program")
                            + "\nSection:           | "+result.getString(   "s_section"));
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.print("Press any key to continue...");
                    System.in.read();
                } else {
                    System.out.println("User ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
