/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProductDetails;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;

/**
 *
 * @author c0652863
 */
@ApplicationScoped
public class ListOfProducts {

    private final List<Product> listOfProducts;

    public ListOfProducts() {
        listOfProducts = new ArrayList<>();
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM product";
            PreparedStatement preparedStatment = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatment.executeQuery();
            while (resultSet.next()) {
                Product product = new Product(resultSet.getInt("productID"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("quantity"));
                listOfProducts.add(product);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ListOfProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonArray toJSON() {
        JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
        listOfProducts.stream().forEach((product) -> {
            jsonBuilder.add(product.toJSON());
        });
        return jsonBuilder.build();
    }

    public Product get(int productID) {
        Product result = null;
        for (Product product : listOfProducts) {
            if (product.getProductID() == productID) {
                result = product;
            }
        }
        return result;
    }

    public void set(int productID, Product product) {
        int result = doUpdate(
                "update product SET name = ?, description = ?, quantity = ? where productID = ?",
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getQuantity()),
                String.valueOf(productID));
        if (result > 0) {
            Product original = get(productID);
            original.setName(product.getName());
            original.setDescription(product.getDescription());
            original.setQuantity(product.getQuantity());
        }

    }

    public void add(Product product) throws Exception {
        int result = doUpdate(
                "INSERT into product (productID, name, description, quantity) values (?, ?, ?, ?)",
                String.valueOf(product.getProductID()),
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getQuantity()));
        if (result > 0) {
            listOfProducts.add(product);
        } else {
            throw new Exception("Error Inserting");
        }
    }

    public void remove(Product product) throws Exception {
        remove(product.getProductID());
    }

    public void remove(int productID) throws Exception {
        int result = doUpdate("DELETE from product where productID = ?",
                String.valueOf(productID));
        if (result > 0) {
            Product original = get(productID);
            listOfProducts.remove(original);
        } else {
            throw new Exception("Delete failed");
        }

    }

    private Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            String jdbc = "jdbc:mysql://ipro.lambton.on.ca/inventory";
            connection = DriverManager.getConnection(jdbc, "products", "products");
        } catch (SQLException ex) {
            System.err.println("Failed to Connect: " + ex.getMessage());
        }
        return connection;
    }

    private int doUpdate(String query, String... params) {
        int result = 0;
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatment = connection.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                preparedStatment.setString(i, params[i - 1]);
            }
            result = preparedStatment.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ListOfProducts.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}
