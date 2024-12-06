package org.example;

import org.example.cli.TicketingCLI;
import org.example.exception.InvalidConfigurationException;

public class Main {
    public static void main(String[] args) {
        TicketingCLI ticketingCLI = new TicketingCLI();
        try {
            ticketingCLI.start();
        } catch (InvalidConfigurationException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}