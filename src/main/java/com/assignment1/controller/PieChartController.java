package com.assignment1.controller;

import com.assignment1.model.PopulationData;
import com.assignment1.util.PopulationDataUtil;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the PieChartView.fxml file.
 */
public class PieChartController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private ComboBox<Integer> yearComboBox;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private ComboBox<String> orderComboBox;

    @FXML
    private CheckBox checkBox;

    @FXML
    private TextField filterNumberCountriesTextField;

    @FXML
    private Button toggleView;

    private ObservableList<PieChart.Data> pieChartData;

    private boolean isPieChartView = true; // Track current view state

    /** Called when the FXML file is loaded */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializeComboBoxes(); // Initialize combo boxes
            addComboBoxListeners(); // Add listeners to combo boxes
            updateChartData(); // Update chart data
        } catch (SQLException e) {
            handleSQLException(e); // Handle SQL exception
        }
    }

    /** Toggle between pie chart and table view */
    @FXML
    private void toggleView() {
        if (isPieChartView) {
            isPieChartView = false;
            toggleView.setText("Pie Chart View");
            buildTableView(); // Build table view
        } else {
            isPieChartView = true;
            toggleView.setText("Table View");
            buildPieChart(); // Build pie chart
        }
    }

    /** Initialize combo boxes */
    private void initializeComboBoxes() throws SQLException {
        yearComboBox.setItems(PopulationDataUtil.getYearList());
        yearComboBox.setValue(2022);

        countryComboBox.setItems(PopulationDataUtil.getCountryList());
        countryComboBox.setValue("ALL");

        orderComboBox.setItems(FXCollections.observableArrayList("Asc", "Desc"));
        orderComboBox.setValue("Desc");

        filterNumberCountriesTextField.setText("15");
    }

    /** Add listeners to combo boxes */
    private void addComboBoxListeners() {
        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        orderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateChartData();
            setControlsState(!newValue);
        });
        filterNumberCountriesTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateChartData());
    }

    /** Update chart data based on combo box selections */
    private void updateChartData() {
        if (borderPane != null) {
            if (isPieChartView) {
                buildPieChart(); // Build pie chart
            } else {
                buildTableView(); // Build table view
            }
        }
    }

    /** Build and display pie chart */
    private void buildPieChart() {
        pieChartData = FXCollections.observableArrayList();
        try {
            ObservableList<PopulationData> data = PopulationDataUtil.retrievePopulationData(yearComboBox.getValue(),
                    countryComboBox.getValue(), orderComboBox.getValue(), checkBox.isSelected(),
                    filterNumberCountriesTextField.getText(), PieChartController.class);
            for (PopulationData populationData : data) {
                pieChartData.add(new PieChart.Data(populationData.getCountry(), populationData.getPopulation()));
            }
        } catch (SQLException e) {
            handleSQLException(e); // Handle SQL exception
        }

        PieChart pieChart = new PieChart(pieChartData);

        pieChart.setTitle("World Population Ranking");
        pieChart.setClockwise(true); // setting the direction to arrange the data
        pieChart.setLabelsVisible(true); // Setting the labels of the pie chart visible
        if (!"ALL".equals(countryComboBox.getValue()) || checkBox.isSelected()) {
            pieChart.setLegendVisible(true);
        } else {
            pieChart.setLegendVisible(false);
        }
        pieChart.setStartAngle(180);

        // Customize pie chart as needed
        borderPane.setCenter(pieChart);
    }

    /** Build and display table view */
    private void buildTableView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/assignment1/view/TableView.fxml"));
        Node tableViewNode;
        try {
            tableViewNode = loader.load();
            TableViewController controller = loader.getController();
            TableView<PopulationData> tableView = controller.getTableView();
            // Initialize table data here
            tableView.setItems(getPopulationDataForTableView()); // Call a method to retrieve data for the table
            borderPane.setCenter(tableViewNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Retrieve population data for table view */
    private ObservableList<PopulationData> getPopulationDataForTableView() {
        ObservableList<PopulationData> populationDataList = FXCollections.observableArrayList();
        try {
            populationDataList = PopulationDataUtil.retrievePopulationData(yearComboBox.getValue(),
                    countryComboBox.getValue(), orderComboBox.getValue(), checkBox.isSelected(),
                    filterNumberCountriesTextField.getText(), TableViewController.class);
        } catch (SQLException e) {
            handleSQLException(e); // Handle SQL exception
        }
        return populationDataList;
    }

    /** Set state of controls */
    private void setControlsState(boolean enabled) {
        countryComboBox.setDisable(!enabled);
        orderComboBox.setDisable(!enabled);
        filterNumberCountriesTextField.setDisable(!enabled);
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
        Logger.getLogger(PieChartController.class.getName()).log(Level.SEVERE, "SQL Exception", e);
    }

    /** Close the application */
    @FXML
    private void closeApplication() {
        Platform.exit();
    }
}
