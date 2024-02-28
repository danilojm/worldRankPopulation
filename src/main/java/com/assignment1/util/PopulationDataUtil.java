package com.assignment1.util;

import com.assignment1.controller.PieChartController;
import com.assignment1.model.PopulationData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Utility class for retrieving population data from the database.
 */
public class PopulationDataUtil {

    /**
     * Retrieves population data from the database based on the specified
     * parameters.
     *
     * @param year                  The year for which data is retrieved.
     * @param country               The country for which data is retrieved.
     * @param order                 The order in which data is sorted.
     * @param checkBoxSelected      Indicates whether a checkbox is selected.
     * @param filterNumberCountries The number of countries to filter.
     * @param instance              An instance indicating the type of controller calling this method.
     * @return An ObservableList containing population data.
     * @throws SQLException If an SQL exception occurs.
     */
    public static ObservableList<PopulationData> retrievePopulationData(int year, String country, String order,
            boolean checkBoxSelected,
            String filterNumberCountries,
            Object instance) throws SQLException {
        ObservableList<PopulationData> populationDataList = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM PopulationData WHERE year = ? ";
            if (checkBoxSelected) {
                sql += "AND countrykey = 'WLD' ";
            } else {
                if (!"ALL".equals(country)) {
                    sql += "AND country = ? ";
                }
                sql += "AND countrykey <> 'WLD' ";
                sql += "ORDER BY population " + order + " LIMIT " +
                        (!filterNumberCountries.equals("") ? filterNumberCountries : 0) + ";";
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, year);
                if (!"ALL".equals(country) && !checkBoxSelected) {
                    stmt.setString(2, country);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    DecimalFormat df = new DecimalFormat("#,###");

                    if (instance.equals(PieChartController.class)) {
                        while (rs.next()) {
                            String countryName = rs.getString("country");
                            long population = rs.getLong("population");
                            String label = countryName + ": " + df.format(population);
                            populationDataList.add(new PopulationData(label, population));
                        }
                    } else {
                        while (rs.next()) {
                            String countryName = rs.getString("country");
                            long population = rs.getLong("population");
                            populationDataList.add(new PopulationData(countryName, population));
                        }
                    }
                }
            }
        }

        return populationDataList;
    }

    /** Retrieve list of years from database */
    public static ObservableList<Integer> getYearList() throws SQLException {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn
                     .prepareStatement("SELECT DISTINCT year FROM PopulationData ORDER BY year DESC");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int year = rs.getInt("year");
                years.add(year);
            }
        }
        return years;
    }

    /** Retrieve list of countries from database */
    public static ObservableList<String> getCountryList() throws SQLException {
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("ALL");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT country FROM PopulationData");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String country = rs.getString("country");
                countries.add(country);
            }
        }
        return countries;
    }
}
