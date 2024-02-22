package com.assignment1;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sql.DBConnection;

public class TableViewController implements Initializable {

    @FXML
    private TableView<PopulationData> tableView;

    @FXML
    private TableColumn<PopulationData, String> countryColumn;

    @FXML
    private TableColumn<PopulationData, Long> populationColumn;

    // Data
    private ObservableList<PopulationData> populationDataList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize table columns
        
        // tableView.setPrefWidth(javafx.stage.Screen.getPrimary().getVisualBounds().getWidth() - 100);
        // tableView.setPrefHeight(javafx.stage.Screen.getPrimary().getVisualBounds().getHeight() - 100);

        // tableView.setLayoutX(25);
        // tableView.setLayoutY(25);

        countryColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCountry()));
        populationColumn.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getPopulation()).asObject());

        // Populate table with data
        populateTable();
    }

    public void initData(int year, String country, String order, boolean checkBoxSelected, String filterNumberCountries) {
        // Retrieve data based on filters
        retrieveData(year, country, order, checkBoxSelected, filterNumberCountries);
    }

    private void populateTable() {
        tableView.setItems(populationDataList);
    }

    private void retrieveData(int year, String country, String order, boolean checkBoxSelected, String filterNumberCountries) {
        // Clear previous data
        populationDataList.clear();

        // Query database and populate the table
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM populationdata WHERE year = ? ";
            if (checkBoxSelected) {
                sql += "AND countrykey = 'WLD' ";
            } else {
                if (!"ALL".equals(country)) {
                    sql += "AND country = ? ";
                }
                sql += "AND countrykey <> 'WLD' ";
                sql += "ORDER BY population " + order + " LIMIT " + filterNumberCountries + ";";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, year);
                if (!"ALL".equals(country)) {
                    stmt.setString(2, country);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String countryName = rs.getString("country");
                        long population = rs.getLong("population");
                        populationDataList.add(new PopulationData(countryName, population));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        javafx.application.Platform.runLater(() -> populateTable());
    }
}

class PopulationData {
    private final SimpleStringProperty country;
    private final SimpleLongProperty population;

    public PopulationData(String country, long population) {
        this.country = new SimpleStringProperty(country);
        this.population = new SimpleLongProperty(population);
    }

    public String getCountry() {
        return country.get();
    }

    public long getPopulation() {
        return population.get();
    }
}
