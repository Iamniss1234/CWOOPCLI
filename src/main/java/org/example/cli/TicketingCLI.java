package org.example.cli;

import org.example.exception.InvalidConfigurationException;
import org.example.ticketPool.TicketPool;
import org.example.User;
import org.example.UserFactory;
import org.example.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.InputMismatchException;
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

    private static final Logger logger = LoggerFactory.getLogger(TicketingCLI.class);


    public void start() throws InvalidConfigurationException, InterruptedException {
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
                logger.error("Invalid input");
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
                displayConfiguration();

                int loadConfigChoice = getValidatedInput("Would you like to use the previous configuration? (1 = Yes, 0 = No): ");
                if (loadConfigChoice == 1) {
                    isConfigLoaded = true;
                } else if (loadConfigChoice == 0) {
                    setNewConfiguration();
                    isConfigLoaded = true;
                } else {
                    logger.error("Invalid input. Please enter 1 or 0.");
                }
            } catch (InvalidConfigurationException e) {
                logger.error("Configuration error: " + e.getMessage());
                logger.info("Invalid configuration detected. Please re-enter the configuration.");
                setNewConfiguration(); // Reset to default values or prompt for new values
            } catch (Exception e) {
                logger.error("Unexpected error while loading configuration: " + e.getMessage());
                logger.info("Proceeding with manual configuration setup.");
                setNewConfiguration(); // Reset or prompt user
            }
        }

    }


    /**
     * Prompts the user to set the new configurations.
     */
    private static void setNewConfiguration(){
        while(true){
            try{
                logger.info("Enter total tickets: ");
                int TotalTickets = scanner.nextInt();

                logger.info("Enter maxTicketCapacity: ");
                int maxTicketCapacity = scanner.nextInt();

                logger.info("Enter ticketReleaseRate: ");
                int ticketReleaseRate = scanner.nextInt();

                logger.info("Enter customerRetrivalRate: ");
                int customerRetrivalRate = scanner.nextInt();

                config.setTotalTickets(TotalTickets);
                config.setMaxTicketCapacity(maxTicketCapacity);
                config.setTicketReleaseRate(ticketReleaseRate);
                config.setCustomerRetrievalRate(customerRetrivalRate);

                config.validateConfiguration();

                config.saveConfiguration("config.json");
                logger.info("New configuration saved.");

                break;

            } catch (InvalidConfigurationException e) {
                logger.error("Invalid configuration: " + e.getMessage());
                logger.info("Please re-enter the configuration values.");
            } catch (InputMismatchException e) {
                logger.error("Invalid input type. Please enter numeric values.");
                scanner.next();
            }
        }

    }

    /**
     * Validation check.
     */

    private static int getValidatedInput(String prompt) {
        while (true) {
            try {
                logger.info(prompt);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                logger.error("Invalid input type. Please enter a numeric value.");
                scanner.next(); // Clear the invalid input
            }
        }
    }

    /**
     * Handle both customer retrieving tickets and vendor releasing tickets simultaneously.
     */
    private static void handleBothUsers() throws InvalidConfigurationException{
        while (true) {
            int input = getValidatedInput("Start - 1\nStop - 0");
            if (input == 1) {
                logger.info("Vendor starting releasing tickets...");
                startUserThreads("vendor");

                startUserThreads("customer");
                logger.info("Customer starting purchasing tickets...");
            } else if (input == 0) {
                stopAllThreads();
                break;
            } else {
                logger.error("Invalid input. Please enter 1 or 0.");
            }
        }
    }

    /**
     * Handle single userType
     *
     * @param userType - customer or vendor
     */
    private static void handleUserType(String userType) throws InvalidConfigurationException{
        while (true) {
            int action = getValidatedInput(userType + " - Start(1) / Stop(0): ");
            if (action == 1) {
                startUserThreads(userType);
            } else if (action == 0) {
                stopAllThreads();
                break;
            } else {
                logger.error("Invalid input. Please enter 1 or 0.");
            }
        }
    }


    /**
     * Starting the threads.
     *
     * @param userType - customer or vendor
     */
    private static void startUserThreads(String userType) {
        for (int i = 0; i < 2; i++) {
            User user = UserFactory.createUser(userType);
            Thread userThread = new Thread(user, userType + ":" + (i + 1));
            threads.add(userThread);
            logger.info("Started " + userType + " thread: " + userThread.getName());
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
            logger.info("All threads stopped.");
        } else {
            logger.info("No threads to stop.");
        }
    }

    /**
     * Stops all threads and saves the current ticket count to the configuration.
     * Calls the stopThreads method and then updates the total available tickets
     * in the configuration based on the ticket pool's current status.
     */
    private static void stopAllThreads() throws InvalidConfigurationException {
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
    private static void displayTicketSummary() throws InvalidConfigurationException {
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

    private static void displayConfiguration() {
        logger.info("Current Configuration:");
        logger.info("Total Tickets: " + config.getTotalTickets());
        logger.info("Max Ticket Capacity: " + config.getMaxTicketCapacity());
        logger.info("Ticket Release Rate: " + config.getTicketReleaseRate());
        logger.info("Customer Retrieval Rate: " + config.getCustomerRetrievalRate());
    }
}
