package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Account;
import com.gabriel.prod.model.AccountStore;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * CreateAccountController — handles the Create Account screen.
 *
 * Responsibilities:
 *  1. Populate the account-type ComboBox with "Customer" and "Admin".
 *  2. Validate that all three fields are filled before proceeding.
 *  3. On valid submission: create an Account, store it in AccountStore,
 *     then navigate to the Success screen passing the new Account.
 *  4. "Cancel" and back-arrow both navigate back to splash.fxml.
 */
public class CreateAccountController implements Initializable {

    // ── FXML-injected controls ──────────────────────────────────────
    @FXML private TextField  tfAccountNumber;
    @FXML private TextField  tfOwnerName;
    @FXML private PasswordField pfPassword;
    @FXML private ComboBox<String> cbAccountType;
    @FXML private Button     btnCreateAccount;
    @FXML private Button     btnCancel;
    @FXML private Button     btnBack;
    @FXML private Label      lblError;

    // ── Lifecycle ───────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate ComboBox options exactly as specified
        cbAccountType.setItems(FXCollections.observableArrayList("Customer", "Admin"));
        // No pre-selection — the promptText "Select account type" shows by default.

        // Hide the error label until validation fires
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    // ── Helpers ─────────────────────────────────────────────────────

    /** Retrieves the Stage from any event source button. */
    private Stage getStage(ActionEvent event) {
        return (Stage) ((Button) event.getSource()).getScene().getWindow();
    }

    /** Shows or hides the inline error message. */
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

    // ── Button handlers ─────────────────────────────────────────────

    /**
     * Triggered by the "✓ Create account" primary button.
     * Validates fields, creates Account, stores it, then navigates to Success.
     */
    @FXML
    public void onCreateAccount(ActionEvent event) {
        clearError();

        // ── Validation ───────────────────────────────────────────
        String accountNumber = tfAccountNumber.getText() == null
                ? "" : tfAccountNumber.getText().trim();
        String ownerName     = tfOwnerName.getText() == null
                ? "" : tfOwnerName.getText().trim();
        String password      = pfPassword.getText() == null
                ? "" : pfPassword.getText().trim();
        String selectedType  = cbAccountType.getValue();

        if (accountNumber.isEmpty() || ownerName.isEmpty() || password.isEmpty() || selectedType == null) {
            showError("Please fill in all fields.");
            return;
        }

        // ── Map display name → internal type constant ─────────────
        // ComboBox shows "Customer" / "Admin"; Account stores "CUSTOMER" / "ADMIN"
        String accountType = selectedType.toUpperCase();

        // ── Create and store the account ──────────────────────────
        Account newAccount = new Account(accountNumber, ownerName, password, accountType, 0.0);
        AccountStore.addAccount(newAccount);
        System.out.println("Account created: " + newAccount);

        // ── Navigate to Success screen ────────────────────────────
        try {
            FXMLLoader loader = new FXMLLoader(
                    CreateAccountController.class.getResource("success.fxml"));
            Parent root = loader.load();

            // Pass the new account to the Success controller before showing
            SuccessController successController = loader.getController();
            successController.setAccount(newAccount);

            Stage stage = getStage(event);
            stage.setTitle("BankSys – Account Created");
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            System.err.println("CreateAccountController: error opening success view – " + ex.getMessage());
            ex.printStackTrace();
            showError("An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Triggered by the "Cancel" ghost button.
     * Navigates back to splash.fxml.
     */
    @FXML
    public void onCancel(ActionEvent event) {
        navigateToSplash(event);
    }

    /**
     * Triggered by the back-arrow (←) button in the card header.
     * Navigates back to splash.fxml.
     */
    @FXML
    public void onBack(ActionEvent event) {
        navigateToSplash(event);
    }

    // ── Navigation helper ───────────────────────────────────────────

    /** Loads splash.fxml and swaps it into the current scene root. */
    private void navigateToSplash(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    CreateAccountController.class.getResource("splash.fxml"));
            Parent root = loader.load();

            Stage stage = getStage(event);
            stage.setTitle("BankSys – Banking Application");
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            System.err.println("CreateAccountController: error navigating to splash – " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
