package com.example.project;

import Database.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class AfterLogin implements Initializable {

    private final Connection con = Connect.connect();

    @FXML
    private GridPane productsGrid;

    @FXML
    private Label welcomeLabel;

    public static HashMap<String, Integer> cartProducts;

    static {
        cartProducts = new HashMap<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Product> products = fetchProductsFromDatabase();
        welcomeLabel.setText(LoginController.getUser());
        int columns = 0;
        int rows = 0;

        try {
            for (Product product : products) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ProductCard.fxml"));
                AnchorPane anchorPane = loader.load();  // Load as AnchorPane
                ProductCardController productCardController = loader.getController();
                productCardController.setData(product);

                // Adding product to cartProducts map
                cartProducts.put(product.getTitle(), product.getProductId());
                System.out.println("title" + ":" + product.getTitle());

                if (columns == 4) {
                    columns = 0;
                    rows++;
                }
                productsGrid.add(anchorPane, columns++, rows); // Add AnchorPane to GridPane
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        for (String key : cartProducts.keySet()) {
            // Access each key (product name)
            Integer productId = cartProducts.get(key); // Get the corresponding value (product ID)
            System.out.println("Product: " + key + ", ID: " + productId);
        }

    }

    // Method to fetch products from the database
    private List<Product> fetchProductsFromDatabase() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT name, price, imageURL, stock, productID FROM products";

        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product product = new Product();
                product.setTitle(rs.getString("name"));
                product.setImgUrl(rs.getString("imageURL"));
                product.setQuantity(rs.getInt("stock"));
                product.setPrice(rs.getDouble("price"));
                product.setProductId(rs.getInt("productID"));
                productList.add(product);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return productList;
    }

    // Method to handle sign-out action
    public void onClickSignOutButton(ActionEvent event) {
        changeSceneToLogin(event);
    }

    private void changeSceneToLogin(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene newScene = new Scene(fxmlLoader.load());
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setX(50);
            currentStage.setY(50);
            currentStage.setHeight(440);
            currentStage.setWidth(650);
            currentStage.setOnCloseRequest(e -> currentStage.close());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Sign Out Error", "Failed to sign out", event);
        }
    }

    // Helper method to change scene
    // Utility method to show alert dialogs
    public static void showAlert(Alert.AlertType alertType, String title, String message, ActionEvent event) {
        // Create the alert
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Get the current Stage from the event source (button or any other control)
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setResizable(false);  // Disable resizing during alert
        alert.showAndWait();

        // Restore resizable property after alert is dismissed
        stage.setResizable(true);
    }

    public void changeSceneToInventory(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Inventory.fxml"));
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
            System.out.println("Error loading Inventory scene: " + e.getMessage());
        }
    }
}
