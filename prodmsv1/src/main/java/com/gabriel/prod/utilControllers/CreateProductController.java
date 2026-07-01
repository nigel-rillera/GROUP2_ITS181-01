package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Product;
import com.gabriel.prod.model.Uom;
import com.gabriel.prod.serviceImpl.ProductService;
import com.gabriel.prod.serviceImpl.UomService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class CreateProductController implements Initializable {

    @Setter private Stage stage;
    @Setter private Scene parentScene;
    @Setter private ProductService productService;
    @Setter private ProdManController prodManController;

    @FXML public TextField tfName;
    @FXML public TextField tfDesc;
    @FXML private ComboBox<Uom> cbUom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("CreateProductController: initialize");
        try {
            Uom[] uoms = UomService.getService().getUoms();
            cbUom.getItems().clear();
            cbUom.getItems().addAll(uoms);
        } catch (Exception e) {
            System.out.println("CreateProductController: failed to load UOMs - " + e.getMessage());
        }
        clearControlTexts();
    }

    public void clearControlTexts() {
        tfName.setText("");
        tfDesc.setText("");
        if (cbUom != null) cbUom.getSelectionModel().clearSelection();
    }

    @FXML
    public void onSubmit(ActionEvent actionEvent) {
        if (tfName.getText().trim().isEmpty()) {
            showError("Product name cannot be empty.");
            return;
        }
        Uom uom = cbUom.getSelectionModel().getSelectedItem();
        if (uom == null) {
            showError("Please select a Unit of Measure.");
            return;
        }
        Product product = new Product();
        product.setName(tfName.getText().trim());
        product.setDescription(tfDesc.getText().trim());
        product.setUomId(uom.getId());
        product.setUomName(uom.getName());
        try {
            product = productService.create(product);
            try { prodManController.refresh(); } catch (Exception ex) {
                System.out.println("CreateProductController: list refresh failed - " + ex.getMessage());
            }
            onBack(actionEvent);
        } catch (Exception ex) {
            showError("Failed to create product: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        System.out.println("CreateProductController: onBack");
        stage.setTitle("Product Management");
        stage.setScene(parentScene);
        stage.show();
    }

    @FXML
    public void onNext(ActionEvent actionEvent) {
        onBack(actionEvent);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}
