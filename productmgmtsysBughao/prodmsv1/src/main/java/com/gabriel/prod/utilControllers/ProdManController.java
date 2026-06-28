package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Product;
import com.gabriel.prod.serviceImpl.ProductService;
import com.gabriel.prod.serviceImpl.UomService;
import com.gabriel.prod.model.Uom;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class ProdManController implements Initializable {

    @Setter private Stage stage;
    @Setter private Scene createViewScene;
    @Setter private Scene updateViewScene;
    @Setter private Scene deleteViewScene;

    // These public fields are injected by FXML (no @FXML needed for public fields)
    public BorderPane prodman;
    public TextField tfId;
    public TextField tfName;
    public TextField tfDesc;
    public ComboBox<String> cbUom;
    public ImageView productImage;

    @FXML private ListView<Product> lvProducts;
    @FXML public Button createButton;
    @FXML public Button updateButton;
    @FXML public Button deleteButton;
    @FXML public Button closeButton;

    // Shared selected product — read by Update/Delete controllers
    public static Product product;

    private ProductService productService;
    private UpdateProductController updateProductController;
    private DeleteProductController deleteProductController;
    private CreateProductController createProductController;

    // ----------------------------------------------------------------
    // Initialise
    // ----------------------------------------------------------------
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("ProdManController: initialize");
        disableControls();
        try {
            productService = ProductService.getService();
            refresh();
            loadUoms();
            Image puffy = new Image(getClass().getResourceAsStream("/images/puffy.gif"));
            productImage.setImage(puffy);
        } catch (Exception ex) {
            showErrorDialog("Failed to load products: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    public void refresh() throws Exception {
        Product[] products = ProductService.getService().getProducts();
        lvProducts.getItems().clear();
        lvProducts.getItems().addAll(products);
    }

    public void disableControls() {
        // Fields remain editable; ComboBox is always interactive
    }

    private void loadUoms() {
        // Always add default items first
        cbUom.getItems().clear();
        cbUom.getItems().addAll("kg", "oz", "liter", "pcs", "L", "mL", "g");
        // Then try to merge UOMs from the database
        try {
            Uom[] uoms = UomService.getService().getUoms();
            for (Uom u : uoms) {
                if (!cbUom.getItems().contains(u.getName())) {
                    cbUom.getItems().add(u.getName());
                }
            }
        } catch (Exception e) {
            // Server may be offline — default items are already loaded above, no duplicates
            System.out.println("ProdManController: could not load UOMs from server - " + e.getMessage());
        }
    }

    public void setControlTexts(Product p) {
        tfId.setText(Integer.toString(p.getId()));
        tfName.setText(p.getName());
        tfDesc.setText(p.getDescription());
        cbUom.setValue(p.getUomName());
    }

    public void clearControlTexts() {
        tfId.setText("");
        tfName.setText("");
        tfDesc.setText("");
        cbUom.setValue(null);
        if (cbUom.getEditor() != null) cbUom.getEditor().clear();
    }

    public void addItem(Product p) {
        lvProducts.getItems().add(p);
    }

    // ----------------------------------------------------------------
    // List selection
    // ----------------------------------------------------------------
    public void onMouseClicked(MouseEvent mouseEvent) {
        product = lvProducts.getSelectionModel().getSelectedItem();
        if (product == null) return;
        setControlTexts(product);
        System.out.println("ProdManController: selected " + product);
    }

    // ----------------------------------------------------------------
    // Create
    // ----------------------------------------------------------------
    public void onCreate(ActionEvent actionEvent) {
        System.out.println("ProdManController: onCreate");
        Scene currentScene = createButton.getScene();
        try {
            if (createViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("create-product.fxml"));
                Parent root = fxmlLoader.load();
                createProductController = fxmlLoader.getController();
                createProductController.setStage(stage);
                createProductController.setParentScene(currentScene);
                createProductController.setProductService(productService);
                createProductController.setProdManController(this);
                createViewScene = new Scene(root, 420, 500);
            }
            createProductController.clearControlTexts();
            clearControlTexts();
            stage.setTitle("Create Product");
            stage.setScene(createViewScene);
            stage.show();
        } catch (Exception ex) {
            showErrorDialog("Error opening Create form: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ----------------------------------------------------------------
    // Update
    // ----------------------------------------------------------------
    public void onUpdate(ActionEvent actionEvent) {
        System.out.println("ProdManController: onUpdate");
        if (product == null) {
            showErrorDialog("Please select a product from the list first.");
            return;
        }
        Scene currentScene = updateButton.getScene();
        try {
            if (updateViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("update-product.fxml"));
                Parent root = fxmlLoader.load();
                updateProductController = fxmlLoader.getController();
                updateProductController.setController(this);
                updateProductController.setStage(stage);
                updateProductController.setParentScene(currentScene);
                updateViewScene = new Scene(root, 420, 500);
            } else {
                updateProductController.refresh();
            }
            stage.setTitle("Update Product");
            stage.setScene(updateViewScene);
            stage.show();
        } catch (Exception ex) {
            showErrorDialog("Error opening Update form: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ----------------------------------------------------------------
    // Delete
    // ----------------------------------------------------------------
    public void onDelete(ActionEvent actionEvent) {
        System.out.println("ProdManController: onDelete");
        if (product == null) {
            showErrorDialog("Please select a product from the list first.");
            return;
        }
        Scene currentScene = deleteButton.getScene();
        try {
            if (deleteViewScene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("delete-product.fxml"));
                Parent root = fxmlLoader.load();
                deleteProductController = fxmlLoader.getController();
                deleteProductController.setController(this);
                deleteProductController.setStage(stage);
                deleteProductController.setParentScene(currentScene);
                deleteViewScene = new Scene(root, 420, 500);
            } else {
                deleteProductController.refresh();
            }
            stage.setTitle("Delete Product");
            stage.setScene(deleteViewScene);
            stage.show();
        } catch (Exception ex) {
            showErrorDialog("Error opening Delete form: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ----------------------------------------------------------------
    // Close
    // ----------------------------------------------------------------
    public void onClose(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Platform.exit();
        }
    }

    // ----------------------------------------------------------------
    // Error dialog
    // ----------------------------------------------------------------
    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
