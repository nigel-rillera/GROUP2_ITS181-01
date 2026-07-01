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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    // Sidebar
    @FXML private Label lblOwnerName;
    @FXML private Label lblAccountNumber;

    // Main Content
    @FXML private Label lblBalance;
    @FXML private Label lblBalanceSub;
    @FXML private VBox transactionList;

    // Modal
    @FXML private StackPane modalOverlay;
    @FXML private StackPane modalIconChip;
    @FXML private Label modalIconGlyph;
    @FXML private Label lblModalTitle;
    @FXML private Label lblModalSubtitle;
    @FXML private TextField tfModalAmount;
    @FXML private Label lblModalError;
    @FXML private Button btnModalConfirm;

    private Account account;
    private String modalActionType; // "DEPOSIT" or "WITHDRAW"
    private final DecimalFormat currencyFormat = new DecimalFormat("₱#,##0.00");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nothing to do until account is set
    }

    public void setAccount(Account account) {
        this.account = account;
        updateUI();
    }

    private void updateUI() {
        if (account == null) return;
        
        lblOwnerName.setText(account.getOwnerName());
        lblAccountNumber.setText(account.getAccountNumber());
        lblBalanceSub.setText(account.getAccountNumber() + " • Customer account");
        
        refreshBalance();
        refreshTransactionList();
    }

    private void refreshBalance() {
        lblBalance.setText(currencyFormat.format(account.getBalance()));
    }

    private void refreshTransactionList() {
        transactionList.getChildren().clear();
        
        List<Transaction> recent = TransactionStore.getRecentTransactions(account.getAccountNumber(), 6);
        
        if (recent.isEmpty()) {
            Label emptyLabel = new Label("No transactions yet");
            emptyLabel.getStyleClass().add("transaction-empty-label");
            transactionList.getChildren().add(emptyLabel);
            return;
        }

        for (Transaction t : recent) {
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
            icon.setText("\u21A7"); // Up arrow
            icon.getStyleClass().add("transaction-icon-glyph-deposit");
        } else {
            iconChip.getStyleClass().add("transaction-icon-chip-withdraw");
            icon.setText("\u21A5"); // Down arrow
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

    // --- Sidebar Handlers ---

    @FXML
    public void onDeposit(ActionEvent event) {
        openModal("DEPOSIT");
    }

    @FXML
    public void onWithdraw(ActionEvent event) {
        openModal("WITHDRAW");
    }

    @FXML
    public void onNavTransactionHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("transaction_history.fxml"));
            Parent root = loader.load();
            TransactionHistoryController ctrl = loader.getController();
            ctrl.setAccount(account);
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("BankSys \u2013 Transaction History");
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

    // --- Action Card Handlers ---

    @FXML
    public void onDepositClick(MouseEvent event) {
        openModal("DEPOSIT");
    }

    @FXML
    public void onWithdrawClick(MouseEvent event) {
        openModal("WITHDRAW");
    }

    // --- Modal Logic ---

    private void openModal(String type) {
        modalActionType = type;
        tfModalAmount.clear();
        lblModalError.setVisible(false);
        lblModalError.setManaged(false);

        modalIconChip.getStyleClass().removeAll("action-icon-chip-deposit", "action-icon-chip-withdraw");
        modalIconGlyph.getStyleClass().removeAll("action-icon-glyph-deposit", "action-icon-glyph-withdraw");
        btnModalConfirm.getStyleClass().removeAll("btn-modal-deposit", "btn-modal-withdraw");

        if ("DEPOSIT".equals(type)) {
            modalIconChip.getStyleClass().add("action-icon-chip-deposit");
            modalIconGlyph.setText("\u21A7");
            modalIconGlyph.getStyleClass().add("action-icon-glyph-deposit");
            
            lblModalTitle.setText("Deposit funds");
            lblModalSubtitle.setText("Enter the amount you want to deposit.");
            
            btnModalConfirm.setText("Deposit");
            btnModalConfirm.getStyleClass().add("btn-modal-deposit");
        } else {
            modalIconChip.getStyleClass().add("action-icon-chip-withdraw");
            modalIconGlyph.setText("\u21A5");
            modalIconGlyph.getStyleClass().add("action-icon-glyph-withdraw");
            
            lblModalTitle.setText("Withdraw funds");
            lblModalSubtitle.setText("Enter the amount you want to withdraw.");
            
            btnModalConfirm.setText("Withdraw");
            btnModalConfirm.getStyleClass().add("btn-modal-withdraw");
        }

        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
        tfModalAmount.requestFocus();
    }

    @FXML
    public void onModalCancel(ActionEvent event) {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    @FXML
    public void onModalConfirm(ActionEvent event) {
        String amountText = tfModalAmount.getText();
        if (amountText == null || amountText.trim().isEmpty()) {
            showModalError("Please enter an amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (NumberFormatException e) {
            showModalError("Invalid amount format.");
            return;
        }

        if (amount <= 0) {
            showModalError("Amount must be greater than 0.");
            return;
        }

        if ("WITHDRAW".equals(modalActionType)) {
            if (amount > account.getBalance()) {
                showModalError("Insufficient balance.");
                return;
            }
            account.setBalance(account.getBalance() - amount);
        } else {
            account.setBalance(account.getBalance() + amount);
        }

        // Create transaction
        String dateStr = LocalDate.now().format(dateFormatter);
        Transaction tx = new Transaction(modalActionType, amount, dateStr, account.getAccountNumber());
        TransactionStore.addTransaction(tx);

        // Update UI
        refreshBalance();
        refreshTransactionList();

        // Close modal
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    private void showModalError(String msg) {
        lblModalError.setText(msg);
        lblModalError.setVisible(true);
        lblModalError.setManaged(true);
    }
}
