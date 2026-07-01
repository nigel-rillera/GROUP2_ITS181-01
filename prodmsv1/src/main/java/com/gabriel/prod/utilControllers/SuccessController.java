package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * SuccessController — displays the Account Created confirmation screen.
 *
 * The account details are injected via {@link #setAccount(Account)} immediately
 * after the FXML is loaded (before the scene is shown), so no data is
 * hardcoded in the FXML.
 *
 * Call sequence from CreateAccountController:
 *   FXMLLoader loader = new FXMLLoader(...success.fxml...);
 *   Parent root = loader.load();
 *   SuccessController ctrl = loader.getController();
 *   ctrl.setAccount(account);          ← populates labels + badge
 *   stage.getScene().setRoot(root);
 */
public class SuccessController implements Initializable {

    // ── FXML-injected controls ──────────────────────────────────────
    @FXML private Label  lblAccountNumber;
    @FXML private Label  lblOwnerName;
    @FXML private Label  lblBadge;
    @FXML private Button btnGoToSignIn;

    // ── Lifecycle ───────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Labels are populated by setAccount() which is called after load().
        // Nothing to do here.
    }

    // ── Public API: called by CreateAccountController ───────────────

    /**
     * Populates the success card with the given account's details.
     * Must be called before the scene is shown.
     *
     * @param account the Account that was just created; must not be null
     */
    public void setAccount(Account account) {
        if (account == null) throw new IllegalArgumentException("account must not be null");

        // Account number
        lblAccountNumber.setText(account.getAccountNumber());

        // Owner name
        lblOwnerName.setText(account.getOwnerName());

        // Role badge — swap CSS class and label text based on accountType
        // Account stores "CUSTOMER" or "ADMIN" (uppercase from CreateAccountController)
        String type = account.getAccountType();
        if ("ADMIN".equalsIgnoreCase(type)) {
            lblBadge.setText("Admin");
            // Remove the default customer style and apply admin style
            lblBadge.getStyleClass().removeAll("badge-customer");
            lblBadge.getStyleClass().add("badge-admin");
        } else {
            // Default: CUSTOMER
            lblBadge.setText("Customer");
            // badge-customer is already set in FXML, ensure it's present
            if (!lblBadge.getStyleClass().contains("badge-customer")) {
                lblBadge.getStyleClass().add("badge-customer");
            }
        }
    }

    // ── Button handlers ─────────────────────────────────────────────

    /**
     * Triggered by the "Go to sign in" primary button.
     * LoginController/login.fxml don't exist yet — placeholder navigation.
     */
    @FXML
    public void onGoToSignIn(ActionEvent event) {
        System.out.println("Navigate to login screen");
        try {
            FXMLLoader loader = new FXMLLoader(
                    SuccessController.class.getResource("login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("BankSys \u2013 Sign In");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            System.err.println("SuccessController: error opening login view \u2013 " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
