package com.gabriel.prod.utilControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * BankSplashController — handles user interactions on the BankSys splash / landing screen.
 *
 * Navigation:
 *   - "Sign in to existing account" → login.fxml (TODO, not built yet)
 *   - "Create a new account"        → create_account.fxml  ← LIVE
 */
public class BankSplashController implements Initializable {

    @FXML
    private Button btnSignIn;

    @FXML
    private Button btnCreateAccount;

    // ── Lifecycle ──────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Static screen — nothing to initialise.
    }

    // ── Helpers ────────────────────────────────────────────────────────

    /** Retrieves the Stage from any event source button. */
    private Stage getStage(ActionEvent event) {
        return (Stage) ((Button) event.getSource()).getScene().getWindow();
    }

    // ── Button Handlers ────────────────────────────────────────────────

    /**
     * Called when the user clicks "Sign in to existing account".
     * Navigates to the Login screen.
     */
    @FXML
    public void onSignIn(ActionEvent event) {
        System.out.println("Navigate to login");
        try {
            FXMLLoader loader = new FXMLLoader(
                    BankSplashController.class.getResource("login.fxml"));
            Parent root = loader.load();
            
            Stage stage = getStage(event);
            stage.setTitle("BankSys \u2013 Sign In");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            System.err.println("BankSplashController: error opening login view - " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Called when the user clicks "Create a new account".
     * Navigates to the Create Account screen (create_account.fxml).
     */
    @FXML
    public void onCreateAccount(ActionEvent event) {
        System.out.println("Navigate to create account");
        try {
            FXMLLoader loader = new FXMLLoader(
                    BankSplashController.class.getResource("create_account.fxml"));
            Parent root = loader.load();

            Stage stage = getStage(event);
            stage.setTitle("BankSys \u2013 Create Account");
            // Swap the scene root so the window size stays the same
            stage.getScene().setRoot(root);

        } catch (Exception ex) {
            System.err.println("BankSplashController: error opening create account view - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
