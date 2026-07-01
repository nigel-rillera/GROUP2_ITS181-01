package com.gabriel.prod.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TransactionStore — simple in-memory repository for Transaction objects.
 * Stores all transactions across the app session.
 */
public class TransactionStore {

    private static final List<Transaction> transactions = new ArrayList<>();

    private TransactionStore() {}

    /**
     * Adds a new transaction to the store (adds to the beginning so newest is first).
     * @param transaction the Transaction to persist; must not be null
     */
    public static void addTransaction(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("transaction must not be null");
        // Insert at index 0 so it naturally sorts newest-first for the UI
        transactions.add(0, transaction);
    }

    /**
     * Retrieves all transactions for a specific account.
     * @param accountNumber the account number
     * @return list of transactions for that account, newest first
     */
    public static List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equalsIgnoreCase(accountNumber))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent N transactions for an account.
     * @param accountNumber the account number
     * @param limit maximum number of transactions to return
     * @return list of up to N most recent transactions
     */
    public static List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        List<Transaction> accountTransactions = getTransactionsByAccount(accountNumber);
        if (accountTransactions.size() <= limit) {
            return accountTransactions;
        }
        return accountTransactions.subList(0, limit);
    }
    
    public static void clear() {
        transactions.clear();
    }
}
