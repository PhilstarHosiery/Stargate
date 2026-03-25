package com.philstar.stargate.controllers;

import com.philstar.stargate.AppState;
import com.philstar.stargate.GrpcClient;
import com.philstar.stargate.StarGateApp;
import com.philstar.stargate.proto.Group;
import com.philstar.stargate.proto.LoginResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.List;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Label         errorLabel;

    @FXML
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setText("");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                GrpcClient grpc = new GrpcClient();

                LoginResponse resp = grpc.login(username, password);
                if (!resp.getSuccess()) {
                    grpc.shutdown();
                    Platform.runLater(() -> {
                        errorLabel.setText(resp.getErrorMessage());
                        loginButton.setDisable(false);
                    });
                    return null;
                }

                List<Group> groups = grpc.listGroups(resp.getUserId());

                Platform.runLater(() -> {
                    AppState state = AppState.get();
                    state.setUser(resp.getUserId(), username);
                    state.setGrpc(grpc);
                    state.loadGroups(groups);
                    try {
                        StarGateApp.showMain();
                    } catch (Exception e) {
                        errorLabel.setText("Failed to open main screen.");
                        loginButton.setDisable(false);
                    }
                });
                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() -> {
            errorLabel.setText("Connection failed: " + task.getException().getMessage());
            loginButton.setDisable(false);
        }));

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
