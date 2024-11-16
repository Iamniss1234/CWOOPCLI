package org.example;

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

        // Continue adding tickets until the pool reaches maximum capacity or the thread is interrupted
        while (!Thread.currentThread().isInterrupted() && pool.getAvailableTickets() < pool.getMaxTicketCapacity()) {
            pool.addTickets(Thread.currentThread().getName());

            try {
                // Sleep based on the configured ticket release rate
                Thread.sleep(config.getTicketReleaseRate() * 1000L);
            } catch (InterruptedException e) {
                // Re-interrupt the thread to preserve the interrupted status
                Thread.currentThread().interrupt();
            }
        }
    }
}
