package com.assignment1.controller;

import com.assignment1.model.PopulationData;
import com.assignment1.util.PopulationDataUtil;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the TableView.fxml file.
 */
public class TableViewController implements Initializable {

    @FXML
    private TableView<PopulationData> tableView;

    @FXML
    private TableColumn<PopulationData, String> countryColumn;

    @FXML
    private TableColumn<PopulationData, String> populationColumn;

    /** List to store population data for the table */
    private ObservableList<PopulationData> populationDataList = FXCollections.observableArrayList();

    /** Getter for the table view */
    public TableView<PopulationData> getTableView() {
        return tableView;
    }

    /** Called when the FXML file is loaded */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTableColumns(); // Initialize table columns
        populateTable(); // Populate table with data
    }

    /** Initialize table columns */
    private void initializeTableColumns() {
        // Set cell value factories for columns
        countryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCountry()));
        populationColumn.setCellValueFactory(data -> {
            String population = String.valueOf(data.getValue().getPopulation());
            String maskedPopulation = applyMask(population); // Apply mask to population data
            return new SimpleStringProperty(maskedPopulation);
        });
    }

    /** Method to apply mask or formatting logic to the population */
    private String applyMask(String population) {
        // Implement your mask or formatting logic here
        // For example, you could add commas to separate thousands: "1,000,000"
        return population.replaceAll("(?<=\\d)(?=(?:\\d{3})+(?!\\d))", ",");
    }

    /**
     * Method to initialize table data.
     * @param year The year to retrieve data for.
     * @param country The country to retrieve data for.
     * @param order The order in which data should be retrieved.
     * @param checkBoxSelected The selection status of the checkbox.
     * @param filterNumberCountries The number of countries to filter by.
     */
    public void initData(int year, String country, String order, boolean checkBoxSelected,
                         String filterNumberCountries) {
        retrieveData(year, country, order, checkBoxSelected, filterNumberCountries); // Retrieve and display data
    }

    /** Populate table with data */
    private void populateTable() {
        tableView.setItems(populationDataList);
    }

    /** Retrieve data from database */
    private void retrieveData(int year, String country, String order, boolean checkBoxSelected,
                              String filterNumberCountries) {
        populationDataList.clear(); // Clear previous data
        try {
            // Retrieve population data from database
            ObservableList<PopulationData> data = PopulationDataUtil.retrievePopulationData(year, country, order,
                    checkBoxSelected, filterNumberCountries, TableViewController.class);
            populationDataList.addAll(data); // Add retrieved data to the list

        } catch (SQLException e) {
            handleSQLException(e); // Handle SQL exception
        }

        // Refresh the table view
        tableView.refresh();
    }

    /** Handle SQL exception */
    private void handleSQLException(SQLException e) {
        // Display an error message to the user
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("An error occurred while accessing the database.");
        alert.setContentText("Please check your database connection and try again.");
        alert.showAndWait();

        // Log the error
        Logger.getLogger(TableViewController.class.getName()).log(Level.SEVERE, "SQL Exception", e);
    }
}
