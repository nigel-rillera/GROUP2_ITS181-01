package com.gabriel.prod.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AccountStore — simple in-memory repository for Account objects.
 *
 * This is intentionally minimal: a static list that persists for the
 * lifetime of the JVM process. No database is used at this stage.
 *
 * Usage:
 *   AccountStore.addAccount(account);
 *   List<Account> all = AccountStore.getAccounts();
 */
public class AccountStore {

    /** Backing list — never exposed as mutable to outside callers. */
    private static final List<Account> accounts = new ArrayList<>();

    // Prevent instantiation
    private AccountStore() {}

    /**
     * Adds a new account to the in-memory store.
     *
     * @param account the Account to persist; must not be null
     */
    public static void addAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("account must not be null");
        accounts.add(account);
    }

    /**
     * Returns an unmodifiable view of all stored accounts.
     *
     * @return read-only list of Account objects
     */
    public static List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    /**
     * Clears all stored accounts (useful for testing / reset flows).
     */
    public static void clear() {
        accounts.clear();
    }
}
