package com.gabriel.prod.model;

/**
 * Transaction model for BankSys Banking Application.
 * Represents a single deposit or withdrawal for an account.
 */
public class Transaction {
    
    private String type; // "DEPOSIT" or "WITHDRAW"
    private double amount;
    private String date; // E.g. "June 28, 2026"
    private String accountNumber;

    public Transaction(String type, double amount, String date, String accountNumber) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "type='" + type + '\'' +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }
}
