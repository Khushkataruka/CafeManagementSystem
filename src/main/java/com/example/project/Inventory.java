package com.example.project;

import Database.Connect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Inventory implements Initializable {
    private static final Connection con = Connect.connect();
    @FXML
    private GridPane productsGrid;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Product> products = new ArrayList<>(products());
        int columns = 0;
        int rows = 0;
        try {
            for (Product product : products) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("ProductCard.fxml"));
                AnchorPane anchorPane = loader.load();  // Load as AnchorPane
                ProductCardController productCardController = loader.getController();
                productCardController.setData(product);

                if (columns == 3) {
                    columns = 0;
                    rows++;
                }

                productsGrid.add(anchorPane, columns++, rows); // Add AnchorPane to GridPane
                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Product> products() {
        List<Product> ls = new ArrayList<>();
        String query = "SELECT cart.quantity, Products.name, Products.price, Products.imageURL \n" +
                "FROM Products \n" +
                "INNER JOIN Cart ON Cart.productID = Products.productID \n" +
                "WHERE Cart.name = ?;\n";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1,LoginController.getUser());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setTitle(rs.getString("products.name"));
                product.setImgUrl(rs.getString("imageURL"));
                product.setQuantity(rs.getInt("quantity"));
                product.setPrice(rs.getDouble("price"));
                ls.add(product);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return ls;
    }

    public void onBackButtonClick(ActionEvent e)
    {
        changeSceneToInventory(e);
    }



    public void changeSceneToInventory(ActionEvent event) {
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
            System.out.println("Error loading Inventory scene: " + e.getMessage());
        }
    }
}
