package com.gabriel.prod.utilControllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * BankSplashApp — JavaFX Application entry point for BankSys Banking Application.
 * Loads splash.fxml as the first (and only) visible screen on startup.
 *
 * This class is intentionally separate from the original SplashApp so the
 * original Product Management entry point is left completely untouched.
 */
public class BankSplashApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("BankSplashApp: start");
        FXMLLoader fxmlLoader = new FXMLLoader(
                BankSplashApp.class.getResource("splash.fxml"));
        Parent root = fxmlLoader.load();

        // Window size: default to 1100x700 with a reasonable minimum
        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("BankSys – Banking Application");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}
