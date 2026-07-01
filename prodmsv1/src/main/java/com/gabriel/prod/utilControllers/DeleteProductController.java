package com.gabriel.prod.utilControllers;

import com.gabriel.prod.model.Product;
import com.gabriel.prod.serviceImpl.ProductService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class DeleteProductController implements Initializable {

    @Setter private Stage stage;
    @Setter private Scene parentScene;
    @Setter private ProdManController controller;

    // Product id stored as plain field — no FXML binding needed
    private int productId;

    @FXML public TextField tfName;
    @FXML public TextField tfDesc;
    @FXML public TextField tfUom;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("DeleteProductController: initialize");
        refresh();
    }

    public void refresh() {
        Product product = ProdManController.product;
        if (product == null) return;   // no product selected yet — safe to skip
        productId = product.getId();
        tfName.setText(product.getName());
        tfDesc.setText(product.getDescription());
        tfUom.setText(product.getUomName());
    }

    @FXML
    public void onBack(ActionEvent actionEvent) {
        System.out.println("DeleteProductController: onBack");
        stage.setTitle("Product Management");
        stage.setScene(parentScene);
        stage.show();
    }

    @FXML
    public void onSubmit(ActionEvent actionEvent) {
        try {
            ProductService.getService().delete(productId);
            try { controller.refresh(); } catch (Exception ex) {
                System.out.println("DeleteProductController: list refresh failed - " + ex.getMessage());
            }
            controller.clearControlTexts();
            // Reset cached scene so the form reloads fresh next time
            controller.setDeleteViewScene(null);
            stage.setTitle("Product Management");
            stage.setScene(parentScene);
            stage.show();
        } catch (Exception e) {
            showError("Error deleting product", e.getMessage());
        }
    }

    private void showError(String header, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.getDialogPane().setExpandableContent(new ScrollPane(new TextArea(detail)));
        alert.showAndWait();
    }
}
