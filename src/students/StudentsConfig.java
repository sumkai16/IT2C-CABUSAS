package students;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import system.config;
import system.main;

public class StudentsConfig {
    private Scanner sc = new Scanner(System.in);
    private config conf = new config();
    private main sys = new main();

    public void addStudents() {
        System.out.print("Student First Name: ");
        String fname = sc.nextLine();
        System.out.print("Student Last Name: ");
        String lname = sc.nextLine();
        System.out.print("Student Email: ");
        String email = sc.nextLine();
        System.out.print("Student Status: ");
        String status = sc.nextLine();
        System.out.print("Student Program: ");
        String program = sc.nextLine();
        System.out.print("Student Year Level: ");
        String year = sc.nextLine();
        System.out.print("Student Section: ");
        String section = sc.nextLine();

        String sql = "INSERT INTO tbl_students (s_fname, s_lname, s_email, s_status, s_program, s_year, s_section) VALUES (?, ?, ?, ?, ?, ?, ?)";
        conf.addRecord(sql, fname, lname, email, status, program, year, section);
    }

    public void viewStudents() {
        String query = "SELECT * FROM tbl_students";
        String[] headers = {"Student ID", "First Name", "Last Name", "Program", "Year Level", "Section"};
        String[] columns = {"s_id", "s_fname", "s_lname", "s_program", "s_year", "s_section"};
        conf.viewRecords(query, headers, columns);
    }

    public void updateStudents() {
        System.out.print("Enter ID to update: ");
        int studentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Do you want to update the first and last name? (yes/no): ");
        String updateNameChoice = sc.nextLine().trim();

        String newFirstName = updateNameChoice.equalsIgnoreCase("yes") ? getUserInput("New First Name: ") : "";
        String newLastName = updateNameChoice.equalsIgnoreCase("yes") ? getUserInput("New Last Name: ") : "";
        String newEmail = getUserInput("New Email: ");
        String newStatus = getUserInput("New Status: ");
        String newProgram = getUserInput("New Program: ");
        String newYear = getUserInput("New Year: ");
        String newSection = getUserInput("New Section: ");

        StringBuilder sqlUpdate = new StringBuilder("UPDATE tbl_students SET ");
        boolean firstField = true;

        firstField = appendUpdate(sqlUpdate, "s_fname", newFirstName, firstField);
        firstField = appendUpdate(sqlUpdate, "s_lname", newLastName, firstField);
        firstField = appendUpdate(sqlUpdate, "s_email", newEmail, firstField);
        firstField = appendUpdate(sqlUpdate, "s_status", newStatus, firstField);
        firstField = appendUpdate(sqlUpdate, "s_program", newProgram, firstField);
        firstField = appendUpdate(sqlUpdate, "s_year", newYear, firstField);
        appendUpdate(sqlUpdate, "s_section", newSection, firstField);
        
        sqlUpdate.append(" WHERE s_id = ?");

        try (PreparedStatement pstmt = conf.connectDB().prepareStatement(sqlUpdate.toString())) {
            int parameterIndex = setUpdateParameters(pstmt, newFirstName, newLastName, newEmail, newStatus, newProgram, newYear, newSection);
            pstmt.setInt(parameterIndex, studentId);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "Student information updated successfully." : "No changes made.");
        } catch (SQLException e) {
            System.out.println("Error updating student information: " + e.getMessage());
        }
    }

    private String getUserInput(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private boolean appendUpdate(StringBuilder sqlUpdate, String column, String value, boolean firstField) {
        if (!value.isBlank()) {
            if (!firstField) sqlUpdate.append(", ");
            sqlUpdate.append(column).append(" = ?");
            firstField = false;
        }
        return firstField;
    }

    private int setUpdateParameters(PreparedStatement pstmt, String... values) throws SQLException {
        int parameterIndex = 1;
        for (String value : values) {
            if (!value.isBlank()) {
                pstmt.setString(parameterIndex++, value);
            }
        }
        return parameterIndex;
    }

    public void deleteStudents() {
        System.out.print("Enter ID to delete: ");
        int studentId = sc.nextInt();
        String sqlDelete = "DELETE FROM tbl_students WHERE s_id = ?";

        try (Connection conn = conf.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setInt(1, studentId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println(rowsDeleted > 0 ? "Student record deleted successfully." : "No record found with the given ID.");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    public void viewUserInfo(int userID) throws IOException {
        System.out.println("--------------------------------------------------------------------------------");
        try (PreparedStatement state = conf.connectDB().prepareStatement("SELECT * FROM tbl_students WHERE s_id = ?")) {
            state.setInt(1, userID);
            try (ResultSet result = state.executeQuery()) {
                if (result.next()) {
                    System.out.printf("\nFull Information:\n--------------------------------------------------------------------------------\nUser ID:           | %d\nFirst Name:        | %s\nLast Name:         | %s\nEmail Address:     | %s\nStatus:            | %s\nProgram:           | %s\nYear Level:        | %s\nSection:           | %s\n--------------------------------------------------------------------------------\nPress any key to continue...",
                            result.getInt("s_id"), result.getString("s_fname"), result.getString("s_lname"), result.getString("s_email"), result.getString("s_status"), result.getString("s_program"), result.getString("s_year"), result.getString("s_section"));
                    System.in.read();
                } else {
                    System.out.println("User ID not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void wantUpdate() throws IOException {
        System.out.print("Do you want to update?(yes/no): ");
        String up = sc.next().trim();
        while (up.equalsIgnoreCase("yes")) {
            System.out.println("1. Add Student\t\t4. View Full Information\n2. Update Student\t5. Back\n3. Delete Student");
            System.out.print("Enter Choice: ");
            switch (sc.nextInt()) {
                case 1 -> addStudents();
                case 2 -> updateStudents();
                case 3 -> deleteStudents();
                case 4 -> viewUserInfo(getStudentId());
                case 5 -> sys.main(new String[]{});
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private int getStudentId() {
        System.out.print("Enter Student ID for full information: ");
        return sc.nextInt();
    }
}
