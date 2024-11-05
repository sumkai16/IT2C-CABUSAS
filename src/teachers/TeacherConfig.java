package teachers;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import system.config;
import system.main;

public class tconf {
    private Scanner sc = new Scanner(System.in);
    private config conf = new config();
    private main sys = new main();
    private int op = 0;

    public void addTeachers() {
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
        System.out.print("Teacher Email: ");
        String email = sc.next();

        String sql = "INSERT INTO tbl_teachers (t_fname, t_lname, t_sex, t_dateofbirth, t_department, t_email) VALUES (?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, fname, lname, sex, dob, department, email);
    }

    public void viewTeachers() {
        String query = "SELECT * FROM tbl_teachers";
        String[] headers = {"ID", "First Name", "Last Name", "Sex", "Date of Birth", "Department", "Email"};
        String[] columns = {"t_id", "t_fname", "t_lname", "t_sex", "t_dateofbirth", "t_department", "t_email"};
        conf.viewRecords(query, headers, columns);
        
        System.out.println("1. Add Teacher\t4. View Full Information\n2. Update Teacher\t5. Back\n3. Delete Teacher");
        op = sc.nextInt();
        switch(op) {
            case 1: addTeachers(); break;
            case 2: updateTeachers(); break;
            case 3: deleteTeachers(); break;
            case 4: {
                System.out.print("Enter Teacher ID for full information: ");
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

    public void updateTeachers() {
        System.out.print("Enter Teacher ID to update: ");
        int teacherId = sc.nextInt();
        System.out.println("1. Department\n2. Email");
        System.out.print("Choose (1-2): ");
        int choice = sc.nextInt();

        System.out.print("Enter New Value: ");
        String newValue = sc.next();
        String sqlUpdate = "UPDATE tbl_teachers SET " + getColumnName(choice) + " = ? WHERE t_id = ?";
        conf.updateRecord(sqlUpdate, newValue, teacherId);
    }

    public void deleteTeachers() {
        System.out.print("Enter ID to delete: ");
        int teacherId = sc.nextInt();
        String sqlDelete = "DELETE FROM tbl_teachers WHERE t_id = ?";
        conf.deleteRecord(sqlDelete, teacherId);
    }

    private String getColumnName(int choice) {
        switch (choice) {
            case 1: return "t_department";
            case 2: return "t_email";
            default: return "";
        }
    }

    public void viewUserInfo(int userID) throws IOException {
        System.out.println("--------------------------------------------------------------------------------");
        try {
            PreparedStatement state = conf.connectDB().prepareStatement("SELECT * FROM tbl_teachers WHERE t_id = ?");
            state.setInt(1, userID);
            
            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    System.out.println("\nFull Information: "
                            + "\n--------------------------------------------------------------------------------"
                            + "\nUser ID:           | "+result.getInt("t_id")
                            + "\nFirst Name:        | "+result.getString("t_fname")
                            + "\nLast Name:         | "+result.getString("t_lname")
                            + "\nSex:               | "+result.getString("t_sex")
                            + "\nDate of Birth:     | "+result.getString("t_dateofbirth")
                            + "\nDepartment:        | "+result.getString("t_department")
                            + "\nEmail:             | "+result.getString("t_email"));
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
