package com.philstar.stargate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StarGateApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.setTitle("StarGate");
        stage.setResizable(true);
        showLogin();
        stage.show();
    }

    public static void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(StarGateApp.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 340);
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(340);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(340);
        primaryStage.centerOnScreen();
    }

    public static void showAdmin() throws IOException {
        FXMLLoader loader = new FXMLLoader(StarGateApp.class.getResource("admin.fxml"));
        Scene scene = new Scene(loader.load(), 820, 580);
        Stage adminStage = new Stage();
        adminStage.setTitle("StarGate — Admin");
        adminStage.setScene(scene);
        adminStage.setMinWidth(600);
        adminStage.setMinHeight(400);
        adminStage.initOwner(primaryStage);
        adminStage.show();
    }

    public static void showMain() throws IOException {
        FXMLLoader loader = new FXMLLoader(StarGateApp.class.getResource("main.fxml"));
        Scene scene = new Scene(loader.load(), 960, 640);
        primaryStage.setScene(scene);
        primaryStage.setWidth(960);
        primaryStage.setHeight(640);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(480);
        primaryStage.centerOnScreen();
    }

    @Override
    public void stop() {
        AppState.get().shutdown();
    }
}
