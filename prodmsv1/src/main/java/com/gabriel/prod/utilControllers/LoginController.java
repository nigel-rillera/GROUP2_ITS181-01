package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Account;
import com.gabriel.prod.model.AccountStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * LoginController — handles the Login screen.
 *
 * Responsibilities:
 *  1. Manage the Customer / Admin segmented tab toggle.
 *  2. Swap icon chip colours, subtitle text, visible fields, and sign-in
 *     button colour + label when the tab changes.
 *  3. Validate fields (inline error, no popups).
 *  4. Look up the entered ID in AccountStore; match on accountNumber and
 *     accountType. Any non-empty password is accepted at this stage.
 *  5. On a successful match, print a placeholder and leave a clear TODO
 *     for the dashboard screens (not built yet).
 *  6. "Back to welcome" navigates to splash.fxml.
 */
public class LoginController implements Initializable {

    // ── FXML controls ───────────────────────────────────────────────────

    // Role icon chip — StackPane gets style class swapped; Label glyph too
    @FXML private StackPane    iconChip;
    @FXML private Label        iconGlyph;

    // Dynamic subtitle
    @FXML private Label        lblSubtitle;

    // Tab toggle buttons
    @FXML private ToggleButton tabCustomer;
    @FXML private ToggleButton tabAdmin;

    // Customer field section
    @FXML private VBox         customerFields;
    @FXML private TextField    tfAccountNumber;
    @FXML private PasswordField pfCustomerPassword;

    // Admin field section
    @FXML private VBox         adminFields;
    @FXML private TextField    tfAdminId;
    @FXML private PasswordField pfAdminPassword;

    // Validation + action
    @FXML private Label        lblError;
    @FXML private Button       btnSignIn;
    @FXML private Button       btnBackToWelcome;

    // ── State ────────────────────────────────────────────────────────────

    /** Tracks which tab is currently active. Defaults to CUSTOMER. */
    private boolean isAdminTab = false;

    // ── Lifecycle ────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Default: Customer tab active — UI already set in FXML; call applyCustomerTab
        // to ensure all programmatic state is consistent with FXML defaults.
        applyCustomerTab();

        // Error label starts hidden
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    /** Retrieves the Stage from an event source (Button or ToggleButton). */
    private Stage getStage(ActionEvent event) {
        return (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void clearError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    // ── Tab visual state helpers ──────────────────────────────────────────

    /**
     * Applies Customer-tab visual state:
     *  - Icon chip: green background + green glyph
     *  - Subtitle: customer wording
     *  - Customer fields shown, Admin fields hidden
     *  - Sign-in button: blue + "Sign in as Customer"
     *  - Tab buttons: customer active style, admin inactive style
     */
    private void applyCustomerTab() {
        isAdminTab = false;

        // Icon chip
        iconChip.getStyleClass().setAll("login-icon-chip-customer");
        iconGlyph.getStyleClass().setAll("login-icon-glyph-customer");
        iconGlyph.setText("\uD83D\uDC64"); // 👤 person glyph

        // Subtitle
        lblSubtitle.setText("Sign in to your customer account to continue.");

        // Field sections
        customerFields.setVisible(true);
        customerFields.setManaged(true);
        adminFields.setVisible(false);
        adminFields.setManaged(false);

        // Sign-in button
        btnSignIn.getStyleClass().setAll("btn-signin-customer");
        btnSignIn.setText("Sign in as Customer");

        // Tab button styles
        tabCustomer.getStyleClass().setAll("tab-toggle-btn-active-customer");
        tabAdmin.getStyleClass().setAll("tab-toggle-btn");

        // Keep toggle selection state consistent
        tabCustomer.setSelected(true);
        tabAdmin.setSelected(false);
    }

    /**
     * Applies Admin-tab visual state:
     *  - Icon chip: purple background + purple glyph
     *  - Subtitle: admin wording
     *  - Admin fields shown, Customer fields hidden
     *  - Sign-in button: purple + "Sign in as Admin"
     *  - Tab buttons: admin active style, customer inactive style
     */
    private void applyAdminTab() {
        isAdminTab = true;

        // Icon chip
        iconChip.getStyleClass().setAll("login-icon-chip-admin");
        iconGlyph.getStyleClass().setAll("login-icon-glyph-admin");
        iconGlyph.setText("\uD83D\uDEE1"); // 🛡 shield glyph

        // Subtitle
        lblSubtitle.setText("Sign in to your admin account to continue.");

        // Field sections
        adminFields.setVisible(true);
        adminFields.setManaged(true);
        customerFields.setVisible(false);
        customerFields.setManaged(false);

        // Sign-in button
        btnSignIn.getStyleClass().setAll("btn-signin-admin");
        btnSignIn.setText("Sign in as Admin");

        // Tab button styles
        tabAdmin.getStyleClass().setAll("tab-toggle-btn-active-admin");
        tabCustomer.getStyleClass().setAll("tab-toggle-btn");

        // Keep toggle selection state consistent
        tabAdmin.setSelected(true);
        tabCustomer.setSelected(false);
    }

    // ── Button / Tab handlers ─────────────────────────────────────────────

    /** Customer tab clicked. */
    @FXML
    public void onTabCustomer(ActionEvent event) {
        clearError();
        applyCustomerTab();
    }

    /** Admin tab clicked. */
    @FXML
    public void onTabAdmin(ActionEvent event) {
        clearError();
        applyAdminTab();
    }

    /**
     * Sign-in button clicked.
     *
     * Validation:
     *   1. Both fields must be non-empty.
     *   2. The entered ID must exist in AccountStore with the correct accountType.
     *   3. Password: any non-empty value is accepted (no real auth yet).
     */
    @FXML
    public void onSignIn(ActionEvent event) {
        clearError();

        if (isAdminTab) {
            handleAdminSignIn(event);
        } else {
            handleCustomerSignIn(event);
        }
    }

    /** Handles sign-in logic for the Customer tab. */
    private void handleCustomerSignIn(ActionEvent event) {
        String accountNumber = tfAccountNumber.getText() == null
                ? "" : tfAccountNumber.getText().trim();
        String password = pfCustomerPassword.getText() == null
                ? "" : pfCustomerPassword.getText().trim();

        if (accountNumber.isEmpty() || password.isEmpty()) {
            showError("Please enter your credentials.");
            return;
        }

        Account found = findAccount(accountNumber, "CUSTOMER");
        if (found == null) {
            showError("Account not found. Check your account number and try again.");
            return;
        }

        if (!found.getPassword().equals(password)) {
            showError("Incorrect password.");
            return;
        }

        // Success — navigate to Customer Dashboard
        System.out.println("Navigate to customer dashboard for: " + found.getAccountNumber());
        
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource("customer_dashboard.fxml"));
            Parent root = loader.load();
            CustomerController dashCtrl = loader.getController();
            dashCtrl.setAccount(found);
            Stage stage = getStage(event);
            stage.setTitle("BankSys \u2013 Customer Dashboard");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            System.err.println("LoginController: error opening customer dashboard \u2013 " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Handles sign-in logic for the Admin tab. */
    private void handleAdminSignIn(ActionEvent event) {
        String adminId   = tfAdminId.getText() == null ? "" : tfAdminId.getText().trim();
        String password  = pfAdminPassword.getText() == null ? "" : pfAdminPassword.getText().trim();

        if (adminId.isEmpty() || password.isEmpty()) {
            showError("Please enter your credentials.");
            return;
        }

        Account found = findAccount(adminId, "ADMIN");
        if (found == null) {
            showError("Account not found. Check your Admin ID and try again.");
            return;
        }

        if (!found.getPassword().equals(password)) {
            showError("Incorrect password.");
            return;
        }

        // Success — navigate to Admin Dashboard (not built yet)
        System.out.println("Navigate to admin dashboard for: " + found.getAccountNumber());
        // TODO: load admin_dashboard.fxml here and pass the Account
        // Example (uncomment when AdminDashboardController is ready):
        //
        // try {
        //     FXMLLoader loader = new FXMLLoader(
        //             LoginController.class.getResource("admin_dashboard.fxml"));
        //     Parent root = loader.load();
        //     AdminDashboardController dashCtrl = loader.getController();
        //     dashCtrl.setAccount(found);
        //     Stage stage = getStage(event);
        //     stage.setTitle("BankSys – Admin Dashboard");
        //     stage.getScene().setRoot(root);
        // } catch (Exception ex) {
        //     System.err.println("LoginController: error opening admin dashboard – " + ex.getMessage());
        //     ex.printStackTrace();
        // }
    }

    /**
     * Searches AccountStore for an account with the given accountNumber
     * and accountType (case-insensitive comparison).
     *
     * @param accountNumber the number/ID to look up
     * @param accountType   "CUSTOMER" or "ADMIN"
     * @return the matching Account, or null if not found
     */
    private Account findAccount(String accountNumber, String accountType) {
        List<Account> accounts = AccountStore.getAccounts();
        for (Account a : accounts) {
            if (a.getAccountNumber().equalsIgnoreCase(accountNumber)
                    && a.getAccountType().equalsIgnoreCase(accountType)) {
                return a;
            }
        }
        return null;
    }

    /**
     * "← Back to welcome" clicked.
     * Navigates back to splash.fxml.
     */
    @FXML
    public void onBackToWelcome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    LoginController.class.getResource("splash.fxml"));
            Parent root = loader.load();

            Stage stage = getStage(event);
            stage.setTitle("BankSys \u2013 Banking Application");
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            System.err.println("LoginController: error navigating to splash \u2013 " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
