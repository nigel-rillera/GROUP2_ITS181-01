package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Account;
import com.gabriel.prod.model.Transaction;
import com.gabriel.prod.model.TransactionStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionHistoryController implements Initializable {

    @FXML private Label lblOwnerName;
    @FXML private Label lblAccountNumber;
    @FXML private VBox transactionList;

    private Account account;
    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Wait for account
    }

    public void setAccount(Account account) {
        this.account = account;
        updateUI();
    }

    private void updateUI() {
        if (account == null) return;
        lblOwnerName.setText(account.getOwnerName());
        lblAccountNumber.setText(account.getAccountNumber());
        
        refreshTransactionList();
    }

    private void refreshTransactionList() {
        transactionList.getChildren().clear();
        
        List<Transaction> all = TransactionStore.getTransactionsByAccount(account.getAccountNumber());
        
        if (all.isEmpty()) {
            Label emptyLabel = new Label("No transactions yet");
            emptyLabel.getStyleClass().add("transaction-empty-label");
            transactionList.getChildren().add(emptyLabel);
            return;
        }

        for (Transaction t : all) {
            transactionList.getChildren().add(createTransactionRow(t));
        }
    }

    private HBox createTransactionRow(Transaction t) {
        HBox row = new HBox();
        row.getStyleClass().add("transaction-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(12);

        // Icon Chip
        StackPane iconChip = new StackPane();
        iconChip.setPrefSize(30, 30);
        iconChip.setMaxSize(30, 30);
        Label icon = new Label();
        
        if ("DEPOSIT".equals(t.getType())) {
            iconChip.getStyleClass().add("transaction-icon-chip-deposit");
            icon.setText("\u21A7");
            icon.getStyleClass().add("transaction-icon-glyph-deposit");
        } else {
            iconChip.getStyleClass().add("transaction-icon-chip-withdraw");
            icon.setText("\u21A5");
            icon.getStyleClass().add("transaction-icon-glyph-withdraw");
        }
        iconChip.getChildren().add(icon);

        // Details (Type + Date)
        VBox details = new VBox(2);
        Label typeLabel = new Label("DEPOSIT".equals(t.getType()) ? "Deposit" : "Withdraw");
        typeLabel.getStyleClass().add("transaction-type-label");
        Label dateLabel = new Label(t.getDate());
        dateLabel.getStyleClass().add("transaction-date-label");
        details.getChildren().addAll(typeLabel, dateLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Amount
        Label amountLabel = new Label();
        if ("DEPOSIT".equals(t.getType())) {
            amountLabel.setText("+" + currencyFormat.format(t.getAmount()));
            amountLabel.getStyleClass().add("transaction-amount-deposit");
        } else {
            amountLabel.setText("-" + currencyFormat.format(t.getAmount()));
            amountLabel.getStyleClass().add("transaction-amount-withdraw");
        }

        row.getChildren().addAll(iconChip, details, spacer, amountLabel);
        return row;
    }

    @FXML
    public void onNavDashboard(ActionEvent event) {
        navigateToDashboard(event);
    }

    @FXML
    public void onDeposit(ActionEvent event) {
        navigateToDashboard(event);
    }

    @FXML
    public void onWithdraw(ActionEvent event) {
        navigateToDashboard(event);
    }

    private void navigateToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customer_dashboard.fxml"));
            Parent root = loader.load();
            CustomerController ctrl = loader.getController();
            ctrl.setAccount(account);
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("BankSys \u2013 Dashboard");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onLogOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("BankSys \u2013 Sign In");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
