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
public class UpdateProductController implements Initializable {

    @Setter private Stage stage;
    @Setter private Scene parentScene;
    @Setter private ProdManController controller;

    // Product id stored as plain field — no FXML binding needed
    private int productId;

    @FXML private TextField tfName;
    @FXML private TextField tfDesc;
    @FXML private ComboBox<Uom> cbUom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("UpdateProductController: initialize");
        try {
            refresh();
        } catch (Exception ex) {
            System.out.println("UpdateProductController: initialize error - " + ex.getMessage());
        }
    }

    public void refresh() throws Exception {
        Product product = ProdManController.product;
        if (product == null) return;   // no product selected yet — safe to skip
        productId = product.getId();
        tfName.setText(product.getName());
        tfDesc.setText(product.getDescription());
        // Load UOMs and pre-select the current one
        Uom[] uoms = UomService.getService().getUoms();
        cbUom.getItems().clear();
        cbUom.getItems().addAll(uoms);
        Uom currentUom = UomService.getService().getUom(product.getUomId());
        cbUom.getSelectionModel().select(currentUom);
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
        product.setId(productId);
        product.setName(tfName.getText().trim());
        product.setDescription(tfDesc.getText().trim());
        product.setUomId(uom.getId());
        product.setUomName(uom.getName());
        try {
            product = ProductService.getService().update(product);
            try { controller.refresh(); } catch (Exception ex) {
                System.out.println("UpdateProductController: list refresh failed - " + ex.getMessage());
            }
            controller.setControlTexts(product);
            onBack(actionEvent);
        } catch (Exception ex) {
            showError("Failed to update product: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        System.out.println("UpdateProductController: onBack");
        stage.setTitle("Product Management");
        stage.setScene(parentScene);
        stage.show();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}
