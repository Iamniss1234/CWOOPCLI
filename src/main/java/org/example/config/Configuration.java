package org.example.config;

import java.io.*;

import org.example.exception.InvalidConfigurationException;
import org.json.JSONObject;

/**
 * Singleton class responsible for loading and saving the ticketing system's configuration.
 * The configuration includes parameters such as total tickets, ticket release rate,
 * customer retrieval rate, and maximum ticket capacity.
 */
public class Configuration {

    private static Configuration instance;

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    /**
     * Private constructor to prevent external instantiation of Configuration.
     * Use {@link #getInstance()} to access the single instance of this class.
     */
    private Configuration() {
        // Default values can be set here if needed
    }

    /**
     * Provides the single instance of Configuration, creating it if necessary.
     *
     * @return the single Configuration instance.
     */
    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * Loads configuration parameters from a JSON file.
     * Reads and sets values for total tickets, ticket release rate,
     * customer retrieval rate, and maximum ticket capacity.
     *
     * @param filePath the path to the configuration JSON file.
     */
    public void loadConfiguration(String filePath) throws InvalidConfigurationException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());

            int loadedTotalTickets = jsonObject.getInt("totalTickets");
            int loadedTicketReleaseRate = jsonObject.getInt("ticketReleaseRate");
            int loadedCustomerRate = jsonObject.getInt("customerRetrievalRate");
            int loadedMaxCapacity = jsonObject.getInt("maxTicketCapacity");

            setTotalTickets(loadedTotalTickets);
            setMaxTicketCapacity(loadedMaxCapacity);
            setCustomerRetrievalRate(loadedCustomerRate);
            setTicketReleaseRate(loadedTicketReleaseRate);

        } catch (IOException e) {
            throw new InvalidConfigurationException("error loading the configuration "+ e.getMessage());
        }
    }

    /**
     * Saves the current configuration parameters to a JSON file.
     *
     * @param filePath the path where the configuration JSON file will be saved.
     */
    public void saveConfiguration(String filePath) throws InvalidConfigurationException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalTickets", totalTickets);
        jsonObject.put("ticketReleaseRate", ticketReleaseRate);
        jsonObject.put("customerRetrievalRate", customerRetrievalRate);
        jsonObject.put("maxTicketCapacity", maxTicketCapacity);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            throw new InvalidConfigurationException("Error saving configuration: " + e.getMessage());
        }
    }

    // Setter methods
    public void setTotalTickets(int totalTickets) throws InvalidConfigurationException {
        validatePositive("Total Tickets", totalTickets);
        this.totalTickets = totalTickets;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) throws InvalidConfigurationException {
        validatePositive("Ticket Release Rate", ticketReleaseRate);
        validateZero("Tickets Release Rate",ticketReleaseRate);
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) throws InvalidConfigurationException {
        validatePositive("Ticket Retrival Rate", customerRetrievalRate);
        validateZero("Customer Retrival Rate",customerRetrievalRate);
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) throws InvalidConfigurationException {
        validatePositive("Max Ticket Capacity", maxTicketCapacity);
        this.maxTicketCapacity = maxTicketCapacity;

    }
    public void validateConfiguration() throws InvalidConfigurationException {
        validateTicketCapacity(totalTickets, maxTicketCapacity);
    }
    private void validateTicketCapacity(int totalTickets, int maxCapacity) throws InvalidConfigurationException {
        if (totalTickets > maxCapacity) {
            throw new InvalidConfigurationException("Total tickets cannot exceed max ticket capacity. "+totalTickets+" "+maxCapacity);
        }
    }

    private void validatePositive(String field, int value) throws InvalidConfigurationException {
        if (value < 0) {
            throw new InvalidConfigurationException(field + " must be a positive value.");
        }
    }

    private void validateZero(String field, int value) throws InvalidConfigurationException {
        if (value == 0) {
            throw new InvalidConfigurationException(field + " must be a greater than zero.");
        }
    }

    // Getter methods
    public int getTotalTickets() { return totalTickets; }
    public int getTicketReleaseRate() { return ticketReleaseRate; }
    public int getCustomerRetrievalRate() { return customerRetrievalRate; }
    public int getMaxTicketCapacity() { return maxTicketCapacity; }
}
