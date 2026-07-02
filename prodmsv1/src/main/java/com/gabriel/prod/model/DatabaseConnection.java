package com.gabriel.prod.model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection connection = null;

    // Reads db.properties from the classpath and returns a Connection.
    // Also auto-creates the database and tables if they don't exist yet.
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Properties props = new Properties();
                InputStream input = DatabaseConnection.class
                    .getResourceAsStream("/db.properties");
                if (input == null) {
                    System.err.println(
                        "db.properties not found. " +
                        "Copy db.properties.template to db.properties " +
                        "and fill in your MySQL password."
                    );
                    javafx.application.Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("Database Configuration Missing");
                        alert.setHeaderText("db.properties not found");
                        alert.setContentText(
                            "Please copy db.properties.template to db.properties " +
                            "and set your MySQL password before running the app."
                        );
                        alert.showAndWait();
                    });
                    return null;
                }
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.user");
                String password = props.getProperty("db.password");

                // First connect without a database to create it if needed
                String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false"
                    + "&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                Connection setup = DriverManager.getConnection(
                    baseUrl, user, password);
                Statement stmt = setup.createStatement();
                stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS banksys_db");
                stmt.close();
                setup.close();

                // Now connect to the actual database
                connection = DriverManager.getConnection(url, user, password);
                initializeTables(connection);

            } catch (Exception e) {
                System.err.println("Database connection failed: "
                    + e.getMessage());
                System.err.println(
                    "Make sure MySQL is running and your password " +
                    "in db.properties is correct.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    private static void initializeTables(Connection conn) throws Exception {
        Statement stmt = conn.createStatement();

        // Accounts table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS accounts (" +
            "  account_number VARCHAR(50) PRIMARY KEY," +
            "  owner_name     VARCHAR(100) NOT NULL," +
            "  account_type   VARCHAR(20) NOT NULL," +
            "  balance        DOUBLE NOT NULL DEFAULT 0.0," +
            "  password       VARCHAR(100) NOT NULL" +
            ")"
        );

        // Transactions table
        stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS transactions (" +
            "  id             INT AUTO_INCREMENT PRIMARY KEY," +
            "  account_number VARCHAR(50) NOT NULL," +
            "  type           VARCHAR(20) NOT NULL," +
            "  amount         DOUBLE NOT NULL," +
            "  date           VARCHAR(50) NOT NULL," +
            "  FOREIGN KEY (account_number)" +
            "    REFERENCES accounts(account_number)" +
            "    ON DELETE CASCADE" +
            ")"
        );

        stmt.close();
    }
}
