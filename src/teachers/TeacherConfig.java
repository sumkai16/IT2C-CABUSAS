package teachers;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import system.config;
import system.main;

public class TeacherConfig {
    private Scanner sc = new Scanner(System.in);
    private config conf = new config();
    private main sys = new main();

    public void addTeachers() {
        try {
            String fname = promptForInput("Teacher First Name: ");
            String lname = promptForInput("Teacher Last Name: ");
            String sex = getValidSexInput();
            String dob = promptForDateInput("Teacher Date of Birth (YYYY-MM-DD): ");
            String department = promptForInput("Teacher Department: ");
            String email = promptForEmailInput("Teacher Email: ");

            String sql = "INSERT INTO tbl_teachers (t_fname, t_lname, t_sex, t_dateofbirth, t_department, t_email) VALUES (?, ?, ?, ?, ?, ?)";
            conf.addRecord(sql, fname, lname, sex, dob, department, email);
            System.out.println("Teacher added successfully.");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void viewTeachers() throws IOException {
        String query = "SELECT t_id, t_fname, t_lname, t_department FROM tbl_teachers";
        String[] headers = {"ID", "First Name", "Last Name", "Department"};
        String[] columns = {"t_id", "t_fname", "t_lname", "t_department"};
        conf.viewRecords(query, headers, columns);
    }

    public void selections() {
        System.out.println("1. Add Teacher\t4. View Full Information\n2. Update Teacher\t5. Back\n3. Delete Teacher");
        try {
            int op = sc.nextInt();
            sc.nextLine();  // Clear buffer after nextInt()
            switch (op) {
                case 1: addTeachers(); break;
                case 2: updateTeachers(); break;
                case 3: deleteTeachers(); break;
                case 4: viewFullTeacherInfo(); break;
                case 5: sys.main(new String[]{}); break;
                default: System.out.println("Invalid option. Please try again.");
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void updateTeachers() {
        try {
            System.out.print("Enter Teacher ID to update: ");
            int teacherId = sc.nextInt();
            sc.nextLine();  // Clear buffer

            System.out.print("Do you want to update the first and last name? (yes/no): ");
            String updateNameChoice = sc.nextLine().trim().toLowerCase();

            if (updateNameChoice.equals("yes")) {
                updateTeacherName(teacherId);
            }

            System.out.println("1. Department\n2. Email");
            System.out.print("Choose (1-2): ");
            int choice = sc.nextInt();
            sc.nextLine();  // Clear buffer

            String newValue = promptForInput("New Value");
            if (choice == 2) {
                newValue = promptForEmailInput("New Email: ");
            }

            updateTeacherField(teacherId, choice, newValue);
            System.out.println("Update completed successfully.");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void deleteTeachers() {
        try {
            System.out.println("List of Teachers:");
            viewTeachers();
            System.out.print("Enter Teacher ID to delete: ");
            int teacherId = sc.nextInt();

            if (teacherExists(teacherId)) {
                deleteTeacherRecord(teacherId);
            } else {
                System.out.println("Error: Teacher ID not found.");
            }
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void viewFullTeacherInfo() throws IOException {
        System.out.print("Enter Teacher ID for full information: ");
        int userId = sc.nextInt();
        viewUserInfo(userId);
    }

    public void viewUserInfo(int userID) throws IOException {
        System.out.println("--------------------------------------------------------------------------------");
        try (PreparedStatement state = conf.connectDB().prepareStatement("SELECT * FROM tbl_teachers WHERE t_id = ?")) {
            state.setInt(1, userID);

            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    System.out.printf("\nFull Information: \n--------------------------------------------------------------------------------" +
                            "\nUser ID:           | %d\nFirst Name:        | %s\nLast Name:         | %s\nSex:               | %s\nDate of Birth:     | %s\nDepartment:        | %s\nEmail:             | %s\n--------------------------------------------------------------------------------",
                            result.getInt("t_id"), result.getString("t_fname"), result.getString("t_lname"),
                            result.getString("t_sex"), result.getString("t_dateofbirth"), result.getString("t_department"), result.getString("t_email"));
                    System.out.print("\nPress any key to continue...");
                    System.in.read();
                } else {
                    System.out.println("User ID not found.");
                }
            }
        } catch (SQLException e) {
            handleError(e);
        }
    }

    private boolean teacherExists(int teacherId) {
        String sql = "SELECT COUNT(*) FROM tbl_teachers WHERE t_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleError(e);
        }
        return false;
    }

    private void deleteTeacherRecord(int teacherId) {
        String sql = "DELETE FROM tbl_teachers WHERE t_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
            System.out.println("Teacher deleted successfully.");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    private void updateTeacherName(int teacherId) {
        String newFirstName = promptForInput("Enter New First Name: ");
        String newLastName = promptForInput("Enter New Last Name: ");

        String sqlUpdateName = "UPDATE tbl_teachers SET t_fname = ?, t_lname = ? WHERE t_id = ?";
        conf.updateRecord(sqlUpdateName, newFirstName, newLastName, teacherId);
        System.out.println("First Name and Last Name updated successfully.");
    }

    private void updateTeacherField(int teacherId, int choice, String newValue) {
        String column = choice == 1 ? "t_department" : "t_email";
        String sqlUpdate = "UPDATE tbl_teachers SET " + column + " = ? WHERE t_id = ?";
        conf.updateRecord(sqlUpdate, newValue, teacherId);
    }

    private String promptForInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                break;
            }
            System.out.println("Error: " + prompt + " cannot be empty.");
        }
        return input;
    }

    private String getValidSexInput() {
        String sex;
        while (true) {
            System.out.print("Teacher Sex (M/F): ");
            sex = sc.nextLine().toUpperCase();
            if (sex.equals("M") || sex.equals("F")) {
                break;
            }
            System.out.println("Error: Invalid sex. Enter 'M' or 'F'.");
        }
        return sex;
    }

    private String promptForDateInput(String prompt) {
        String date;
        while (true) {
            System.out.print(prompt);
            date = sc.nextLine().trim();
            if (isValidDateFormat(date)) {
                break;
            }
            System.out.println("Error: Date must be in YYYY-MM-DD format.");
        }
        return date;
    }

    private boolean isValidDateFormat(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private String promptForEmailInput(String prompt) {
        String email;
        while (true) {
            System.out.print(prompt);
            email = sc.nextLine().trim();
            if (email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                break;
            }
            System.out.println("Error: Invalid email format.");
        }
        return email;
    }

    private void handleError(Exception e) {
        System.out.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
}
