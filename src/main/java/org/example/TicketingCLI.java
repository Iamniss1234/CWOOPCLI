package org.example;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The TicketingCLI class provides a command-line interface for managing ticket sales and releases.
 * The configuration for ticketing can be loaded from a file or set manually.
 * Users can act as customers, vendors, or both to simulate a ticketing system.
 */

public class TicketingCLI {
    private static final ArrayList<Thread> threads = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Configuration config = Configuration.getInstance();
    private static TicketPool pool;

    /**
     * The main method initializes the ticketing system and starts the user interface loop.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        loadConfigurationPrompt();

        // Initialize TicketPool after loading configuration
        pool = TicketPool.getInstance(config.getTotalTickets(), config.getMaxTicketCapacity());

        while (true) {
            int choice = getValidatedInput("Enter\nCustomer -> 1\nVendor -> 2\nBoth -> 3\nExit -> 4");
            if (choice == 1 || choice == 2) {
                handleUserType(choice == 1 ? "customer" : "vendor");
            } else if (choice == 3) {
                handleBothUsers();
            } else if (choice == 4) {
                displayTicketSummary();
                break;
            } else {
                System.out.println("Invalid input");
            }
        }

        // Closing the scanner
        scanner.close();
    }


    /**
     * Prompts the user to load a configuration from a file or set a new configuration.
     */
    private static void loadConfigurationPrompt() {
        boolean isConfigLoaded = false;
        while (!isConfigLoaded) {
            try {
                config.loadConfiguration("config.json");
                System.out.println("Previous configuration loaded.\n" +
                        "Total tickets: " + config.getTotalTickets() + "\n" +
                        "Customer Retrieval Rate: " + config.getCustomerRetrievalRate() + "\n" +
                        "Ticket Release Rate: " + config.getTicketReleaseRate() + "\n" +
                        "Max Ticket Capacity: " + config.getMaxTicketCapacity() + "\n");

                int loadConfigChoice = getValidatedInput("Would you like to load the previous configuration? (1 = Yes, 0 = No): ");
                if (loadConfigChoice == 1) {
                    isConfigLoaded = true;
                } else if (loadConfigChoice == 0) {
                    setNewConfiguration();
                    isConfigLoaded = true;
                } else {
                    System.out.println("Invalid input. Please enter 1 or 0.");
                }
            } catch (Exception e) {
                System.out.println("Error loading configuration: " + e.getMessage());
                System.out.println("Proceeding with new configuration setup.");
                setNewConfiguration();
                isConfigLoaded = true;
            }
        }
    }


    /**
     * Prompts the user to set the new configurations.
     */
    private static void setNewConfiguration() {
        config.setTotalTickets(getValidatedInput("Enter total tickets: "));
        config.setTicketReleaseRate(getValidatedInput("Enter ticket release rate: "));
        config.setCustomerRetrievalRate(getValidatedInput("Enter customer retrieval rate: "));
        config.setMaxTicketCapacity(getValidatedInput("Enter max ticket capacity: "));
        config.saveConfiguration("config.json");
        System.out.println("New configuration saved.");
    }

    /**
     * Validation check.
     */
    private static int getValidatedInput(String prompt) {
        int value;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();  // Consume newline left-over
                if (value >= 0) {
                    return value;
                } else {
                    System.out.println("Please enter a positive number.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();  // Consume invalid input
            }
        }
    }

    /**
     * Handle both customer retrieving tickets and vendor releasing tickets simultaneously.
     */
    private static void handleBothUsers() {
        while (true) {
            int input = getValidatedInput("Start - 1\nStop - 0");
            if (input == 1) {
                System.out.println("Vendor starting releasing tickets...");
                startUserThreads("vendor");

                startUserThreads("customer");
                System.out.println("Customer starting purchasing tickets...");
            } else if (input == 0) {
                stopAllThreads();
                break;
            } else {
                System.out.println("Invalid input. Please enter 1 or 0.");
            }
        }
    }

    /**
     * Handle single userType
     *
     * @param userType - customer or vendor
     */
    private static void handleUserType(String userType) {
        while (true) {
            int action = getValidatedInput(userType + " - Start(1) / Stop(0): ");
            if (action == 1) {
                startUserThreads(userType);
            } else if (action == 0) {
                stopAllThreads();
                break;
            } else {
                System.out.println("Invalid input. Please enter 1 or 0.");
            }
        }
    }


    /**
     * Starting the threads.
     *
     * @param userType - customer or vendor
     */
    private static void startUserThreads(String userType) {
        for (int i = 0; i < 5; i++) {
            User user = UserFactory.createUser(userType);
            Thread userThread = new Thread(user, userType + ":" + (i + 1));
            threads.add(userThread);
            System.out.println("Started " + userType + " thread: " + userThread.getName());
            userThread.start();
        }
    }

    /**
     * Stops all  running threads in the ticketing system.
     * If there  are active threads, each thread is interrupted and removed from the list.
     * Otherwise, it displays a message indicating that no threads are running.
     */
    private static void stopThreads() {
        if (!threads.isEmpty()) {
            for (Thread thread : threads) {
                thread.interrupt();
            }
            threads.clear();
            System.out.println("All threads stopped.");
        } else {
            System.out.println("No threads to stop.");
        }
    }

    /**
     * Stops all threads and saves the current ticket count to the configuration.
     * Calls the stopThreads method and then updates the total available tickets
     * in the configuration based on the ticket pool's current status.
     */
    private static void stopAllThreads() {
        stopThreads();
        if (pool != null) {
            config.setTotalTickets(pool.getAvailableTickets());
            config.saveConfiguration("config.json");
        }
    }

    /**
     * Displays a summary of the ticketing system, including configuration details and ticket statistics.
     * Ensures that all threads are stopped before displaying the summary.
     * Outputs details such as the total tickets, retrieval rate, release rate, and ticket availability.
     */
    private static void displayTicketSummary() {
        stopThreads();  // Ensure threads are stopped before displaying summary

        if (pool != null) {
            config.saveConfiguration("config.json");

            System.out.println("=========== Ticketing System Summary ===========");

            // Display configuration parameters
            System.out.printf("%-30s %-10s\n", "Configuration Parameter", "Value");
            System.out.println("------------------------------------------------");
            System.out.printf("%-30s %-10d\n", "Total Tickets", config.getTotalTickets());
            System.out.printf("%-30s %-10d\n", "Customer Retrieval Rate", config.getCustomerRetrievalRate());
            System.out.printf("%-30s %-10d\n", "Ticket Release Rate", config.getTicketReleaseRate());
            System.out.printf("%-30s %-10d\n", "Max Ticket Capacity", config.getMaxTicketCapacity());

            System.out.println("\n=========== Ticket Pool Summary ===========");

            // Display ticket pool summary
            System.out.printf("%-30s %-10s\n", "Ticket Information", "Count");
            System.out.println("------------------------------------------------");
            System.out.printf("%-30s %-10d\n", "Total Tickets Available", pool.getAvailableTickets());
            System.out.printf("%-30s %-10d\n", "Total Tickets Released", pool.getTotalTicketsReleased());
            System.out.printf("%-30s %-10d\n", "Total Tickets Purchased", pool.getTotalTicketsPurchased());
        }
    }
}
