package com.gabriel.prod.utilControllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashApp extends Application {

    public void start(Stage stage) throws IOException {
        System.out.println("SplashApp: start");
        FXMLLoader fxmlLoader = new FXMLLoader(SplashApp.class.getResource("splash-view.fxml"));
        Parent root = fxmlLoader.load();
        SplashController splashController = fxmlLoader.getController();
        splashController.setStage(stage);
        Scene scene = new Scene(root, 360, 520);
        stage.setTitle("Product Management System");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
