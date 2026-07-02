package com.gabriel.prod.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountStore {

    public static List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM accounts");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account a = new Account(
                    rs.getString("account_number"),
                    rs.getString("owner_name"),
                    rs.getString("password"),
                    rs.getString("account_type"),
                    rs.getDouble("balance")
                );
                list.add(a);
            }
        } catch (Exception e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
        return list;
    }

    public static void addAccount(Account a) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO accounts " +
                "(account_number, owner_name, account_type, balance, password)" +
                " VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getOwnerName());
            ps.setString(3, a.getAccountType());
            ps.setDouble(4, a.getBalance());
            ps.setString(5, a.getPassword());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error adding account: " + e.getMessage());
        }
    }

    public static void updateAccount(Account a) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE accounts SET owner_name=?, account_type=?," +
                " balance=?, password=?" +
                " WHERE account_number=?");
            ps.setString(1, a.getOwnerName());
            ps.setString(2, a.getAccountType());
            ps.setDouble(3, a.getBalance());
            ps.setString(4, a.getPassword());
            ps.setString(5, a.getAccountNumber());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error updating account: " + e.getMessage());
        }
    }

    public static void deleteAccount(Account a) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM accounts WHERE account_number=?");
            ps.setString(1, a.getAccountNumber());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
    }

    public static boolean accountExists(String accountNumber) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return false;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM accounts WHERE account_number=?");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error checking account: " + e.getMessage());
            return false;
        }
    }

    public static Account findByAccountNumber(String accountNumber) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM accounts WHERE account_number=?");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                    rs.getString("account_number"),
                    rs.getString("owner_name"),
                    rs.getString("password"),
                    rs.getString("account_type"),
                    rs.getDouble("balance")
                );
            }
        } catch (Exception e) {
            System.err.println("Error finding account: " + e.getMessage());
        }
        return null;
    }
}
