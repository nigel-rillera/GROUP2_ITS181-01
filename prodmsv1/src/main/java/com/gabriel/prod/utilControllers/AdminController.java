package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Account;
import com.gabriel.prod.model.AccountStore;
import com.gabriel.prod.model.TransactionStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * AdminController — backs the Admin Dashboard (admin_dashboard.fxml).
 *
 * Responsibilities:
 *  1. Display account statistics (total, customers, admins) live.
 *  2. Show all accounts in a searchable TableView.
 *  3. Provide Create, Modify, and Delete operations via modal overlays
 *     that reuse the same StackPane-based overlay mechanism as the
 *     Customer Dashboard.
 *  4. Log out back to login.fxml.
 */
public class AdminController implements Initializable {

    // ── Sidebar ──────────────────────────────────────────────────────────────
    @FXML private Label lblAdminName;
    @FXML private Label lblAdminAccountNumber;

    // ── Stat Cards ───────────────────────────────────────────────────────────
    @FXML private Label lblTotalAccounts;
    @FXML private Label lblCustomerCount;
    @FXML private Label lblAdminCount;

    // ── Search ───────────────────────────────────────────────────────────────
    @FXML private TextField tfSearch;

    // ── Table ────────────────────────────────────────────────────────────────
    @FXML private TableView<Account> accountTable;
    @FXML private TableColumn<Account, String> colAccountNumber;
    @FXML private TableColumn<Account, String> colOwnerName;
    @FXML private TableColumn<Account, String> colType;
    @FXML private TableColumn<Account, Double> colBalance;
    @FXML private TableColumn<Account, Void>   colActions;

    // ── Modal Overlay (shared shell) ─────────────────────────────────────────
    @FXML private StackPane  modalOverlay;
    @FXML private StackPane  modalIconChip;
    @FXML private Label      modalIconGlyph;
    @FXML private Label      lblModalTitle;
    @FXML private Label      lblModalSubtitle;

    // Fields shown in Create + Modify modals
    @FXML private VBox       modalFieldsSection;      // shown for Create & Modify
    @FXML private TextField  tfModalAccNumber;
    @FXML private TextField  tfModalOwnerName;
    @FXML private PasswordField pfModalPassword;
    @FXML private ComboBox<String> cbModalType;
    @FXML private TextField  tfModalBalance;

    // Info pill shown only in Delete modal
    @FXML private VBox       modalDeleteSection;      // shown only for Delete
    @FXML private Label      lblDeleteInfo;

    @FXML private Label      lblModalError;
    @FXML private Button     btnModalConfirm;

    // ── State ────────────────────────────────────────────────────────────────
    private Account loggedInAdmin;

    /** Which modal mode is currently open: "CREATE", "MODIFY", "DELETE" */
    private String modalMode;

    /** The Account row currently selected for Modify or Delete. */
    private Account selectedAccount;

    private final DecimalFormat currencyFmt = new DecimalFormat("₱#,##0.00");

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        accountTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupTable();
        setupSearch();
        refreshStats();

        // ComboBox options
        cbModalType.setItems(FXCollections.observableArrayList("Customer", "Admin"));

        // Modal starts hidden
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    /**
     * Called by LoginController after loading this screen.
     * Populates the sidebar labels with the logged-in admin's details.
     */
    public void setAccount(Account account) {
        this.loggedInAdmin = account;
        if (account != null) {
            lblAdminName.setText(account.getOwnerName());
            lblAdminAccountNumber.setText(account.getAccountNumber());
        }
    }

    // ── Table Setup ──────────────────────────────────────────────────────────

    private void setupTable() {
        colAccountNumber.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colOwnerName.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

        // Type column — colored role pill
        colType.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        colType.setCellFactory(col -> new TableCell<>() {
            private final Label pill = new Label();
            {
                pill.setMaxWidth(Double.MAX_VALUE);
                pill.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    pill.setText(type.equalsIgnoreCase("ADMIN") ? "Admin" : "Customer");
                    pill.getStyleClass().setAll(
                            type.equalsIgnoreCase("ADMIN") ? "badge-admin" : "badge-customer");
                    setGraphic(pill);
                    setText(null);
                }
            }
        });

        // Balance column — currency formatted
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colBalance.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                setText(empty || balance == null ? null : currencyFmt.format(balance));
            }
        });

        // Actions column — edit (pencil) + delete (trash) icon buttons
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit   = new Button("\u270E");   // ✎
            private final Button btnDelete = new Button("\uD83D\uDDD1"); // 🗑

            {
                btnEdit.getStyleClass().add("admin-action-btn-edit");
                btnDelete.getStyleClass().add("admin-action-btn-delete");

                btnEdit.setOnAction(e -> {
                    Account acc = getTableView().getItems().get(getIndex());
                    openModifyModal(acc);
                });
                btnDelete.setOnAction(e -> {
                    Account acc = getTableView().getItems().get(getIndex());
                    openDeleteModal(acc);
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, btnEdit, btnDelete);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
    }

    // ── Search / Filter ──────────────────────────────────────────────────────

    private void setupSearch() {
        // Wrap the store's observable list in a FilteredList so that
        // typing in the search bar immediately narrows the table rows.
        javafx.collections.transformation.FilteredList<Account> filtered = new javafx.collections.transformation.FilteredList<>(
                javafx.collections.FXCollections.observableArrayList(AccountStore.getAllAccounts()), a -> true);

        tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.trim().toLowerCase();
            filtered.setPredicate(a -> {
                if (query.isEmpty()) return true;
                return a.getAccountNumber().toLowerCase().contains(query)
                        || a.getOwnerName().toLowerCase().contains(query);
            });
        });

        accountTable.setItems(filtered);

        // Listener removed since stats are now manually refreshed. ────────────────────────────────────────────────────
    }

    // ── Stat Card Refresh ────────────────────────────────────────────────────

    private void refreshStats() {
        java.util.List<Account> accounts = AccountStore.getAllAccounts();
        long total     = accounts.size();
        long customers = accounts.stream()
                .filter(a -> "CUSTOMER".equalsIgnoreCase(a.getAccountType()))
                .count();
        long admins    = total - customers;

        lblTotalAccounts.setText(String.valueOf(total));
        lblCustomerCount.setText(String.valueOf(customers));
        lblAdminCount.setText(String.valueOf(admins));
    }

    // ── Sidebar Handlers ─────────────────────────────────────────────────────

    @FXML
    public void onLogOut(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("BankSys \u2013 Sign In");
            stage.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── New Account Button ────────────────────────────────────────────────────

    @FXML
    public void onNewAccount(ActionEvent event) {
        openCreateModal();
    }

    // ── Modal Helpers ─────────────────────────────────────────────────────────

    /** Opens the Create Account modal with a green icon chip. */
    private void openCreateModal() {
        modalMode = "CREATE";
        selectedAccount = null;

        // Icon chip — green (Create)
        modalIconChip.getStyleClass().setAll("admin-modal-chip-create");
        modalIconGlyph.getStyleClass().setAll("admin-modal-glyph-create");
        modalIconGlyph.setText("\uD83D\uDC64"); // 👤

        lblModalTitle.setText("Create new account");
        lblModalSubtitle.setText("Fill in the account details below.");

        // Show fields section, hide delete section
        modalFieldsSection.setVisible(true);
        modalFieldsSection.setManaged(true);
        modalDeleteSection.setVisible(false);
        modalDeleteSection.setManaged(false);

        // Clear all fields
        tfModalAccNumber.clear();
        tfModalAccNumber.setDisable(false);
        tfModalOwnerName.clear();
        pfModalPassword.clear();
        cbModalType.getSelectionModel().select("Customer");
        tfModalBalance.clear();
        tfModalBalance.setPromptText("0.00");

        // Confirm button — green
        btnModalConfirm.getStyleClass().setAll("admin-btn-modal-create");
        btnModalConfirm.setText("Create");

        clearModalError();
        showModal();
    }

    /** Opens the Modify Account modal pre-filled with the selected account's data. */
    private void openModifyModal(Account acc) {
        modalMode = "MODIFY";
        selectedAccount = acc;

        // Icon chip — blue (Modify)
        modalIconChip.getStyleClass().setAll("admin-modal-chip-modify");
        modalIconGlyph.getStyleClass().setAll("admin-modal-glyph-modify");
        modalIconGlyph.setText("\u270E"); // ✎

        lblModalTitle.setText("Modify account");
        lblModalSubtitle.setText("Update the account details below.");

        // Show fields section, hide delete section
        modalFieldsSection.setVisible(true);
        modalFieldsSection.setManaged(true);
        modalDeleteSection.setVisible(false);
        modalDeleteSection.setManaged(false);

        // Pre-fill & lock account number
        tfModalAccNumber.setText(acc.getAccountNumber());
        tfModalAccNumber.setDisable(true);
        tfModalOwnerName.setText(acc.getOwnerName());
        pfModalPassword.setText(acc.getPassword());
        cbModalType.getSelectionModel().select(
                "ADMIN".equalsIgnoreCase(acc.getAccountType()) ? "Admin" : "Customer");
        tfModalBalance.setText(String.valueOf(acc.getBalance()));
        tfModalBalance.setPromptText("Balance");

        // Confirm button — blue
        btnModalConfirm.getStyleClass().setAll("admin-btn-modal-modify");
        btnModalConfirm.setText("Save changes");

        clearModalError();
        showModal();
    }

    /** Opens the Delete Account modal with a read-only info pill. */
    private void openDeleteModal(Account acc) {
        modalMode = "DELETE";
        selectedAccount = acc;

        // Icon chip — red (Delete)
        modalIconChip.getStyleClass().setAll("admin-modal-chip-delete");
        modalIconGlyph.getStyleClass().setAll("admin-modal-glyph-delete");
        modalIconGlyph.setText("\uD83D\uDDD1"); // 🗑

        lblModalTitle.setText("Delete account");
        lblModalSubtitle.setText("This action cannot be undone. Are you sure you want to delete this account?");

        // Hide fields section, show delete info pill section
        modalFieldsSection.setVisible(false);
        modalFieldsSection.setManaged(false);
        modalDeleteSection.setVisible(true);
        modalDeleteSection.setManaged(true);

        String typeLabel = "ADMIN".equalsIgnoreCase(acc.getAccountType()) ? "Admin" : "Customer";
        lblDeleteInfo.setText(acc.getAccountNumber() + "  •  " + acc.getOwnerName() + "  •  " + typeLabel);

        // Confirm button — red
        btnModalConfirm.getStyleClass().setAll("admin-btn-modal-delete");
        btnModalConfirm.setText("Delete");

        clearModalError();
        showModal();
    }

    private void showModal() {
        modalOverlay.setVisible(true);
        modalOverlay.setManaged(true);
    }

    private void hideModal() {
        modalOverlay.setVisible(false);
        modalOverlay.setManaged(false);
    }

    private void showModalError(String msg) {
        lblModalError.setText(msg);
        lblModalError.setVisible(true);
        lblModalError.setManaged(true);
    }

    private void clearModalError() {
        lblModalError.setText("");
        lblModalError.setVisible(false);
        lblModalError.setManaged(false);
    }

    // ── Modal Action Handlers ─────────────────────────────────────────────────

    @FXML
    public void onModalCancel(ActionEvent event) {
        hideModal();
    }

    @FXML
    public void onModalConfirm(ActionEvent event) {
        switch (modalMode) {
            case "CREATE" -> handleCreate();
            case "MODIFY" -> handleModify();
            case "DELETE" -> handleDelete();
        }
    }

    // ── Create ────────────────────────────────────────────────────────────────

    private void handleCreate() {
        String accNum   = tfModalAccNumber.getText() == null ? "" : tfModalAccNumber.getText().trim();
        String owner    = tfModalOwnerName.getText() == null ? "" : tfModalOwnerName.getText().trim();
        String password = pfModalPassword.getText() == null ? "" : pfModalPassword.getText().trim();
        String typeStr  = cbModalType.getSelectionModel().getSelectedItem();
        String balStr   = tfModalBalance.getText() == null ? "" : tfModalBalance.getText().trim();

        // Required field validation
        if (accNum.isEmpty() || owner.isEmpty() || password.isEmpty() || typeStr == null) {
            showModalError("Please fill in all fields.");
            return;
        }

        // Duplicate account-number check
        boolean duplicate = AccountStore.accountExists(accNum);
        if (duplicate) {
            showModalError("Account number already exists.");
            return;
        }

        // Balance validation
        double balance = 0.0;
        if (!balStr.isEmpty()) {
            try {
                balance = Double.parseDouble(balStr);
                if (balance < 0) {
                    showModalError("Balance must be a non-negative number.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showModalError("Balance must be a valid number.");
                return;
            }
        }

        String accountType = "Admin".equals(typeStr) ? "ADMIN" : "CUSTOMER";
        Account newAcc = new Account(accNum, owner, password, accountType, balance);
        AccountStore.addAccount(newAcc);
        
        // Refresh table and stats
        accountTable.setItems(new javafx.collections.transformation.FilteredList<>(
                javafx.collections.FXCollections.observableArrayList(AccountStore.getAllAccounts()), a -> true));
        refreshStats();
        hideModal();
    }

    // ── Modify ────────────────────────────────────────────────────────────────

    private void handleModify() {
        String owner    = tfModalOwnerName.getText() == null ? "" : tfModalOwnerName.getText().trim();
        String password = pfModalPassword.getText() == null ? "" : pfModalPassword.getText().trim();
        String typeStr  = cbModalType.getSelectionModel().getSelectedItem();
        String balStr   = tfModalBalance.getText() == null ? "" : tfModalBalance.getText().trim();

        if (owner.isEmpty() || password.isEmpty() || typeStr == null) {
            showModalError("Owner name, password, and account type cannot be empty.");
            return;
        }

        double balance = 0.0;
        if (balStr.isEmpty()) {
            showModalError("Balance cannot be empty.");
            return;
        }
        try {
            balance = Double.parseDouble(balStr);
            if (balance < 0) {
                showModalError("Balance must be a non-negative number.");
                return;
            }
        } catch (NumberFormatException ex) {
            showModalError("Balance must be a valid number.");
            return;
        }

        // Mutate in place — the ObservableList will fire change events automatically
        selectedAccount.setOwnerName(owner);
        selectedAccount.setPassword(password);
        selectedAccount.setAccountType("Admin".equals(typeStr) ? "ADMIN" : "CUSTOMER");
        selectedAccount.setBalance(balance);

        AccountStore.updateAccount(selectedAccount);

        // Force table refresh
        accountTable.setItems(new javafx.collections.transformation.FilteredList<>(
                javafx.collections.FXCollections.observableArrayList(AccountStore.getAllAccounts()), a -> true));
        refreshStats();
        hideModal();
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    private void handleDelete() {
        // Safeguard: cannot delete the currently logged-in admin account
        if (loggedInAdmin != null
                && loggedInAdmin.getAccountNumber()
                        .equalsIgnoreCase(selectedAccount.getAccountNumber())) {
            showModalError("You cannot delete the account you are currently logged in as.");
            return;
        }

        // Remove from store
        AccountStore.deleteAccount(selectedAccount);
        
        // Refresh table and stats
        accountTable.setItems(new javafx.collections.transformation.FilteredList<>(
                javafx.collections.FXCollections.observableArrayList(AccountStore.getAllAccounts()), a -> true));
        refreshStats();
        hideModal();
    }
}
