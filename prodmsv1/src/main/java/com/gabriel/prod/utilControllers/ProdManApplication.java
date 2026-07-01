package com.gabriel.prod.utilControllers;

import javafx.application.Application;

public class ProdManApplication  {
    public static void main(String[] args) {

        // ── BankSys Banking Application ──────────────────────────────────────
        // Launches the new banking splash screen (splash.fxml).
        Application.launch(BankSplashApp.class, args);

        // ── Original Product Management entry point (DO NOT DELETE) ─────────
        // Uncomment the line below (and comment out the one above) to restore
        // the Product Management System splash screen.
        // Application.launch(SplashApp.class, args);
    }
}
