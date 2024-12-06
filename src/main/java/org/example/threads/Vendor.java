package org.example.threads;

import org.example.ticketPool.TicketPool;
import org.example.User;
import org.example.config.Configuration;

/**
 * The Vendor class implements the {@link User} interface and represents a vendor
 * that adds tickets to the {@link TicketPool}. This class is intended to be run
 * as a thread, with each instance continually adding tickets at a rate specified
 * by the configuration, until the pool reaches its maximum capacity or the thread
 * is interrupted.
 */
public class Vendor implements User {

    /**
     * The run method executes the vendor's main logic. It retrieves configuration settings
     * from the {@link Configuration} singleton instance and the ticket pool from {@link TicketPool}.
     * The vendor thread continues adding tickets to the pool until either the maximum capacity
     * is reached or the thread is interrupted. The frequency of adding tickets is controlled by
     * the ticket release rate in the configuration.
     */
    @Override
    public void run() {
        Configuration config = Configuration.getInstance();
        TicketPool pool = TicketPool.getInstance(config.getTotalTickets(), config.getMaxTicketCapacity());


        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " was interrupted, exiting...");
                break;
            }
            boolean ticketReleased = pool.addTickets(Thread.currentThread().getName());
            if (!ticketReleased) {
                // Exit if no tickets are available
                System.out.println(Thread.currentThread().getName() + " exiting as max capacity reached.");
                break;
            }

            try {
                // Sleep based on the customer retrieval rate from the configuration
                Thread.sleep(config.getTicketReleaseRate() * 1000L);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted during sleep.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}