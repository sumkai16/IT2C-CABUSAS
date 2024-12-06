package students;

import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import system.config;
import system.main;

public class StudentsConfig {
    private Scanner sc = new Scanner(System.in);
    private config conf = new config();
    private main sys = new main();
            StudentMain sm = new StudentMain();
       

    public void addStudents() throws IOException {
        try {
            String fname = validateInput("Student First Name: ", "^[A-Za-z ]+$", "First name must contain only letters and spaces.");
            String lname = validateInput("Student Last Name: ", "^[A-Za-z ]+$", "Last name must contain only letters and spaces.");
            String email = validateInput("Student Email: ", "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", "Invalid email format.");
            String program = validateInput("Student Program: ", "^[A-Za-z0-9 ]+$", "Program must contain only letters, numbers, and spaces.");
            String year = validateInput("Student Year Level: ", "^[1-4]$", "Year level must be between 1 and 4.");
            String section = validateInput("Student Section: ", "^[A-Za-z0-9 ]+$", "Section must contain only letters, numbers, and spaces.");

            String status = "Enrolled";

            String sql = "INSERT INTO tbl_students (s_fname, s_lname, s_email, s_status, s_program, s_year, s_section) VALUES (?, ?, ?, ?, ?, ?, ?)";
            conf.addRecord(sql, fname, lname, email, status, program, year, section);
            System.out.println("Student added successfully.");
        } catch (CancellationException e) {
            System.out.println(e.getMessage());
            sm.students();  // Return to the students menu
        }
    }



    private String validateInput(String message, String pattern, String errorMessage) {
        String input;
        while (true) {
            System.out.print(message + " (Enter 'x' to cancel): ");
            input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("x")) {
                throw new CancellationException("Transaction cancelled by user.");
            }
            if (input.matches(pattern)) {
                return input;
            } else {
                System.out.println(errorMessage);
            }
        }
    }



    public void viewStudentss() {
            String query = "SELECT * FROM tbl_students ORDER BY s_lname ASC";
            String[] headers = {"Student ID", "Last Name", "First Name", "Program", "Year Level", "Section"};
            String[] columns = {"s_id", "s_lname", "s_fname", "s_program", "s_year", "s_section"};
            conf.viewRecords(query, headers, columns);
        }


   public void viewStudents() throws IOException {
        while (true) {
            try {
                System.out.println("\nChoose filtering option:");
                System.out.println("1. By Year Level");
                System.out.println("2. By Section");
                System.out.println("3. By Program");
                System.out.println("4. View All");
                System.out.println("5. Back to Main Menu");
                System.out.print("Enter choice (1-4): ");

                int choice = sc.nextInt();
                sc.nextLine(); // Consume newline

                if (choice == 5) {
                    sm.students();
                   
                }

                String filterColumn = "";
                String filterValue = "";
                String additionalFilterColumn = "";
                String additionalFilterValue = "";

                switch (choice) {
                    case 1:
                        System.out.print("Enter Year Level: ");
                        filterColumn = "s_year";
                        filterValue = sc.nextLine().trim();
                        break;
                    case 2:
                        System.out.print("Enter Program: ");
                        additionalFilterColumn = "s_program";
                        additionalFilterValue = sc.nextLine().trim();

                        System.out.print("Enter Year Level: ");
                        filterColumn = "s_year";
                        filterValue = sc.nextLine().trim();

                        System.out.print("Enter Section: ");
                        String sectionFilterValue = sc.nextLine().trim();

                        // Combine filters in SQL query
                        String querySection = "SELECT * FROM tbl_students WHERE " + additionalFilterColumn + " = ? AND " + filterColumn + " = ? AND s_section = ? ORDER BY s_lname ASC";
                        String[] headersSection = {"Student ID", "Last Name", "First Name", "Program", "Year Level", "Section"};
                        String[] columnsSection = {"s_id", "s_lname", "s_fname", "s_program", "s_year", "s_section"};

                        // Call the method in the config class with three parameters
                        conf.viewFilteredRecords(querySection, headersSection, columnsSection, additionalFilterValue, filterValue, sectionFilterValue);
                        continue; // Skip rest of the loop iteration
                    case 3:
                        System.out.print("Enter Program: ");
                        filterColumn = "s_program";
                        filterValue = sc.nextLine().trim();
                        break;
                    case 4:
                        viewStudentss();
                        break;
                    default:
                        System.out.println("Invalid choice, please enter a number between 1 and 4.");
                        continue;  // Restart the loop if invalid input
                }

                // Construct the SQL query with ORDER BY clause for other cases
                String query = "SELECT * FROM tbl_students WHERE " + filterColumn + " = ? ORDER BY s_lname ASC";
                String[] headers = {"Student ID", "Last Name", "First Name", "Program", "Year Level", "Section"};
                String[] columns = {"s_id", "s_lname", "s_fname", "s_program", "s_year", "s_section"};

                // Call the method in the config class that supports parameterized queries
                conf.viewFilteredRecords(query, headers, columns, filterValue);

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                sc.nextLine();  // Clear the invalid input
            }
        }
    }



    public void updateStudents() throws IOException {
        while (true) {
            try {
                // Input Student ID with cancellation option
                System.out.print("Enter Student ID to update (or 'x' to cancel): ");
                String input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("x")) {
                    throw new CancellationException("Transaction cancelled by user.");
                }

                int studentId = Integer.parseInt(input);

                // Confirm if the student exists
                if (!studentExists(studentId)) {
                    System.out.println("Error: Student ID not found.");
                    continue;
                }

                // Display update options
                System.out.println("Select the field to update:");
                System.out.println("1. First Name");
                System.out.println("2. Last Name");
                System.out.println("3. Email");
                System.out.println("4. Status");
                System.out.println("5. Program");
                System.out.println("6. Year Level");
                System.out.println("7. Section");
                System.out.println("8. Back to Main Menu");
                System.out.print("Enter your choice: ");

                input = sc.nextLine().trim();
                if (input.equalsIgnoreCase("x")) {
                    throw new CancellationException("Transaction cancelled by user.");
                }

                int choice = Integer.parseInt(input);
                String column = "";
                String newValue = "";

                switch (choice) {
                    case 1:
                        column = "s_fname";
                        newValue = validateInput("Enter new First Name: ", "^[A-Za-z ]+$", "First name must contain only letters and spaces.");
                        break;
                    case 2:
                        column = "s_lname";
                        newValue = validateInput("Enter new Last Name: ", "^[A-Za-z ]+$", "Last name must contain only letters and spaces.");
                        break;
                    case 3:
                        column = "s_email";
                        newValue = validateInput("Enter new Email: ", "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", "Invalid email format.");
                        break;
                    case 4:
                        column = "s_status";
                        newValue = validateInput("Enter new Status: ", "^[A-Za-z ]+$", "Status must contain only letters and spaces.");
                        break;
                    case 5:
                        column = "s_program";
                        newValue = validateInput("Enter new Program: ", "^[A-Za-z0-9 ]+$", "Program must contain only letters, numbers, and spaces.");
                        break;
                    case 6:
                        column = "s_year";
                        newValue = validateInput("Enter new Year Level (1-4): ", "^[1-4]$", "Year level must be between 1 and 4.");
                        break;
                    case 7:
                        column = "s_section";
                        newValue = validateInput("Enter new Section: ", "^[A-Za-z0-9 ]+$", "Section must contain only letters, numbers, and spaces.");
                        break;
                    case 8:
                        sm.students();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        continue;
                }

                String sqlUpdate = "UPDATE tbl_students SET " + column + " = ? WHERE s_id = ?";
                try (Connection conn = conf.connectDB();
                     PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                    pstmt.setString(1, newValue);
                    pstmt.setInt(2, studentId);
                    int rowsUpdated = pstmt.executeUpdate();
                    System.out.println(rowsUpdated > 0 ? "Student information updated successfully." : "No changes made or student ID not found.");
                } catch (SQLException e) {
                    System.out.println("Error updating student information: " + e.getMessage());
                }

                System.out.print("Do you want to update another student? (yes/no): ");
                String response = sc.nextLine().trim();
                if (!response.equalsIgnoreCase("yes")) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or 'x' to cancel.");
            } catch (CancellationException e) {
                System.out.println(e.getMessage());
                sm.students();  // Return to the students menu
                return;
            }
        }
    }



    // Method to check if a student exists
    private boolean studentExists(int studentId) {
        String query = "SELECT s_id FROM tbl_students WHERE s_id = ?";
        try (Connection conn = conf.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error checking student existence: " + e.getMessage());
            return false;
        }
}


    // Method to map columns to validation patterns
    private String getValidationPattern(String column) {
        switch (column) {
            case "s_fname":
            case "s_lname":
                return "^[A-Za-z ]+$"; // Only letters and spaces
            case "s_email":
                return "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"; // Email format
            case "s_year":
                return "^[1-4]$"; // Year level between 1 and 4
            case "s_program":
            case "s_section":
                return "^[A-Za-z0-9 ]+$"; // Alphanumeric with spaces
            default:
                return ".*"; // Default pattern if not specified
        }
    }


    private String getUserInput(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private boolean appendUpdate(StringBuilder sqlUpdate, String column, String value, boolean firstField) {
        if (!value.trim().isEmpty()) {
            if (!firstField) sqlUpdate.append(", ");
            sqlUpdate.append(column).append(" = ?");
            firstField = false;
        }
        return firstField;
    }

    private int setUpdateParameters(PreparedStatement pstmt, String... values) throws SQLException {
        int parameterIndex = 1;
        for (String value : values) {
            if (!value.trim().isEmpty()) {
                pstmt.setString(parameterIndex++, value);
            }
        }
        return parameterIndex;
    }

    public void deleteStudents() throws IOException {
        try {
            // Input Student ID with cancellation option
            System.out.print("Enter Student ID to delete (or 'x' to cancel): ");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("x")) {
                throw new CancellationException("Transaction cancelled by user.");
            }

            int studentId = Integer.parseInt(input);

            // Confirm if the student exists
            if (!studentExists(studentId)) {
                System.out.println("Error: Student ID not found.");
                return;
            }

            String sqlDelete = "DELETE FROM tbl_students WHERE s_id = ?";
            try (Connection conn = conf.connectDB(); PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
                pstmt.setInt(1, studentId);
                int rowsDeleted = pstmt.executeUpdate();
                System.out.println(rowsDeleted > 0 ? "Student record deleted successfully." : "No record found with the given ID.");
            } catch (SQLException e) {
                System.out.println("Error deleting record: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number or 'x' to cancel.");
        } catch (CancellationException e) {
            System.out.println(e.getMessage());
            sm.students();  // Return to the students menu
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
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    addStudents();
                    break;
                case 2:
                    updateStudents();
                    break;
                case 3:
                    deleteStudents();
                    break;
                case 4:
                    viewUserInfo(getStudentId());
                    break;
                case 5:
                    sys.main(new String[]{});
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private int getStudentId() {
        System.out.print("Enter Student ID for full information: ");
        return sc.nextInt();
    }
    
    
}
