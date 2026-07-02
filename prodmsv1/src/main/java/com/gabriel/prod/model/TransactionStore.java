package com.gabriel.prod.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionStore {

    public static void addTransaction(Transaction t) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO transactions " +
                "(account_number, type, amount, date) VALUES (?, ?, ?, ?)");
            ps.setString(1, t.getAccountNumber());
            ps.setString(2, t.getType());
            ps.setDouble(3, t.getAmount());
            ps.setString(4, t.getDate());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error adding transaction: " + e.getMessage());
        }
    }

    public static List<Transaction> getTransactionsByAccount(
            String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn == null) return list;
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM transactions WHERE account_number=?" +
                " ORDER BY id DESC");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction(
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getString("date"),
                    rs.getString("account_number")
                );
                list.add(t);
            }
        } catch (Exception e) {
            System.err.println(
                "Error loading transactions: " + e.getMessage());
        }
        return list;
    }
}
