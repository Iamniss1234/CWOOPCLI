package org.example;

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

        // Continuously try to remove a ticket as long as there are available tickets and the thread is not interrupted
        while (!Thread.currentThread().isInterrupted() && pool.getAvailableTickets() > 0) {
            pool.removeTicket(Thread.currentThread().getName());
            try {
                // Sleep based on the customer retrieval rate from the configuration
                Thread.sleep(config.getCustomerRetrievalRate() * 1000L);
            } catch (InterruptedException e) {
                // Restore the interrupted status and exit if interrupted
                Thread.currentThread().interrupt();
            }
        }
    }
}
