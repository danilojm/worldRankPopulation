package com.assignment1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class responsible for launching the JavaFX application.
 */
public class App extends Application {

    /**
     * The entry point of the JavaFX application.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs during loading of the FXML file.
     */
    @Override
    public void start(Stage stage) {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("view/PieChartView.fxml"));

            // Set style for the root node
            root.setStyle("-fx-padding: 20px;");

            // Create a scene
            Image icon = new Image(getClass().getResourceAsStream("images/world_icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("World Population Ranking - by Danilo Mendes de Oliveira");

            // Apply Style CSS to the project
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("css/styles.css").toExternalForm());

            // Set the stage to full screen
            stage.setFullScreen(true);

            // Set the scene to the stage and show the stage
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    /**
     * Handles IOExceptions occurred during application initialization.
     *
     * @param e The IOException.
     */
    private void handleIOException(IOException e) {
        // Display an error message to the user
        Logger.getLogger(App.class.getName()).log(Level.SEVERE, "Error loading FXML", e);
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
