package org.example;

import java.io.*;
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
    public void loadConfiguration(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
            this.totalTickets = jsonObject.getInt("totalTickets");
            this.ticketReleaseRate = jsonObject.getInt("ticketReleaseRate");
            this.customerRetrievalRate = jsonObject.getInt("customerRetrievalRate");
            this.maxTicketCapacity = jsonObject.getInt("maxTicketCapacity");
        } catch (IOException e) {
            System.out.println("Error loading configuration: " + e.getMessage());
        }
    }

    /**
     * Saves the current configuration parameters to a JSON file.
     *
     * @param filePath the path where the configuration JSON file will be saved.
     */
    public void saveConfiguration(String filePath) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalTickets", totalTickets);
        jsonObject.put("ticketReleaseRate", ticketReleaseRate);
        jsonObject.put("customerRetrievalRate", customerRetrievalRate);
        jsonObject.put("maxTicketCapacity", maxTicketCapacity);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(jsonObject.toString());
        } catch (IOException e) {
            System.out.println("Error saving configuration: " + e.getMessage());
        }
    }

    // Setter methods
    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    // Getter methods
    public int getTotalTickets() { return totalTickets; }
    public int getTicketReleaseRate() { return ticketReleaseRate; }
    public int getCustomerRetrievalRate() { return customerRetrievalRate; }
    public int getMaxTicketCapacity() { return maxTicketCapacity; }
}
