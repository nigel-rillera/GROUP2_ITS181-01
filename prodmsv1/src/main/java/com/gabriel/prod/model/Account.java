package com.gabriel.prod.model;

/**
 * Account model for BankSys Banking Application.
 * Represents a bank account with owner info, type, and balance.
 */
public class Account {

    private String accountNumber;
    private String ownerName;
    private String password;
    /** Account type: "CUSTOMER" or "ADMIN" */
    private String accountType;
    private double balance;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public Account(String accountNumber, String ownerName, String password, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.ownerName     = ownerName;
        this.password      = password;
        this.accountType   = accountType;
        this.balance       = balance;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    // ---------------------------------------------------------------
    // Setters
    // ---------------------------------------------------------------

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param accountType must be "CUSTOMER" or "ADMIN"
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // ---------------------------------------------------------------
    // Utility
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                '}';
    }
}
