package com.example.project;

import Database.Connect;
import Exceptions.InvalidQuanityException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Product;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.example.project.AfterLogin.showAlert;

public class ProductCardController implements Initializable {

    private final Connection con = Connect.connect();

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private ImageView productImage;

    @FXML
    private Spinner<Integer> quantityInput;


    // Method to set product details in the card
    public void setData(Product product) {
        // Set image
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(product.getImgUrl())));
        productImage.setImage(img);

        // Set price and name
        priceLabel.setText(String.format("₹%.2f", product.getPrice()));
        nameLabel.setText(product.getTitle() + " (" + product.getQuantity() + ")");
    }

    // Method to add product to the cart
    public void addToCart(ActionEvent e) {
        String productName = nameLabel.getText().split("\\(")[0].trim();
        String user = LoginController.getUser();
        int quantity;// Get the current logged-in user
        int productID = 0;
        if (quantityInput.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a valid quantity.",e);
            return;
        }
        else{
            quantity = quantityInput.getValue();
        }
        // Get the quantity from the Spinner



        try {
            // Validate quantity
            int availableQuantity = Integer.parseInt(nameLabel.getText().split("\\(")[1].split("\\)")[0]);
            if (quantity > availableQuantity) {
                throw new InvalidQuanityException("Quantity must be less than or equal to available stock.");
            }

            if (AfterLogin.cartProducts.containsKey(productName)) {
                productID = AfterLogin.cartProducts.get(productName);
                System.out.println("Product id:" + productID);// Retrieve product ID
            }

            // Insert into the cart database
            String query = "INSERT INTO cart (name, productID, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, user);
                ps.setInt(2, productID);
                ps.setInt(3, quantity);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Cart", "Product added successfully to the cart!",e);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Cart", "Failed to add product to the cart.",e);
                }
            }

        } catch (InvalidQuanityException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Quantity", ex.getMessage(),e);
        } catch (SQLException ex) {
            System.out.println("Database error: " + ex.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);  // min, max, default
        quantityInput.setValueFactory(valueFactory);
    }



}
