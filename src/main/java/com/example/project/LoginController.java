package com.example.project;

import Database.Connect;
import Exceptions.InvalidPassword;
import Exceptions.UserExists;
import Exceptions.UserNotFound;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.example.project.AfterLogin.showAlert;

public class LoginController {
    private final HashSet<String> usernames = new HashSet<>();
    private final Connection con = Connect.connect();
    private static String user;
    @FXML
    private Button createButton;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private TextField usernameTextfield;
    @FXML
    private TextField passTextfield;
    @FXML
    private PasswordField signUpPasswordField;
    @FXML
    private TextField signupUsernameField;
    @FXML
    private TextField phoneNoField;
    @FXML
    private TextField emailField;
    private boolean isSignUpState = false;

    // On Click: Toggle between Login and SignUp
    public void onClickCreateButton(ActionEvent e) {
        double rightX = this.rightPane.getLayoutX();
        TranslateTransition translate = new TranslateTransition(Duration.millis(500.0), this.leftPane);
        if (!this.isSignUpState) {
            translate.setToX(rightX);
            this.isSignUpState = true;
            this.createButton.setText("Login");
        } else {
            translate.setToX(0.0);
            this.isSignUpState = false;
            this.createButton.setText("Create Account");
        }

        translate.play();
    }

    // On Click: Handle Login Button Click
    public void onClickLoginButton(ActionEvent event) {
        String username = this.usernameTextfield.getText();
        String pass = this.passTextfield.getText();
        String query = "SELECT password FROM users WHERE name=?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = this.con.prepareStatement(query);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new UserNotFound("User Not Found");
            }

            String password = rs.getString("password");
            if (!password.equals(pass)) {
                throw new InvalidPassword("Invalid Password");
            }

            user = username;
            showAlert(Alert.AlertType.INFORMATION, "Login Status", "Logged in Successfully", event);

            // If login is successful, load the AfterLogin.fxml scene
            changeSceneToAfterLogin(event);

        } catch (InvalidPassword | UserNotFound var18) {
            showAlert(Alert.AlertType.ERROR, "Login Error", var18.getMessage(), event);
        } catch (SQLException var19) {
            System.out.println("SQL Error: " + var19.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException var17) {
                System.err.println("Error closing resources: " + var17.getMessage());
            }
        }
    }

    // Method to change the scene to AfterLogin.fxml
    private void changeSceneToAfterLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AfterLogin.fxml"));
            Scene newScene = new Scene(fxmlLoader.load());

            // Get the current stage (the window of the button that triggered the event)
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Set the new scene to the current stage
            currentStage.setScene(newScene);

            // Optionally set properties for the new window (e.g., size, position)
            currentStage.setX(50);
            currentStage.setY(50);
            currentStage.setFullScreen(true);
            currentStage.setOnCloseRequest(_ -> currentStage.close());

            // Show the new scene
            currentStage.show();

        } catch (IOException e) {
            System.out.println("Error loading AfterLogin scene: " + e.getMessage());
        }
    }

    // On Click: Handle Register Button Click
    public void onClickRegisterButton(ActionEvent event) {
        String username = this.signupUsernameField.getText();
        String password = this.signUpPasswordField.getText();
        String phoneNo = this.phoneNoField.getText();
        String email = this.emailField.getText();
        if (!username.isEmpty() && !password.isEmpty() && !phoneNo.isEmpty() && !email.isEmpty()) {
            PreparedStatement ps = null;

            try {
                if (this.usernames.contains(username)) {
                    throw new UserExists("Username already exists. Please choose a different username.");
                }

                String insertQuery = "INSERT INTO users (name, password, phone, email) VALUES (?, ?, ?, ?)";
                ps = this.con.prepareStatement(insertQuery);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, phoneNo);
                ps.setString(4, email);
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Registration Status", "Account created successfully!", event);
                    this.signupUsernameField.clear();
                    this.signUpPasswordField.clear();
                    this.phoneNoField.clear();
                    this.emailField.clear();
                    this.usernames.add(username);
                    this.onClickCreateButton(event);
                }
            } catch (UserExists var19) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", var19.getMessage(), event);
            } catch (SQLException var20) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred: " + var20.getMessage(), event);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException var18) {
                    System.err.println("Error closing PreparedStatement: " + var18.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "All fields are required.", event);
        }
    }

    // Getter for user
    public static String getUser() {
        return user;
    }
}
