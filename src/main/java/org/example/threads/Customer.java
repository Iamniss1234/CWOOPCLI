package org.example.threads;

import org.example.logger.Loging;
import org.example.ticketPool.TicketPool;
import org.example.User;
import org.example.config.Configuration;

/**
 * Represents a Customer in the ticketing system.
 * A Customer attempts to purchase tickets from the {@link TicketPool} until tickets are no longer available
 * or the thread is interrupted.
 */
public class Customer implements User {

    /**
     * Runs the Customer's ticket-purchasing operation.
     * Continuously attempts to remove a ticket from the {@link TicketPool} based on the customer retrieval rate
     * set in the {@link Configuration} until no tickets are available or the thread is interrupted.
     */
    @Override
    public void run() {
        Configuration config = Configuration.getInstance();
        TicketPool pool = TicketPool.getInstance(config.getTotalTickets(), config.getMaxTicketCapacity());

        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " was interrupted, exiting...");
                Loging.log(Thread.currentThread().getName() + " was interrupted, exiting...");
                break;
            }

            boolean ticketPurchased = pool.removeTicket(Thread.currentThread().getName());
            if (!ticketPurchased) {
                // Exit if no tickets are available
                System.out.println(Thread.currentThread().getName() + " exiting as no tickets are available.");
                Loging.log(Thread.currentThread().getName() + " exiting as no tickets are available.");
                break;
            }

            try {
                // Sleep based on the customer retrieval rate from the configuration
                Thread.sleep(config.getCustomerRetrievalRate() * 1000L);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted during sleep.");
                Loging.log(Thread.currentThread().getName() + " was interrupted during sleep.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}
