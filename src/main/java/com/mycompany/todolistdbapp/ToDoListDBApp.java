package com.mycompany.todolistdbapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class ToDoListDBApp {

    // DATABASE CONFIGURATION
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/tododb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";        // change if needed
    private static final String DB_PASSWORD = "root"; // change if needed

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            showMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            switch (choice) {
                case 1 -> addTask();
                case 2 -> viewTasks();
                case 3 -> markTaskAsDone();
                case 4 -> deleteTask();
                case 5 -> System.out.println("Exiting To-Do List App 👋");
                default -> System.out.println("Invalid choice ❌");
            }
        } while (choice != 5);
    }

    // ---------------- DATABASE CONNECTION ----------------
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // ---------------- MENU ----------------
    private static void showMenu() {
        System.out.println("\n===== TO-DO LIST MENU =====");
        System.out.println("1. Add Task");
        System.out.println("2. View Tasks");
        System.out.println("3. Mark Task as Done");
        System.out.println("4. Delete Task");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }

    // ---------------- ADD TASK ----------------
    private static void addTask() {
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();

        String sql = "INSERT INTO tasks (description) VALUES (?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, description);
            ps.executeUpdate();
            System.out.println("Task added successfully ✅");

        } catch (SQLException e) {
            System.out.println("Error adding task ❌");
        }
    }

    // ---------------- VIEW TASKS ----------------
    private static void viewTasks() {
        // ✅ Corrected SQL to show TRUE / FALSE for is_done
        String sql = "SELECT id, description, IF(is_done = 1, 'TRUE', 'FALSE') AS status FROM tasks";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n----- TASK LIST -----");
            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("description");
                String status = rs.getString("status"); // get TRUE / FALSE directly from SQL

                System.out.println(id + ". " + desc + " [" + status + "]");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching tasks ❌");
        }
    }

    // ---------------- MARK TASK AS DONE ----------------
    private static void markTaskAsDone() {
        viewTasks();
        System.out.print("Enter task ID to mark as done: ");
        int id = scanner.nextInt();

        String sql = "UPDATE tasks SET is_done = TRUE WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Task marked as done ✔");
            else
                System.out.println("Task not found ❌");

        } catch (SQLException e) {
        }
    }

    // ---------------- DELETE TASK ----------------
    private static void deleteTask() {
        viewTasks();
        System.out.print("Enter task ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Task deleted 🗑");
            else
                System.out.println("Task not found ❌");

        } catch (SQLException e) {
        }
    }
}
