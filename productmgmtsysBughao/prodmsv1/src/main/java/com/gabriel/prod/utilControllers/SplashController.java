package com.gabriel.prod.utilControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class SplashController implements Initializable {

    @Setter
    private Stage stage;

    @FXML
    public ImageView productImage;

    @FXML
    private Button btnProceed;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image image = new Image(getClass().getResourceAsStream("/images/wink.gif"));
            productImage.setImage(image);
        } catch (Exception ex) {
            System.out.println("SplashController: could not load image - " + ex.getMessage());
        }
    }

    @FXML
    public void onProceed(ActionEvent actionEvent) {
        System.out.println("SplashController: onProceed");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("prodman-view.fxml"));
            Parent root = fxmlLoader.load();
            ProdManController prodManController = fxmlLoader.getController();
            prodManController.setStage(stage);
            Scene scene = new Scene(root, 680, 620);
            stage.setTitle("Product Management");
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            System.out.println("SplashController: error opening main view - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
