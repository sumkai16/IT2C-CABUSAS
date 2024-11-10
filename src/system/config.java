package system;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class config {

    // Connection Method to SQLite
    public static Connection connectDB() {
        try {
            Class.forName("org.sqlite.JDBC"); // Load SQLite JDBC driver
            return DriverManager.getConnection("jdbc:sqlite:axci.db"); // Establish connection
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return null;
        }
    }

    //-----------------------------------------------
    // ADD, UPDATE, DELETE Records Methods
    //-----------------------------------------------
    public void executeUpdate(String sql, String successMessage, String failureMessage, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, values);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(successMessage);
            } else {
                System.out.println(failureMessage);
            }
        } catch (SQLException e) {
            System.out.println(failureMessage + ": " + e.getMessage());
        }
    }

    public void addRecord(String sql, Object... values) {
        executeUpdate(sql, "Record added successfully!", "Error adding record", values);
    }

    public void updateRecord(String sql, Object... values) {
        executeUpdate(sql, "Record updated successfully!", "Error updating record", values);
    }

    public void deleteRecord(String sql, Object... values) {
        executeUpdate(sql, "Record deleted successfully!", "Error deleting record", values);
    }

    //-----------------------------------------------
    // VIEW RECORDS METHODS (WITHOUT AND WITH PARAMS)
    //-----------------------------------------------

    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
        viewRecordsWithParams(sqlQuery, columnHeaders, columnNames);
    }

    public void viewRecordsWithParams(String sqlQuery, String[] columnHeaders, String[] columnNames, Object... params) {
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery)) {

            setPreparedStatementValues(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                printTableHeaders(columnHeaders);

                // Print rows
                while (rs.next()) {
                    printTableRow(rs, columnNames);
                }
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    //-----------------------------------------------
    // Helper Methods for Table Formatting
    //-----------------------------------------------
    private void printTableHeaders(String[] columnHeaders) {
        StringBuilder headerLine = new StringBuilder("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n| ");
        for (String header : columnHeaders) {
            headerLine.append(String.format("%-30s | ", header));
        }
        headerLine.append("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(headerLine);
    }

    private void printTableRow(ResultSet rs, String[] columnNames) throws SQLException {
        StringBuilder row = new StringBuilder("| ");
        for (String colName : columnNames) {
            String value = rs.getString(colName);
            row.append(String.format("%-30s | ", value != null ? value : ""));
        }
        System.out.println(row);
    }

    //-----------------------------------------------
    // Helper Method for Setting PreparedStatement Values
    //-----------------------------------------------
    private void setPreparedStatementValues(PreparedStatement pstmt, Object... values) throws SQLException {
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) value);
            } else if (value instanceof Double) {
                pstmt.setDouble(i + 1, (Double) value);
            } else if (value instanceof Float) {
                pstmt.setFloat(i + 1, (Float) value);
            } else if (value instanceof Long) {
                pstmt.setLong(i + 1, (Long) value);
            } else if (value instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) value);
            } else if (value instanceof java.util.Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) value).getTime()));
            } else if (value instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) value);
            } else if (value instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(i + 1, (java.sql.Timestamp) value);
            } else {
                pstmt.setString(i + 1, value.toString());
            }
        }
    }

    //-----------------------------------------------
    // GET SINGLE VALUE METHOD
    //-----------------------------------------------
    public double getSingleValue(String sql, Object... params) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setPreparedStatementValues(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving single value: " + e.getMessage());
            return 0.0;
        }
    }

    // Method to get a list of course IDs
    public List<Integer> getCourseIds() {
        List<Integer> courseIds = new ArrayList<>();
        String query = "SELECT course_id FROM tbl_courses";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courseIds.add(rs.getInt("course_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return courseIds;
    }

    // Method to get a list of student IDs in a specific course
    public List<Integer> getStudentIdsInCourse(int courseId) {
        List<Integer> studentIds = new ArrayList<>();
        String query = "SELECT student_id FROM tbl_student_courses WHERE course_id = ?";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getInt("student_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentIds;
    }
}
