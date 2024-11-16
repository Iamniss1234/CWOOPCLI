package org.example;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The TicketPool class represents a singleton pool of tickets with thread-safe methods for
 * adding and removing tickets. It also tracks the total number of tickets released and purchased.
 * This class uses a ReentrantLock to manage concurrent access to ticket operations.
 */
public class TicketPool {

    /** The singleton instance of the TicketPool. */
    private static TicketPool instance;

    /** The current number of available tickets in the pool. */
    private int availableTickets;

    /** The maximum ticket capacity of the pool. */
    private final int maxTicketCapacity;

    /** Lock to ensure thread-safe access to ticket operations. */
    private final ReentrantLock lock = new ReentrantLock();

    /** The total number of tickets released by vendors. */
    private int totalTicketsReleased = 0;

    /** The total number of tickets purchased by customers. */
    private int totalTicketsPurchased = 0;

    /**
     * Private constructor to initialize the TicketPool with the initial ticket count and maximum capacity.
     * This constructor is private to enforce the singleton pattern.
     *
     * @param initialTickets The initial number of tickets available.
     * @param maxTickets The maximum capacity of tickets in the pool.
     */
    private TicketPool(int initialTickets, int maxTickets) {
        this.availableTickets = initialTickets;
        this.maxTicketCapacity = maxTickets;
    }

    /**
     * Returns the singleton instance of TicketPool. If the instance is null, it initializes it
     * with the specified initial ticket count and maximum capacity.
     *
     * @param initialTickets The initial number of tickets available.
     * @param maxTickets The maximum capacity of tickets in the pool.
     * @return The singleton instance of TicketPool.
     */
    public static synchronized TicketPool getInstance(int initialTickets, int maxTickets) {
        if (instance == null) {
            instance = new TicketPool(initialTickets, maxTickets);
        }
        return instance;
    }

    /**
     * Adds a ticket to the pool, provided the current count is below the maximum ticket capacity.
     * Increments the total tickets released count.
     *
     * @param vendorID The ID of the vendor adding the ticket.
     */
    public void addTickets(String vendorID) {
        lock.lock();
        try {
            if (availableTickets < maxTicketCapacity) {
                availableTickets++;
                totalTicketsReleased++;  // Increment the released counter
                System.out.println(vendorID + " added ticket. Total now: " + availableTickets);
            } else {
                System.out.println("Max tickets reached.");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes a ticket from the pool for a customer, provided there are tickets available.
     * Increments the total tickets purchased count.
     *
     * @param customerId The ID of the customer purchasing the ticket.
     */
    public void removeTicket(String customerId) {
        lock.lock();
        try {
            if (availableTickets > 0) {
                availableTickets--;
                totalTicketsPurchased++;  // Increment the purchased counter
                System.out.println(customerId + " purchased a ticket - tickets remaining " + availableTickets);
            } else {
                System.out.println("No tickets available for customer to purchase.");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the current number of available tickets in the pool.
     *
     * @return The current number of available tickets.
     */
    public int getAvailableTickets() {
        return availableTickets;
    }

    /**
     * Returns the maximum ticket capacity of the pool.
     *
     * @return The maximum ticket capacity.
     */
    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    /**
     * Returns the total number of tickets released by vendors.
     *
     * @return The total tickets released.
     */
    public int getTotalTicketsReleased() {
        return totalTicketsReleased;
    }

    /**
     * Returns the total number of tickets purchased by customers.
     *
     * @return The total tickets purchased.
     */
    public int getTotalTicketsPurchased() {
        return totalTicketsPurchased;
    }
}
