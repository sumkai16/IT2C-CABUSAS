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
    private TeacherMain tm = new TeacherMain();

    public void addTeachers() {
        try {
            String fname = promptForInput("Teacher First Name (Enter 'x' to cancel): ");
            if (fname == null) return;

            String lname = promptForInput("Teacher Last Name (Enter 'x' to cancel): ");
            if (lname == null) return;

            String sex = getValidSexInput();
            if (sex == null) return;

            String dob = promptForDateInput("Teacher Date of Birth (YYYY-MM-DD) (Enter 'x' to cancel): ");
            if (dob == null) return;

            String department = promptForInput("Teacher Department (Enter 'x' to cancel): ");
            if (department == null) return;

            String email = promptForEmailInput("Teacher Email (Enter 'x' to cancel): ");
            if (email == null) return;

            if (isEmailDuplicate(email)) {
                System.out.println("Error: This email is already registered.");
                return;
            }

            String sql = "INSERT INTO tbl_teachers (t_fname, t_lname, t_sex, t_dateofbirth, t_department, t_email) VALUES (?, ?, ?, ?, ?, ?)";
            conf.addRecord(sql, fname, lname, sex, dob, department, email);
            System.out.println("Teacher added successfully.");
        } catch (Exception e) {
            handleError(e);
        }
    }

    private boolean isEmailDuplicate(String email) {
        String sql = "SELECT COUNT(*) FROM tbl_teachers WHERE t_email = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleError(e);
        }
        return false;
    }

    public void viewTeachers() {
        System.out.print("Filter by Department? (Y/N): ");
        String filterChoice = sc.nextLine().trim().toUpperCase();

        if (filterChoice.equals("Y")) {
            System.out.print("Enter Department Name: ");
            String department = sc.nextLine().trim();

            String query = "SELECT t_id, t_fname, t_lname, t_department FROM tbl_teachers WHERE t_department = ?";
            String[] headers = {"ID", "First Name", "Last Name", "Department"};
            String[] columns = {"t_id", "t_fname", "t_lname", "t_department"};

            conf.viewFilteredRecords(query, headers, columns, department);
        } else {
            String query = "SELECT t_id, t_fname, t_lname, t_department FROM tbl_teachers";
            String[] headers = {"ID", "First Name", "Last Name", "Department"};
            String[] columns = {"t_id", "t_fname", "t_lname", "t_department"};

            conf.viewRecords(query, headers, columns);
        }
    }
    public void viewFullTeacherInfo() {
        String query = "SELECT t_id, t_fname, t_lname, t_sex, t_dateofbirth, t_department, t_email FROM tbl_teachers";
        String[] headers = {"ID", "First Name", "Last Name", "Sex", "Date of Birth", "Department", "Email"};
        String[] columns = {"t_id", "t_fname", "t_lname", "t_sex", "t_dateofbirth", "t_department", "t_email"};

        conf.viewRecords(query, headers, columns);
    }

    public void selections() {
        System.out.println("1. Add Teacher\t4. View Full Information\n2. Update Teacher\t5. Back\n3. Delete Teacher");
        try {
            int op = getValidIntegerInput();
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

    // Update teacher information
    public void updateTeachers() throws IOException {
        System.out.print("Enter Teacher ID to update (or 'x' to cancel): ");
        int teacherId = getValidIntegerInput();
        if (teacherId == -1) return;  // If 'x' is entered, exit

        if (!teacherExists(teacherId)) {
            System.out.println("Error: Teacher ID not found.");
            return;
        }

        System.out.println("Select the field to update:\n1. First Name\n2. Last Name\n3. Sex\n4. Date of Birth\n5. Department\n6. Email\n7. Back to Main Menu");
        int choice = getValidIntegerInput();
        String newValue = null;

        switch (choice) {
            case 1: newValue = promptForInput("New First Name: "); updateTeacherField(teacherId, "t_fname", newValue); break;
            case 2: newValue = promptForInput("New Last Name: "); updateTeacherField(teacherId, "t_lname", newValue); break;
            case 3: newValue = getValidSexInput(); updateTeacherField(teacherId, "t_sex", newValue); break;
            case 4: newValue = promptForDateInput("New Date of Birth (YYYY-MM-DD): "); updateTeacherField(teacherId, "t_dateofbirth", newValue); break;
            case 5: newValue = promptForInput("New Department: "); updateTeacherField(teacherId, "t_department", newValue); break;
            case 6:
                newValue = promptForEmailInput("New Email: ");
                if (isEmailDuplicate(newValue)) {
                    System.out.println("Error: This email is already registered.");
                    return;
                }
                updateTeacherField(teacherId, "t_email", newValue);
                break;
            case 7: tm.teachers(); return;
            default: System.out.println("Invalid option. Please try again.");
        }
    }

    private void updateTeacherField(int teacherId, String column, String newValue) {
        String sql = "UPDATE tbl_teachers SET " + column + " = ? WHERE t_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newValue);
            stmt.setInt(2, teacherId);
            int rowsUpdated = stmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Teacher information updated successfully." : "No changes made.");
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void deleteTeachers() throws IOException {
        System.out.print("Enter Teacher ID to delete (or 'x' to cancel): ");
        int teacherId = getValidIntegerInput();
        if (teacherId == -1) return;

        if (teacherExists(teacherId)) {
            deleteTeacherRecord(teacherId);
        } else {
            System.out.println("Error: Teacher ID not found.");
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

    private int getValidIntegerInput() throws IOException {
        while (true) {
            String userInput = sc.nextLine().trim();
            if (userInput.equalsIgnoreCase("x")) {
                System.out.println("Operation canceled.");
                tm.teachers();
                return -1;
            }
            try {
                return Integer.parseInt(userInput);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
            }
        }
    }

    private String promptForInput(String prompt) {
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        return input.equalsIgnoreCase("x") ? null : input;
    }

    private String getValidSexInput() throws IOException {
        while (true) {
            System.out.print("Teacher Sex (M/F, or 'x' to cancel): ");
            String sex = sc.nextLine().toUpperCase();
            if (sex.equals("X")) {
                System.out.println("Operation canceled.");
                tm.teachers();
                return null;
            }
            if (sex.equals("M") || sex.equals("F")) return sex;
            System.out.println("Error: Invalid sex. Enter 'M' or 'F'.");
        }
    }

    private String promptForDateInput(String prompt) {
        System.out.print(prompt);
        String date = sc.nextLine().trim();
        return date.equalsIgnoreCase("x") ? null : date;
    }

    private String promptForEmailInput(String prompt) {
        System.out.print(prompt);
        String email = sc.nextLine().trim();
        return email.equalsIgnoreCase("x") ? null : email;
    }

    private void handleError(Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
