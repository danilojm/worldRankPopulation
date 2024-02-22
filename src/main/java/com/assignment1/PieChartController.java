package com.assignment1;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import sql.DBConnection;

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

    private Node currentView;

    private ObservableList<PieChart.Data> pieChartData;

    @FXML
    private Button toggleView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializeComboBoxes(); // Initialize combo boxes with data

            // Add listeners to ComboBoxes and TextField to update the chart data
            addComboBoxListeners();

            // Initial chart data update
            currentView = buildPieChart(); // Build and display pie chart
            borderPane.setCenter(currentView); // Set pie chart as center node of BorderPane
        } catch (SQLException e) {
            handleSQLException(e); // Handle SQL exception
        }
    }

    /* Initialize combo boxes with appropriate data */
    private void initializeComboBoxes() throws SQLException {
        // Set items for year combo box
        yearComboBox.setItems(getYearList());
        yearComboBox.setValue(2022); // Default value

        // Set items for country combo box
        countryComboBox.setItems(getCountryList());
        countryComboBox.setValue("ALL"); // Default value

        // Set items and default value for order combo box
        orderComboBox.setItems(FXCollections.observableArrayList("Asc", "Desc"));
        orderComboBox.setValue("Desc"); // Default value

        // Set default value for filter number countries text field
        filterNumberCountriesTextField.setText("15");
    }

    /*
     * Add listeners to ComboBoxes and TextField to update chart data when changed
     */
    private void addComboBoxListeners() {
        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        countryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        orderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateChartData());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            // Update chart data when the checkbox value changes
            updateChartData();

            // Enable or disable other controls based on the checkbox value
            if (newValue) {
                // Checkbox is checked, disable other controls
                countryComboBox.setDisable(true);
                orderComboBox.setDisable(true);
                filterNumberCountriesTextField.setDisable(true);
            } else {
                // Checkbox is unchecked, enable other controls
                countryComboBox.setDisable(false);
                orderComboBox.setDisable(false);
                filterNumberCountriesTextField.setDisable(false);
            }
        });
        filterNumberCountriesTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> updateChartData());
    }

    /* Update chart data based on current ComboBox and TextField values */
    private void updateChartData() {
        if (currentView instanceof PieChart) {
            currentView = buildPieChart(); // Rebuild pie chart with updated data
        } else {
            currentView = buildTableView(); // Rebuild table view with updated data
        }
        borderPane.setCenter(currentView); // Set updated view as center node of BorderPane
    }

    /* Toggle between pie chart and table view */
    @FXML
    private void toggleView() {
        Node newView;
        if (currentView instanceof PieChart) {
            toggleView.setText("Chart View");
            newView = buildTableView(); // Build table view
        } else {
            toggleView.setText("Table View");
            newView = buildPieChart(); // Build pie chart
        }
        borderPane.setCenter(newView); // Set new view as center node of BorderPane
        currentView = newView; // Update reference to current view
    }

    /* Retrieve list of years from database */
    private ObservableList<Integer> getYearList() throws SQLException {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("SELECT DISTINCT year FROM populationdata ORDER BY year DESC");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int year = rs.getInt("year");
                years.add(year);
            }
        }
        return years;
    }

    /* Retrieve list of countries from database */
    private ObservableList<String> getCountryList() throws SQLException {
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("ALL");
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT country FROM populationdata");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String country = rs.getString("country");
                countries.add(country);
            }
        }
        return countries;
    }

    /* Build and display table view */
    private Node buildTableView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/TableView.fxml"));
        Node table = null;
        try {
            table = loader.load(); // Load TableView from FXML
            TableViewController controller = loader.getController(); // Get controller for TableView
            controller.initData(yearComboBox.getValue(), countryComboBox.getValue(), orderComboBox.getValue(),
                    checkBox.isSelected(), filterNumberCountriesTextField.getText()); // Initialize data for TableView
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exception
        }
        borderPane.setCenter(table); // Set TableView as center node of BorderPane
        return table; // Return TableView node
    }

    /* Build and display pie chart */
    private PieChart buildPieChart() {
        pieChartData = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM populationdata WHERE year = ? ";
            if (checkBox.isSelected()) {
                sql += "AND countrykey = 'WLD' ";
            } else {
                if (!"ALL".equals(countryComboBox.getValue())) {
                    sql += "AND country = ? ";
                }
                sql += "AND countrykey <> 'WLD' ";
                sql += "ORDER BY population " + orderComboBox.getValue() + " LIMIT "
                        + filterNumberCountriesTextField.getText() + ";";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, yearComboBox.getValue());
                if (!"ALL".equals(countryComboBox.getValue()) && !checkBox.isSelected()) {
                    stmt.setString(2, countryComboBox.getValue());
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,###");
                    while (rs.next()) {
                        String country = rs.getString("country");
                        long population = rs.getLong("population");
                        String label = country + ": " + df.format(population);
                        pieChartData.add(new PieChart.Data(label, population));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("World Population Ranking");
        pieChart.setClockwise(true); // setting the direction to arrange the data
        pieChart.setLabelsVisible(true); // Setting the labels of the pie chart visible
        if (!"ALL".equals(countryComboBox.getValue()) || checkBox.isSelected()){
            pieChart.setLegendVisible(true);
        } else {
            pieChart.setLegendVisible(false);
        }
        pieChart.setStartAngle(180);

        createToolTips(pieChart);

        borderPane.setCenter(pieChart);

        return pieChart;
    }

    /* Create tooltips for pie chart */
    private void createToolTips(PieChart pc) {
        DecimalFormat df = new DecimalFormat("#,###");
        for (PieChart.Data data : pc.getData()) {
            String msg = df.format(data.getPieValue());

            Tooltip tp = new Tooltip(msg);
            tp.setShowDelay(Duration.seconds(0));

            Tooltip.install(data.getNode(), tp);

            // Update tooltip data when changed
            data.pieValueProperty().addListener((observable, oldValue, newValue) -> {
                tp.setText(newValue.toString());
            });
        }
    }

    /* Handle SQL exceptions */
    private void handleSQLException(SQLException e) {
        e.printStackTrace();
    }

    /* Close the application */
    @FXML
    private void closeApplication() {
        Platform.exit();
    }
}
