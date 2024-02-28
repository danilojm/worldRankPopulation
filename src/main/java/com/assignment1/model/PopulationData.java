package com.assignment1.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents population data for a country.
 */
public class PopulationData {
    // Properties for country name and population
    private final SimpleStringProperty country;
    private final SimpleLongProperty population;

    /**
     * Constructs a new PopulationData object with the specified country name and population.
     *
     * @param country    The name of the country.
     * @param population The population of the country.
     */
    public PopulationData(String country, long population) {
        this.country = new SimpleStringProperty(country);
        this.population = new SimpleLongProperty(population);
    }

    /**
     * Gets the country name.
     *
     * @return The country name.
     */
    public String getCountry() {
        return country.get();
    }

    /**
     * Gets the population of the country.
     *
     * @return The population of the country.
     */
    public long getPopulation() {
        return population.get();
    }
}
